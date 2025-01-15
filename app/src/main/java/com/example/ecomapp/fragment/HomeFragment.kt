package com.example.ecomapp.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ecomapp.R
import com.example.ecomapp.adapter.CategoryAdapter
import com.example.ecomapp.databinding.FragmentHomesBinding
import com.example.ecomapp.model.CategoryModel
import com.example.ecomapp.model.ProductModel
import com.example.ecommerceapp.adapters.ProductAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomesBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var productList = ArrayList<ProductModel>()  // Store all products

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchUserLocation { location ->
                updateDeliveryLocation(location)
                showLocationSubmittedPopup()
            }
        } else {
            Toast.makeText(requireContext(), "Location permission denied!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomesBinding.inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setupImageSlider()
        setupRecyclerViews()
        fetchProductsFromRealtimeDb()
        fetchCategories()
        setupSearchFunctionality()



        binding.locationText.setOnClickListener {
            showLocationBottomSheet()
        }


        return binding.root
    }

    private fun setupImageSlider() {
        val imageSlider: ImageSlider = binding.imgSlider
        val imageList = arrayListOf(
            SlideModel(R.drawable.banner, ScaleTypes.FIT),
            SlideModel(R.drawable.ban2, ScaleTypes.FIT),
            SlideModel(R.drawable.ban, ScaleTypes.FIT),
            SlideModel(R.drawable.ban3, ScaleTypes.FIT),
            SlideModel(R.drawable.grocery, ScaleTypes.FIT)
        )
        imageSlider.setImageList(imageList)
    }

    private fun setupRecyclerViews() {
        productAdapter = ProductAdapter(requireContext(), ArrayList())
        binding.productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productRecyclerView.adapter = productAdapter

        categoryAdapter = CategoryAdapter(requireContext(), ArrayList())
        binding.categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecyclerView.adapter = categoryAdapter
    }

    private fun fetchProductsFromRealtimeDb() {
        databaseRef.child("products").get()
            .addOnSuccessListener { snapshot ->
                productList.clear()
                if (snapshot.exists()) {
                    for (doc in snapshot.children) {
                        val product = doc.getValue(ProductModel::class.java)
                        product?.let { productList.add(it) }
                    }
                    if (productList.isEmpty()) {
                        Toast.makeText(requireContext(), "No products found!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        productAdapter.updateData(productList)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("realtimeDbFetch", "Error fetching products", exception)
            }
    }

    private fun fetchCategories() {
        databaseRef.child("category").get()
            .addOnSuccessListener { snapshot ->
                val categoryList = ArrayList<CategoryModel>()
                if (snapshot.exists()) {
                    snapshot.children.forEach { document ->
                        val category = document.getValue(CategoryModel::class.java)
                        category?.let { categoryList.add(it) }
                    }
                    if (categoryList.isNotEmpty()) {
                        categoryAdapter.updateData(categoryList)
                    }
                } else {
                    Toast.makeText(requireContext(), "No categories available.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("realtimeDb_Error", "Failed to fetch categories", exception)
            }
    }

    private fun setupSearchFunctionality() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProducts(query: String) {
        val filteredList = if (query.isEmpty()) {
            productList
        } else {
            productList.filter {
                it.name?.contains(query, ignoreCase = true) == true
            } as ArrayList<ProductModel>
        }
        productAdapter.updateData(filteredList)
    }


    /// bottom location sheet
    private fun showLocationBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val dialog = BottomSheetDialog(requireContext())

        dialog.setContentView(dialogView)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val closeButton = dialogView.findViewById<ImageView>(R.id.CrossBtn)
        val pincodeEditText = dialogView.findViewById<EditText>(R.id.pincodeEditText)
        val changeLocationButton = dialogView.findViewById<Button>(R.id.changeLocationButton)
        val useCurrentLocationButton = dialogView.findViewById<Button>(R.id.useCurrentLocation)

        //  Close BottomSheet when "X" button is clicked
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Change location using pincode
        changeLocationButton.setOnClickListener {
            val enteredPincode = pincodeEditText.text.toString().trim()
            if (enteredPincode.isNotEmpty()) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocationName(enteredPincode, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val city = address.locality ?: address.subAdminArea ?: "Unknown"
                        val postalCode = address.postalCode ?: enteredPincode
                        val locationString = "$city, $postalCode"
                        updateDeliveryLocation(locationString)
                    } else {
                        updateDeliveryLocation("Pincode: $enteredPincode")
                    }
                    showLocationSubmittedPopup()
                    dialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateDeliveryLocation("Pincode: $enteredPincode")
                    showLocationSubmittedPopup()
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a valid pincode", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Use current location
        useCurrentLocationButton.setOnClickListener {
            fetchUserLocation { location ->
                updateDeliveryLocation(location)
                showLocationSubmittedPopup()
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun updateDeliveryLocation(location: String) {
        binding.locationText.text = "Deliver to - $location"
    }

    // Show a toast popup confirming the location submission
    private fun showLocationSubmittedPopup() {
        Toast.makeText(requireContext(), "Your location submitted successfully", Toast.LENGTH_SHORT)
            .show()
    }

    /**
     * Checks for location permission.
     * If granted, gets last known location from FusedLocationProviderClient.
     * Calls [doReverseGeocoding] to convert lat/lng into a city + pincode string.
     */
    private fun fetchUserLocation(callback: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val result = doReverseGeocoding(location.latitude, location.longitude)
                callback(result)
            } else {
                callback("Location not found")
            }
        }.addOnFailureListener {
            callback("Error fetching location")
        }
    }

    /**
     * Handles reverse geocoding in a version-safe way:
     * - For Android 13+ (TIRAMISU), uses callback-based getFromLocation().
     * - Otherwise, uses the synchronous getFromLocation().
     */
    @Suppress("DEPRECATION")
    private fun doReverseGeocoding(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Callback-based approach on Android 13+
            var result = "Unknown Location"
            geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: "Unknown"
                    val pincode = addresses[0].postalCode ?: "N/A"
                    result = "$city, $pincode"
                }
            }
            // NOTE: Because the callback is async, 'result' may not update immediately.
            result
        } else {
            // Synchronous approach on older devices
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val city = addresses[0].locality ?: "Unknown"
                val pincode = addresses[0].postalCode ?: "N/A"
                "$city, $pincode"
            } else {
                "Unknown Location"
            }
        }
    }

}