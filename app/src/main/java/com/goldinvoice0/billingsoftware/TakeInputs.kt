package com.goldinvoice0.billingsoftware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentTakeInputsBinding
import kotlinx.coroutines.launch

class TakeInputs : Fragment() {

    private var _binding: FragmentTakeInputsBinding? = null
    private val binding get() = _binding!!
    private val shopViewModel: ShopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTakeInputsBinding.inflate(inflater, container, false)


        binding.buttonSaveDetails.setOnClickListener {
            if (binding.shopName.text.toString().isEmpty()) {
                binding.shopName.error = "required"
            }
            if (binding.address1.text.toString().isEmpty()) {
                binding.address1.error = "required"
            }
            if (binding.address2.text.toString().isEmpty()) {
                binding.address2.error = "required"
            }
            if (binding.gstNumber.text.toString().isEmpty()) {
                binding.gstNumber.error = "required"
            }

            val list = listOf(
                binding.shopName,
                binding.address1,
                binding.address2,
                binding.gstNumber
            )
            val isAnyEditTextEmpty = list.any { it.text.toString().trim().isEmpty() }

            if (!isAnyEditTextEmpty) {
                binding.buttonSaveDetails.isEnabled = false // Disable to prevent multiple clicks
                lifecycleScope.launch {

                    val shop = Shop(
                        shopName = binding.shopName.text.toString(),
                        shopOwner = binding.userName.text.toString(),
                        address1 = binding.address1.text.toString(),
                        address2 = binding.address2.text.toString(),
                        gstNumber = binding.gstNumber.text.toString()
                    )
                    shopViewModel.insert(shop)

                    saveAuthenticationStatus()
                    Log.d("TakeInputs", "Navigating to main screen...")
                    findNavController().navigate(R.id.action_takeInputs_to_signatureFragement)
                    binding.buttonSaveDetails.isEnabled = true // Re-enable after completion
                }
            }
        }

        return binding.root
    }



    private fun saveAuthenticationStatus() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("authenticated", true)
        editor.apply()
        Log.d("TakeInputs", "Authentication status saved successfully")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
