package com.example.ecomapp.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecomapp.R
import com.example.ecomapp.databinding.ActivityCheckoutBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Razorpay Preload
        Checkout.preload(applicationContext)

        // Set click listener for payment
        binding.btnConfirmOrder.setOnClickListener {
            startPayment()
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_tH7nTQabmzh9IE")

        try {
            val options = JSONObject()
            options.put("name", "Urbanlook")
            options.put("description", "Order Payment")
            options.put("currency", "INR")
            options.put("amount", 500000) // ₹100.00 (100 * 100 Paise)
            options.put("prefill.email", "customer@example.com")
            options.put("prefill.contact", "9876543210")

            checkout.open(this, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment Successful! Payment ID: $razorpayPaymentId", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Toast.makeText(this, "Payment Failed! Error: $response", Toast.LENGTH_LONG).show()
    }
}
