package com.goldinvoice0.billingsoftware

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.goldinvoice0.billingsoftware.databinding.DialogCustomerInputBinding

class CustomerInputDialog : DialogFragment() {
    private var _binding: DialogCustomerInputBinding? = null
    private val binding get() = _binding!!

    interface OnCustomerInputListener {
        fun onCustomerInput(name: String, contact: String, address: String)
    }

    private var listener: OnCustomerInputListener? = null

    // Companion object for creating dialog instance with data
    companion object {
        private const val ARG_CUSTOMER_NAME = "customer_name"
        private const val ARG_CUSTOMER_CONTACT = "customer_contact"

        fun newInstance(customerName: String? = null, customerContact: String? = null): CustomerInputDialog {
            return CustomerInputDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_CUSTOMER_NAME, customerName)
                    putString(ARG_CUSTOMER_CONTACT, customerContact)
                }
            }
        }
    }

    fun setOnCustomerInputListener(listener: OnCustomerInputListener) {
        this.listener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (listener == null) {
            listener = try {
                context as OnCustomerInputListener
            } catch (e: ClassCastException) {
                throw RuntimeException("$context must implement OnCustomerInputListener")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCustomerInputBinding.inflate(inflater, container, false)

        // Pre-fill data if available from arguments
        arguments?.let { bundle ->
            bundle.getString(ARG_CUSTOMER_NAME)?.let { name ->
                binding.etCustomerName.setText(name)
            }
            bundle.getString(ARG_CUSTOMER_CONTACT)?.let { contact ->
                binding.etCustomerContact.setText(contact)
            }
        }

        binding.btnSubmit.setOnClickListener {
            validateAndSubmit()
        }

        return binding.root
    }

    private fun validateAndSubmit() {
        val name = binding.etCustomerName.text.toString().trim()
        val contact = binding.etCustomerContact.text.toString().trim()
        val address = binding.etCustomerAddress.text.toString().trim()

        if (name.isEmpty()) {
            binding.etCustomerName.error = "Name is required"
            return
        }
        if (contact.isEmpty()) {
            binding.etCustomerContact.error = "Contact is required"
            return
        }
        if (address.isEmpty()) {
            binding.etCustomerAddress.error = "Address is required"
            return
        }

        // Pass data to the listener
        listener?.onCustomerInput(name, contact, address)
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}