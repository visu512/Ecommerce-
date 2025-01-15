package com.example.ecomapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecomapp.activity.CheckoutActivity
import com.example.ecomapp.adapter.CartAdapter
import com.example.ecomapp.databinding.FragmentCartsBinding
import com.example.ecomapp.roomdb.AppDatabase
import com.example.ecomapp.roomdb.ProductModelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartsBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartList = mutableListOf<ProductModelEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartsBinding.inflate(inflater, container, false)

        // Handle Back Button Press to Navigate Back to ProfileFragment
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val fragmentManager = requireActivity().supportFragmentManager
                    if (fragmentManager.backStackEntryCount > 0) {
                        fragmentManager.popBackStack() // Go back to ProfileFragment
                    } else {
                        requireActivity().finish() // Close app if no fragments left
                    }
                }
            }
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadCartData()

        binding.backCartBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnBuy.setOnClickListener {
            if (cartList.isEmpty()) {
                showToast("Your cart is empty!")
            } else {
                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadCartData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(requireContext()).productDao()
            dao.getAllProducts().collect { dbItems ->
                cartList.clear()
                cartList.addAll(dbItems)

                withContext(Dispatchers.Main) {
                    cartAdapter.notifyDataSetChanged()
                    updateTotalPrice()
                    checkCartEmpty() // Check if cart is empty
                }
            }
        }
    }

    private fun updateTotalPrice() {
        val total = cartAdapter.getTotalPrice()
        binding.tvTotalPrice.text = "Total: ₹${String.format("%.2f", total)}"
    }

    private fun checkCartEmpty() {
        if (cartList.isEmpty()) {
            binding.emptyCartText.visibility = View.VISIBLE
            binding.cartRecyclerView.visibility = View.GONE
            binding.btnBuy.visibility = View.GONE
        } else {
            binding.emptyCartText.visibility = View.GONE
            binding.cartRecyclerView.visibility = View.VISIBLE
            binding.btnBuy.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(requireContext(), cartList) { updateTotalPrice() }
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
