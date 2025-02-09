package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.PaymentAdapter
import com.goldinvoice0.billingsoftware.Model.Payment
import com.goldinvoice0.billingsoftware.Model.PaymentType
import com.goldinvoice0.billingsoftware.databinding.FragmentOrderInputContainerBinding


class OrderInputContainer : Fragment() {
    private var _binding: FragmentOrderInputContainerBinding? = null
    private val binding get() = _binding!!

    private var payments = mutableListOf<Payment>()
    private val paymentAdapter = PaymentAdapter(
        onDeleteClick = { item, position ->
            handleDelete(item, position)
        }
    )
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderInputContainerBinding.inflate(inflater, container, false)


        setupPaymentTypeDropdown()
        setupRecyclerView()
        setupAddPaymentButton()


        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Done -> {

                    findNavController().popBackStack()
                    true
                }

                else -> false
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        if (arguments?.getBoolean("EditAdvancePayment") == true) {
            sharedViewModel.payments.removeObservers(viewLifecycleOwner) // Prevent multiple observers
            sharedViewModel.payments.observe(viewLifecycleOwner) { paymentsList ->
                payments.clear()
                payments.addAll(paymentsList) // Avoid referencing the same list
                paymentAdapter.submitList(payments.toList()) // Ensure a new list is created
                updateTotal()
            }
        }
    }


    private fun handleDelete(item: Payment, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Payment")
            .setMessage("Are you sure you want to delete this payment?")
            .setPositiveButton("Delete") { _, _ ->
                val updatedPayments = payments.toMutableList() // Create a new list
                updatedPayments.removeAt(position)
                payments = updatedPayments // Update reference
                paymentAdapter.submitList(payments.toList()) // Ensure adapter gets a fresh list
                sharedViewModel.removePayment(position)
                updateTotal()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun setupPaymentTypeDropdown() {
        val paymentTypes = arrayOf("Gold", "Silver", "Payment")
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            paymentTypes
        )
        binding.paymentTypeDropdown.setAdapter(adapter)

        binding.paymentTypeDropdown.setOnItemClickListener { _, _, position, _ ->
            showInputCard(paymentTypes[position])
        }
    }

    private fun showInputCard(type: String) {
        // Hide all cards first
        binding.goldInputCard.visibility = View.GONE
        binding.silverInputCard.visibility = View.GONE
        binding.paymentInputCard.visibility = View.GONE

        // Show selected card
        when (type) {
            "Gold" -> binding.goldInputCard.visibility = View.VISIBLE
            "Silver" -> binding.silverInputCard.visibility = View.VISIBLE
            "Payment" -> binding.paymentInputCard.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        binding.paymentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = paymentAdapter
        }
    }

    private fun setupAddPaymentButton() {
        binding.addPaymentButton.setOnClickListener {
            val selectedType = binding.paymentTypeDropdown.text.toString()
            if (validateInputs(selectedType)) {
                addPayment(selectedType)
            }
        }
    }

    private fun validateInputs(type: String): Boolean {
        return when (type) {
            "Gold" -> validateGoldInputs()
            "Silver" -> validateSilverInputs()
            "Payment" -> validatePaymentInputs()
            else -> false
        }
    }

    private fun validateGoldInputs(): Boolean {
        val weight = binding.goldWeightInput.text.toString()
        val rate = binding.goldRateInput.text.toString()

        if (weight.isEmpty() || rate.isEmpty()) {
            showError("Please fill all fields")
            return false
        }

        return true
    }

    private fun validateSilverInputs(): Boolean {
        val weight = binding.silverWeightInput.text.toString()
        val rate = binding.silverRateInput.text.toString()

        if (weight.isEmpty() || rate.isEmpty()) {
            showError("Please fill all fields")
            return false
        }

        return true
    }

    private fun validatePaymentInputs(): Boolean {
        val amount = binding.paymentAmountInput.text.toString()

        if (amount.isEmpty()) {
            showError("Please enter amount")
            return false
        }

        return true
    }

    private fun addPayment(type: String) {
        val payment = when (type) {
            "Gold" -> createGoldPayment()
            "Silver" -> createSilverPayment()
            "Payment" -> createCashPayment()
            else -> null
        }

        payment?.let {
            val updatedPayments =
                payments.toMutableList() // Create new list to avoid reference issue
            updatedPayments.add(it)
            payments = updatedPayments // Assign new list
            paymentAdapter.submitList(payments.toList()) // Adapter gets a new list reference
            sharedViewModel.addPayment(payment) // Update ViewModel
            updateTotal()
            clearInputs()
        }
    }


    private fun createGoldPayment(): Payment {
        val weight = binding.goldWeightInput.text.toString().toDouble()
        val rate = binding.goldRateInput.text.toString().toDouble()
        return Payment(
            type = PaymentType.GOLD,
            weight = weight,
            rate = rate,
            value = weight * rate,
            date = System.currentTimeMillis()
        )
    }

    private fun createSilverPayment(): Payment {
        val weight = binding.silverWeightInput.text.toString().toDouble()
        val rate = binding.silverRateInput.text.toString().toDouble()
        return Payment(
            type = PaymentType.SILVER,
            weight = weight,
            rate = rate,
            value = weight * rate,
            date = System.currentTimeMillis()
        )
    }

    private fun createCashPayment(): Payment {
        val amount = binding.paymentAmountInput.text.toString().toDouble()
        return Payment(
            type = PaymentType.CASH,
            value = amount,
            date = System.currentTimeMillis()
        )
    }

    private fun updateTotal() {
        val total = payments.sumOf { it.value }
        binding.totalAdvanceText.text = "₹${total.format(2)}"
    }

    private fun clearInputs() {
        binding.goldWeightInput.text?.clear()
        binding.goldRateInput.text?.clear()
        binding.silverWeightInput.text?.clear()
        binding.silverRateInput.text?.clear()
        binding.paymentAmountInput.text?.clear()
        binding.paymentTypeDropdown.text?.clear()
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

// Extension function for number formatting
fun Double.format(digits: Int) = "%.${digits}f".format(this)