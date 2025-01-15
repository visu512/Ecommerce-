package com.example.ecomapp.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ecomapp.R
import com.example.ecomapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController  //  Define NavController globally

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find NavController safely
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController
            binding.bottomBar.setupWithNavController(navController)
        }

        // Hide Bottom Navigation when keyboard is open
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = binding.root.rootView.height - binding.root.height
            binding.bottomBar.visibility = if (heightDiff > 300) View.GONE else View.VISIBLE
        }

        // Hide keyboard when clicking outside
        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
    }

    //  Improved back press handling
    override fun onBackPressed() {
        if (!::navController.isInitialized) {
            super.onBackPressed()
            return
        }

        //  for HomeFragment
        if (navController.currentDestination?.id == R.id.homes_Fragment) {  // ⬅ Check your navigation.xml ID
            showExitDialog()
        } else if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }

    //  Show exit dialog
    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to exit the app?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    // Hide keyboard function
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
