package com.goldinvoice0.billingsoftware

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import com.goldinvoice0.billingsoftware.Model.PaymentMethod
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction
import com.goldinvoice0.billingsoftware.databinding.AdvacePaymentInputBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AdvancePaymentInputBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: AdvacePaymentInputBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var paymentAddListener: PaymentAddListener? = null
    private var selectedDate: Long = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPaymentTypeDropdown()
        setupAddPaymentButton()
        setupCalculationListeners()
        setupDatePicker()
    }

    // Double extension for formatting
    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    // Interface for communication
    interface PaymentAddListener {
        fun onPaymentAdded(payment: PaymentTransaction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AdvacePaymentInputBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                setupFullHeight(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = true

            }
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    private fun setupPaymentTypeDropdown() {
        val paymentTypes = PaymentMethod.values().map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            paymentTypes
        ).apply {
            setDropDownViewResource(R.layout.dropdown_item)
        }

        binding.paymentTypeDropdown.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(R.color.white)
        }

        binding.paymentTypeDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedPaymentType = PaymentMethod.values()[position]
            updateInputFieldsVisibility(selectedPaymentType)
        }
    }

    private fun updateInputFieldsVisibility(paymentType: PaymentMethod) {
        // Hide all input cards first
        binding.goldInputCard.visibility = View.GONE
        binding.silverInputCard.visibility = View.GONE
        binding.paymentInputCard.visibility = View.GONE
        binding.upiInputCard.visibility = View.GONE
        binding.bankInputCard.visibility = View.GONE
        binding.checkNumberLayout.visibility = View.GONE

        // Show relevant input fields based on payment type
        when (paymentType) {
            PaymentMethod.GOLD_EXCHANGE -> binding.goldInputCard.visibility = View.VISIBLE
            PaymentMethod.SILVER_EXCHANGE -> binding.silverInputCard.visibility = View.VISIBLE
            PaymentMethod.UPI -> {
                binding.upiInputCard.visibility = View.VISIBLE
                binding.paymentInputCard.visibility = View.VISIBLE
                setupUpiServiceDropdown()
            }

            PaymentMethod.BANK_TRANSFER -> {
                binding.bankInputCard.visibility = View.VISIBLE
                binding.paymentInputCard.visibility = View.VISIBLE
                setupBankNameDropdown()
            }

            PaymentMethod.CHECK, PaymentMethod.DEMAND_DRAFT -> {
                binding.bankInputCard.visibility = View.VISIBLE
                binding.paymentInputCard.visibility = View.VISIBLE
                binding.checkNumberLayout.visibility = View.VISIBLE
                setupBankNameDropdown()
            }

            PaymentMethod.CASH, PaymentMethod.ADVANCE_PAYMENT,
            PaymentMethod.LATE_PAYMENT -> {
                binding.paymentInputCard.visibility = View.VISIBLE
            }

            else -> binding.paymentInputCard.visibility = View.VISIBLE
        }
    }

    private fun setupUpiServiceDropdown() {
        val upiServices = listOf(
            "Google Pay",
            "PhonePe",
            "Paytm",
            "BHIM",
            "SBI YONO",
            "Axis Bank UPI",
            "ICICI Bank iMobile",
            "HDFC Bank PayZapp",
            "Mobikwik",
            "Jio Payments Bank",
            "Airtel Payments Bank",
            "WhatsApp Pay",
            "Amazon Pay",
            "Samsung Pay",
            "Freecharge",
            "Other"
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, upiServices).apply {
            setDropDownViewResource(R.layout.dropdown_item)
        }

        binding.upiServiceDropdown.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(R.color.white)
        }

    }

    private fun setupBankNameDropdown() {
        val banks = listOf(
            "State Bank of India (SBI)",
            "HDFC Bank",
            "Axis Bank",
            "Bank of Baroda (BoB)",
            "ICICI Bank",
            "Punjab National Bank (PNB)",
            "Kotak Mahindra Bank",
            "IndusInd Bank",
            "Central Bank of India",
            "Yes Bank",
            "Union Bank of India",
            "IDBI Bank",
            "Bank of India (BoI)",
            "Canara Bank",
            "Federal Bank",
            "Other"
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, banks).apply {
            setDropDownViewResource(R.layout.dropdown_item)
        }
        binding.bankNameDropdown.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(R.color.white)
        }


    }

    private fun setupCalculationListeners() {
        // For Gold calculation
        val goldTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateGoldValue()
            }
        }
        binding.goldWeightInput.addTextChangedListener(goldTextWatcher)
        binding.goldRateInput.addTextChangedListener(goldTextWatcher)

        // For Silver calculation
        val silverTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateSilverValue()
            }
        }
        binding.silverWeightInput.addTextChangedListener(silverTextWatcher)
        binding.silverRateInput.addTextChangedListener(silverTextWatcher)
    }

    private fun calculateGoldValue() {
        val weight = binding.goldWeightInput.text.toString().toDoubleOrNull() ?: 0.0
        val rate = binding.goldRateInput.text.toString().toDoubleOrNull() ?: 0.0
        val value = weight * rate
        binding.paymentAmountInput.setText(value.format(2))
    }

    private fun calculateSilverValue() {
        val weight = binding.silverWeightInput.text.toString().toDoubleOrNull() ?: 0.0
        val rate = binding.silverRateInput.text.toString().toDoubleOrNull() ?: 0.0
        val value = weight * rate
        binding.paymentAmountInput.setText(value.format(2))
    }

    private fun collectPaymentData(paymentType: PaymentMethod): PaymentTransaction {
        return when (paymentType) {
            PaymentMethod.GOLD_EXCHANGE -> PaymentTransaction(
                type = paymentType,
                amount = binding.paymentAmountInput.text.toString().toDoubleOrNull() ?: 0.0,
                goldWeight = binding.goldWeightInput.text.toString().toDoubleOrNull(),
                goldRate = binding.goldRateInput.text.toString().toDoubleOrNull(),
                date = binding.etDate.text.toString()
            )

            PaymentMethod.SILVER_EXCHANGE -> PaymentTransaction(
                type = paymentType,
                amount = binding.paymentAmountInput.text.toString().toDoubleOrNull() ?: 0.0,
                silverWeight = binding.silverWeightInput.text.toString().toDoubleOrNull(),
                silverRate = binding.silverRateInput.text.toString().toDoubleOrNull(),
                date = binding.etDate.text.toString()

            )

            PaymentMethod.UPI -> PaymentTransaction(
                type = paymentType,
                amount = binding.paymentAmountInput.text.toString().toDoubleOrNull() ?: 0.0,
                upiService = binding.upiServiceDropdown.text.toString(),
                fromUpi = binding.fromUpiInput.text.toString(),
                toUpi = binding.toUpiInput.text.toString(),
                date = binding.etDate.text.toString()
            )

            PaymentMethod.BANK_TRANSFER -> PaymentTransaction(
                type = paymentType,
                amount = binding.paymentAmountInput.text.toString().toDoubleOrNull() ?: 0.0,
                bankName = binding.bankNameDropdown.text.toString(),
                fromName = binding.fromNameInput.text.toString(),
                toName = binding.toNameInput.text.toString(),
                date = binding.etDate.text.toString()
            )

            PaymentMethod.CHECK, PaymentMethod.DEMAND_DRAFT -> PaymentTransaction(
                type = paymentType,
                amount = binding.paymentAmountInput.text.toString().toDoubleOrNull() ?: 0.0,
                bankName = binding.bankNameDropdown.text.toString(),
                fromName = binding.fromNameInput.text.toString(),
                toName = binding.toNameInput.text.toString(),
                checkNumber = binding.checkNumberInput.text.toString(),
                date = binding.etDate.text.toString()
            )

            else -> PaymentTransaction(
                type = paymentType,
                amount = binding.paymentAmountInput.text.toString().toDoubleOrNull() ?: 0.0,
                date = binding.etDate.text.toString()
            )
        }
    }

    private fun validateInputs(paymentType: PaymentMethod): Boolean {
        var isValid = true

        fun showError(inputField: TextInputLayout, errorMessage: String) {
            inputField.error = errorMessage
            isValid = false
        }

        fun clearError(inputField: TextInputLayout) {
            inputField.error = null
        }

        // Validate Amount
        if (binding.paymentAmountInput.text.toString().isBlank()) {
            showError(binding.paymentAmountLayout, "Amount is required")
        } else {
            clearError(binding.paymentAmountLayout)
        }

        // Validate based on payment type
        when (paymentType) {
            PaymentMethod.GOLD_EXCHANGE -> {
                if (binding.goldWeightInput.text.toString().isBlank()) {
                    showError(binding.goldWeightLayout, "Weight is required")
                } else {
                    clearError(binding.goldWeightLayout)
                }

                if (binding.goldRateInput.text.toString().isBlank()) {
                    showError(binding.goldRateLayout, "Rate is required")
                } else {
                    clearError(binding.goldRateLayout)
                }
            }

            PaymentMethod.SILVER_EXCHANGE -> {
                if (binding.silverWeightInput.text.toString().isBlank()) {
                    showError(binding.silverWeightLayout, "Weight is required")
                } else {
                    clearError(binding.silverWeightLayout)
                }

                if (binding.silverRateInput.text.toString().isBlank()) {
                    showError(binding.silverRateLayout, "Rate is required")
                } else {
                    clearError(binding.silverRateLayout)
                }
            }

            PaymentMethod.UPI -> {
                if (binding.fromUpiInput.text.toString().isBlank()) {
                    showError(binding.fromUpiLayout, "Sender UPI ID is required")
                } else {
                    clearError(binding.fromUpiLayout)
                }

                if (binding.toUpiInput.text.toString().isBlank()) {
                    showError(binding.toUpiLayout, "Receiver UPI ID is required")
                } else {
                    clearError(binding.toUpiLayout)
                }
            }

            PaymentMethod.BANK_TRANSFER, PaymentMethod.CHECK, PaymentMethod.DEMAND_DRAFT -> {
                if (binding.bankNameDropdown.text.toString().isBlank()) {
                    showError(binding.bankNameLayout, "Bank Name is required")
                } else {
                    clearError(binding.bankNameLayout)
                }

                if (binding.fromNameInput.text.toString().isBlank()) {
                    showError(binding.fromNameLayout, "Sender Name is required")
                } else {
                    clearError(binding.fromNameLayout)
                }

                if (binding.toNameInput.text.toString().isBlank()) {
                    showError(binding.toNameLayout, "Receiver Name is required")
                } else {
                    clearError(binding.toNameLayout)
                }

                if (paymentType == PaymentMethod.CHECK || paymentType == PaymentMethod.DEMAND_DRAFT) {
                    if (binding.checkNumberInput.text.toString().isBlank()) {
                        showError(binding.checkNumberLayout, "Check Number is required")
                    } else {
                        clearError(binding.checkNumberLayout)
                    }
                }
            }

            else -> {
                // No additional validation needed for CASH or ADVANCE_PAYMENT
            }
        }

        return isValid
    }

    private fun setupDatePicker() {
        // Set initial date
        binding.etDate.setText(
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(selectedDate))
        )

        binding.datePicker.setEndIconOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDate = calendar.timeInMillis
                    binding.etDate.setText(
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(calendar.time)
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAddPaymentButton() {
        binding.addPaymentButton.setOnClickListener {
            val paymentTypeText = binding.paymentTypeDropdown.text.toString()

            if (paymentTypeText.isBlank()) {
                binding.paymentTypeLayout.error = "Please select a payment method"
                return@setOnClickListener
            } else {
                binding.paymentTypeLayout.error = null
            }

            val paymentType = PaymentMethod.valueOf(paymentTypeText)

            if (validateInputs(paymentType)) {
                val paymentData: PaymentTransaction = collectPaymentData(paymentType)
                paymentAddListener?.onPaymentAdded(paymentData)
                dismiss()
            }
        }
    }


    fun setPaymentAddListener(listener: PaymentAddListener) {
        paymentAddListener = listener
    }
}


























