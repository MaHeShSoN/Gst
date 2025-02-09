package com.goldinvoice0.billingsoftware

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldinvoice0.billingsoftware.databinding.FragmentSignatureFragementBinding
import java.io.File


class SignatureFragement : Fragment() {



    private var _binding: FragmentSignatureFragementBinding? = null
    private val binding get() = _binding!!
    private var signatureSaved: Boolean = false // Tracks if the signature is saved
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignatureFragementBinding.inflate(inflater, container, false)

        binding.clearsign.setOnClickListener {
            binding.signaturePad.clear()
        }

        // Load the saved signature
        val file = File(requireContext().filesDir, "signature.png")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.signaturePad.signatureBitmap = bitmap
        }

        binding.saveSign.setOnClickListener{
            if (binding.signaturePad.isEmpty) {
                Toast.makeText(context, "Please sign before saving.", Toast.LENGTH_SHORT).show()
            } else {
                val signatureBitmap = binding.signaturePad.signatureBitmap
                saveSignatureToFile(signatureBitmap)
            }
        }


            // Inflate the layout for this fragment
        return binding.root
    }

    private fun saveSignatureToFile(bitmap: Bitmap) {
        try {
            val file = File(requireContext().filesDir, "signature.png")
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                Toast.makeText(requireContext(), "Signature saved successfully.", Toast.LENGTH_SHORT).show()
                signatureSaved = true // Set saved status
                findNavController().navigate(R.id.action_signatureFragement_to_mainScreen)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save signature: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}