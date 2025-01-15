//package com.example.ecomapp.fragment
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.activity.OnBackPressedCallback
//import androidx.fragment.app.Fragment
//import com.example.ecomapp.R
//import com.example.ecomapp.databinding.FragmentProfilesBinding
//
//class ProfileFragment : Fragment() {
//
//    private lateinit var binding: FragmentProfilesBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Initialize View Binding
//        binding = FragmentProfilesBinding.inflate(inflater, container, false)
//
//        // Navigate to CartFragment
//        binding.cartBtn.setOnClickListener {
//            val cartFragment = CartFragment()
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, cartFragment) // Replace with CartFragment
//                .addToBackStack(null) // Ensure it can go back
//                .commit()
//        }
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Handle Back Button Press to Navigate Back to ProfileFragment
//        requireActivity().onBackPressedDispatcher.addCallback(
//            viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
//                        requireActivity().supportFragmentManager.popBackStack() // Go back to previous fragment
//                    } else {
//                        requireActivity().finish() // Exit app if no fragments left
//                    }
//                }
//            }
//        )
//    }
//}


package com.example.ecomapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.ecomapp.R
import com.example.ecomapp.databinding.FragmentProfilesBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfilesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize View Binding
        binding = FragmentProfilesBinding.inflate(inflater, container, false)

        // Navigate to CartFragment when clicking on "My Cart"
        binding.cartBtn.setOnClickListener {
            val cartFragment = CartFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, cartFragment) // Replace current fragment
                .addToBackStack("ProfileFragment") //  Correctly add to back stack
                .commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle Back Button Press to Navigate Back Properly
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val fragmentManager = requireActivity().supportFragmentManager
                    if (fragmentManager.backStackEntryCount > 0) {
                        fragmentManager.popBackStack() // ✅ Go back to previous fragment
                    } else {
                        requireActivity().finish() // Close app if no fragments left
                    }
                }
            }
        )
    }
}
