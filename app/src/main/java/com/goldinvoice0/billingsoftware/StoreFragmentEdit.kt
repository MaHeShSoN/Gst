package com.goldinvoice0.billingsoftware

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentStoreEditBinding
import java.io.File


class StoreFragmentEdit : Fragment() {

    private var _binding: FragmentStoreEditBinding? = null
    private val binding get() = _binding!!
    private val shopViewModel: ShopViewModel by viewModels()
    private lateinit var shop: Shop
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoreEditBinding.inflate(inflater, container, false)

        shopViewModel.shop.observe(viewLifecycleOwner) { shop1 ->
            shop = shop1
            binding.userName.setText(shop1.shopOwner)
            binding.address1.setText(shop1.address1)
            binding.address2.setText(shop1.address2)
            binding.gstNumber.setText(shop1.gstNumber)
            binding.shopName.setText(shop1.shopName)
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.buttonSubmit -> {
                    // Retrieve input data
                    val shopOwner = binding.userName.text.toString()
                    val shopName = binding.shopName.text.toString()
                    val address1 = binding.address1.text.toString()
                    val address2 = binding.address2.text.toString()
                    val gstNumber = binding.gstNumber.text.toString()

                    // Validate required fields
                    if (shopOwner.isBlank() || shopName.isBlank() || address1.isBlank() || address2.isBlank() || gstNumber.isBlank()) {
                        Toast.makeText(
                            requireContext(),
                            "Please fill all required fields.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val newShop = Shop(
                            shop.id,
                            binding.shopName.text.toString(),
                            binding.userName.text.toString(),
                            binding.address1.text.toString(),
                            binding.address2.text.toString(),
                            binding.gstNumber.text.toString()
                        )
                        shopViewModel.update(newShop)
                        findNavController().popBackStack()

                    }
                    true
                }

                else -> false
            }
        }


        // Load the saved signature
        val file = File(requireContext().filesDir, "signature.png")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.signaturePad.signatureBitmap = bitmap
        }

        binding.signaturePad.setOnTouchListener { _, _ ->

            true }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.edit.setOnClickListener {
            findNavController().navigate(R.id.action_storeFragmentEdit_to_signatureFragement)
        }

        binding.fabAddSign.setOnClickListener {
            findNavController().navigate(R.id.action_storeFragmentEdit_to_signatureFragement)
        }

        binding.clear.setOnClickListener {
            deleteSignatureFile()
            binding.signaturePad.clear()
            binding.fabAddSign.visibility = View.VISIBLE

        }

        // Inflate the layout for this fragment
        return binding.root
    }
    private fun deleteSignatureFile() {
        val file = File(requireContext().filesDir, "signature.png")
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(requireContext(), "Signature deleted successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete the signature.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No signature found to delete.", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onStart() {
        super.onStart()
        val file = File(requireContext().filesDir, "signature.png")
        if(file.exists()) {
            binding.fabAddSign.visibility = View.GONE
        }else {
            binding.fabAddSign.visibility = View.VISIBLE
        }
    }

}