package com.goldinvoice0.billingsoftware

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldinvoice0.billingsoftware.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {


    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            // Check if user is signed in (non-null) and update UI accordingly.
            val authenticated = sharedPreferences.getBoolean("authenticated", false)
            Log.d("mahesh0", "Authenticated value: $authenticated")
            if (authenticated) {
                findNavController().navigate(R.id.action_splashFragment_to_mainScreen)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_takeInputs)
            }
        }, 3000) // 3000 milliseconds = 3 seconds
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}