package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.ReceviedPaymentAdapter
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.goldinvoice0.billingsoftware.Model.RecivedPaymentType
import com.goldinvoice0.billingsoftware.databinding.FragmentPaymentEntryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DecimalFormat

class paymentEntry : Fragment() {
    private var _binding: FragmentPaymentEntryBinding? = null
    private val binding get() = _binding!!

    private val paymentAdapter = ReceviedPaymentAdapter(
        onDeleteClick = { payment ->
            if (isAdded && !isDetached) {
                handlePaymentDelete(payment)
            }
        }
    )
    private var totalAmount: Int = 0
    private var receivedAmount: Int = 0
    private var extraCharges: Int = 0
    private val payments = mutableListOf<PaymentRecived>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var isProcessingUpdate = false  // Add this flag


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentEntryBinding.inflate(inflater, container, false)

        arguments?.getInt("totalAmount", 0)?.let { amount ->
            totalAmount = amount
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Done -> {
                    if (isAdded && !isDetached) {
                        findNavController().popBackStack()
                    }
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        updateSummaryCard()
        setupInitialData()
    }

    private fun setupViews() {
        _binding?.apply {
            rvPayments.apply {
                adapter = paymentAdapter
                layoutManager = LinearLayoutManager(context)
            }

            fabAddPayment.setOnClickListener {
                if (isAdded && !isDetached && _binding != null) {
                    showPaymentEntryBottomSheet()
                }
            }
        }
    }

    private fun setupInitialData() {
        if (arguments?.getBoolean("EditPayment") == true) {
            sharedViewModel.paymentEntry.observe(viewLifecycleOwner) { paymentsList ->
                if (_binding == null || isProcessingUpdate) return@observe

                try {
                    isProcessingUpdate = true

                    payments.clear()
                    payments.addAll(paymentsList)
                    paymentAdapter.submitList(payments.toList())

                    // Reset counters
                    receivedAmount = 0
                    extraCharges = 0

                    // Recalculate totals
                    paymentsList.forEach { payment ->
                        when (payment.type) {
                            RecivedPaymentType.EXTRA_CHARGE -> extraCharges += payment.amount
                            RecivedPaymentType.RECEIVED -> receivedAmount += payment.amount
                            else -> {}
                        }
                    }

                    updateSummaryCard()
                } finally {
                    isProcessingUpdate = false
                }
            }
        }
    }

    private fun addPayment(payment: PaymentRecived) {
        if (_binding == null) return

        try {
            isProcessingUpdate = true
            payments.add(payment)
            sharedViewModel.addPaymentRecived(payment)
            paymentAdapter.submitList(payments.toList())

            when (payment.type) {
                RecivedPaymentType.RECEIVED -> receivedAmount += payment.amount
                RecivedPaymentType.EXTRA_CHARGE -> extraCharges += payment.amount
                else -> {}
            }
            updateSummaryCard()
        } finally {
            isProcessingUpdate = false
        }
    }

    private fun updateSummaryCard() {
        _binding?.apply {
            tvTotalAmount.text = formatCurrency(totalAmount)

            extraChargesSection.visibility = if (extraCharges > 0) View.VISIBLE else View.GONE
            if (extraCharges > 0) {
                tvExtraCharges.text = "+ ${formatCurrency(extraCharges)}"
            }

            tvReceivedAmount.text = "- ${formatCurrency(receivedAmount)}"

            val balanceDue = totalAmount + extraCharges - receivedAmount
            tvBalanceAmount.text = formatCurrency(balanceDue)

            if (isAdded && !isDetached) {
                tvBalanceAmount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (balanceDue <= 0) R.color.success_color else R.color.text_primary
                    )
                )
            }
        }
    }

    private fun showPaymentEntryBottomSheet() {
        if (!isAdded || isDetached) return

        PaymentBottomSheetFragment().apply {
            setTotalAmount(totalAmount + extraCharges - receivedAmount)
            setOnPaymentAddedListener { payment ->
                if (this@paymentEntry.isAdded && !this@paymentEntry.isDetached) {

                    addPayment(payment)
                }
            }
        }.show(childFragmentManager, "PaymentEntry")
    }

    private fun handlePaymentDelete(payment: PaymentRecived) {
        if (!isAdded || isDetached) return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Payment")
            .setMessage("Are you sure you want to delete this payment?")
            .setPositiveButton("Delete") { _, _ ->
                deletePayment(payment)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun deletePayment(payment: PaymentRecived) {
        if (_binding == null) return

        try {
            isProcessingUpdate = true
            when (payment.type) {
                RecivedPaymentType.RECEIVED -> receivedAmount -= payment.amount
                RecivedPaymentType.EXTRA_CHARGE -> extraCharges -= payment.amount
                else -> {

                }
            }

            payments.remove(payment)
            sharedViewModel.deletePaymentRecived(payment)
            paymentAdapter.submitList(payments.toList())
            updateSummaryCard()
        } finally {
            isProcessingUpdate = false
        }
    }
    private fun formatCurrency(amount: Int): String {
        return "₹${formatNumberToIndian(amount.toLong())}"
    }

    private fun formatNumberToIndian(number: Long): String {
        return DecimalFormat("##,##,###").format(number)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}