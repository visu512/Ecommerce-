package com.example.ecomapp.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ecomapp.databinding.ActivityProductDetailsBinding
import com.example.ecomapp.roomdb.AppDatabase
import com.example.ecomapp.roomdb.ProductDao
import com.example.ecomapp.roomdb.ProductModelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Set bottom Navigation Bar color to white
//        window.navigationBarColor = Color.WHITE
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        // Get Data from Intent
        val name = intent.getStringExtra("productName")
        val desc = intent.getStringExtra("productDesc")
        val price = intent.getStringExtra("productPrice")

        // Set Data to Views
        binding.text1.text = name
        binding.text2.text = desc
        binding.text3.text = "₹$price"

        // Load multiple images into the ImageSlider
        loadProductImages()

        // Handle Cart Actions
        handleCartActions(name, desc, price)
    }

    private fun loadProductImages() {
        val imageList = ArrayList<SlideModel>()

        // Get images array from Intent
        val images = intent.getStringArrayListExtra("productImages")
        val coverImageUrl = intent.getStringExtra("coverImageUrl")

        if (images != null && images.isNotEmpty()) {
            for (imageUrl in images) {
                imageList.add(SlideModel(imageUrl, ""))
            }
        } else if (!coverImageUrl.isNullOrEmpty()) {
            // If no additional images, use cover image
            imageList.add(SlideModel(coverImageUrl, "Cover Image"))
        }

        // Debugging - Log the Image List
        Log.d("ProductDetails", "Loaded Images: ${imageList.map { it.imageUrl }}")

        // Load images into ImageSlider
        binding.imgSlider.setImageList(imageList)
    }

    private fun handleCartActions(name: String?, desc: String?, price: String?) {
        val productDao = AppDatabase.getInstance(this).productDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val isProductExist = name?.let { productDao.isExist(it) } ?: 0

            launch(Dispatchers.Main) {
                binding.pButton.text = if (isProductExist > 0) "ADDED TO CART" else "ADD TO CART"

                binding.pButton.setOnClickListener {
                    if (isProductExist > 0) {
//                        openCart()
                    } else {
                        addToCart(productDao, name, price, desc)
                    }
                }
            }
        }
    }

    private fun addToCart(productDao: ProductDao, name: String?, price: String?, desc: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (name == null || price == null) return@launch // Prevent null values

            val isProductExist = productDao.isExist(name) // Check if product exists

            if (isProductExist == 0) { // Insert only if product is NOT in cart
                val product = ProductModelEntity(
                    productId = name,
                    productName = name,
                    productImage = intent.getStringExtra("coverImageUrl") ?: "",
                    productPrice = price
                )
                productDao.insertProduct(product) // Insert into database

                launch(Dispatchers.Main) {
                    binding.pButton.text = "ADDED TO CART"
//                    Toast.makeText(this@ProductDetailsActivity, "Product added to cart!", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
//
//    private fun openCart() {
//        val preference = getSharedPreferences("info", MODE_PRIVATE)
//        val editor = preference.edit()
//        editor.putBoolean("navigateToCart", true)
//        editor.apply()
//
//        startActivity(Intent(this, MainActivity::class.java))
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(intent)
//        finish()
//    }
}

