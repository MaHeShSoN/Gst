package com.goldinvoice0.billingsoftware

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.goldinvoice0.billingsoftware.Model.ExtraChargeType
import com.goldinvoice0.billingsoftware.Model.PaymentMethod
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.goldinvoice0.billingsoftware.Model.RecivedPaymentType
import com.goldinvoice0.billingsoftware.databinding.FragmentPaymentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PaymentBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPaymentBottomSheetBinding
    private var totalAmount: Int = 0
    private var onPaymentAdded: ((PaymentRecived) -> Unit)? = null
    private var selectedDate: Long = System.currentTimeMillis()

    fun setTotalAmount(amount: Int) {
        totalAmount = amount
        Log.d("totalPayment", totalAmount.toString())
    }

    fun setOnPaymentAddedListener(listener: (PaymentRecived) -> Unit) {
        onPaymentAdded = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBottomSheetBinding.inflate(inflater, container, false)


        // Make bottom sheet full screen
        dialog?.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true

                // Set the height to match parent
                sheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupPaymentTypeSelection()
        setupCalculationListeners()
        setupDatePicker()
        setupDropdowns()
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


        // Set initial date
        binding.etExtraChargeDate.setText(
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(selectedDate))
        )

        binding.extraChargeDatePicker.setEndIconOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDate = calendar.timeInMillis
                    binding.etExtraChargeDate.setText(
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


    private fun setupDropdowns() {

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


        // Setup extra charge types dropdown
        val extraChargeTypes = ExtraChargeType.values().map { it.name.replace("_", " ") }
        val extraChargeTypesAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            extraChargeTypes
        ).apply {
            setDropDownViewResource(R.layout.dropdown_item)
        }

        binding.dropDownExtraChargeType.apply {
            setAdapter(extraChargeTypesAdapter)
            setDropDownBackgroundResource(R.color.grayLight1)
        }



        //Implement Bank Name,Upi Service Name Dropdown
        setupUpiServiceDropdown()
        setupBankNameDropdown()

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


    private fun calculateGoldValue() {
        val weight = binding.goldWeightInput.text.toString().toDoubleOrNull() ?: 0.0
        val rate = binding.goldRateInput.text.toString().toDoubleOrNull() ?: 0.0
        val value = weight * rate
        binding.paymentAmountInput.setText(value.toInt().toString())
        Log.d("rPAdapter", value.format(2))
    }

    private fun calculateSilverValue() {
        val weight = binding.silverWeightInput.text.toString().toDoubleOrNull() ?: 0.0
        val rate = binding.silverRateInput.text.toString().toDoubleOrNull() ?: 0.0
        val value = weight * rate
        binding.paymentAmountInput.setText(value.toInt().toString())
        Log.d("rPAdapter", value.format(2))
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


    private fun setupPaymentTypeSelection() {
        binding.rgPaymentTypeSelection.setOnCheckedChangeListener { _, checkedId ->
            binding.apply {
                when (checkedId) {
                    R.id.rb_payment_received -> {
                        layoutPaymentReceived.visibility = View.VISIBLE
                        layoutExtraCharges.visibility = View.GONE
                    }

                    R.id.rb_extra_charges -> {
                        layoutPaymentReceived.visibility = View.GONE
                        layoutExtraCharges.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupViews() {
        binding.btnSubmitPayment.setOnClickListener {
            savePayment()
        }
    }

    private fun collectPaymentData(paymentType: PaymentMethod): PaymentRecived {
        return when (paymentType) {
            PaymentMethod.GOLD_EXCHANGE -> PaymentRecived(
                type = RecivedPaymentType.RECEIVED,
                paymentMethod = paymentType,
                amount = binding.paymentAmountInput.text.toString().toIntOrNull() ?: 0,
                goldWeight = binding.goldWeightInput.text.toString().toDoubleOrNull(),
                goldRate = binding.goldRateInput.text.toString().toDoubleOrNull(),
                date = binding.etDate.text.toString()
            )

            PaymentMethod.SILVER_EXCHANGE -> PaymentRecived(
                type = RecivedPaymentType.RECEIVED,
                paymentMethod = paymentType,
                amount = binding.paymentAmountInput.text.toString().toIntOrNull() ?: 0,
                silverWeight = binding.silverWeightInput.text.toString().toDoubleOrNull(),
                silverRate = binding.silverRateInput.text.toString().toDoubleOrNull(),
                date = binding.etDate.text.toString()

            )

            PaymentMethod.UPI -> PaymentRecived(
                type = RecivedPaymentType.RECEIVED,
                paymentMethod = paymentType,
                amount = binding.paymentAmountInput.text.toString().toIntOrNull() ?: 0,
                upiService = binding.upiServiceDropdown.text.toString(),
                fromUpi = binding.fromUpiInput.text.toString(),
                toUpi = binding.toUpiInput.text.toString(),
                date = binding.etDate.text.toString()
            )

            PaymentMethod.BANK_TRANSFER -> PaymentRecived(
                type = RecivedPaymentType.RECEIVED,
                paymentMethod = paymentType,
                amount = binding.paymentAmountInput.text.toString().toIntOrNull() ?: 0,
                bankName = binding.bankNameDropdown.text.toString(),
                fromName = binding.fromNameInput.text.toString(),
                toName = binding.toNameInput.text.toString(),
                date = binding.etDate.text.toString()
            )

            PaymentMethod.CHECK, PaymentMethod.DEMAND_DRAFT -> PaymentRecived(
                type = RecivedPaymentType.RECEIVED,
                paymentMethod = paymentType,
                amount = binding.paymentAmountInput.text.toString().toIntOrNull() ?: 0,
                bankName = binding.bankNameDropdown.text.toString(),
                fromName = binding.fromNameInput.text.toString(),
                toName = binding.toNameInput.text.toString(),
                checkNumber = binding.checkNumberInput.text.toString(),
                date = binding.etDate.text.toString()
            )

            else -> PaymentRecived(
                type = RecivedPaymentType.RECEIVED,
                paymentMethod = paymentType,
                amount = binding.paymentAmountInput.text.toString().toIntOrNull() ?: 0,
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

    private fun validateExtraChargeType(extraChargeType: ExtraChargeType): Boolean {
        var isValid = true

        fun showError(inputField: TextInputLayout, errorMessage: String) {
            inputField.error = errorMessage
            isValid = false
        }

        fun clearError(inputField: TextInputLayout) {
            inputField.error = null
        }

        // Validate Amount
        if (binding.etExtraChargeAmount.text.toString().isBlank()) {
            showError(binding.extraChargeAmountLayout, "Amount is required")
        } else {
            clearError(binding.extraChargeAmountLayout)
        }




        return isValid
    }

    private fun savePayment() {
        val amount = binding.paymentAmountInput.text.toString().toIntOrNull() ?: 0
        val isReceived =
            binding.rgPaymentTypeSelection.checkedRadioButtonId == R.id.rb_payment_received


        // For received payments, validate against total amount
        if (isReceived && amount > totalAmount) {
            Snackbar.make(
                binding.root,
                "Amount cannot be greater than balance due",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }


        if (isReceived) {
            //payment methods will be use to find which type of payment is done
            val paymentTypeText = binding.paymentTypeDropdown.text.toString()

            if (amount <= 0) {
                Snackbar.make(binding.root, "Please enter a valid amount", Snackbar.LENGTH_SHORT)
                    .show()
                Log.d("rPAdapter", amount.toString() + " amount")
                return
            }

            if (paymentTypeText.isBlank()) {
                binding.paymentTypeLayout.error = "Please select a payment method"
                return
            } else {
                binding.paymentTypeLayout.error = null
            }

            val paymentType = PaymentMethod.valueOf(paymentTypeText)
            if (validateInputs(paymentType)) {
                val paymentData: PaymentRecived = collectPaymentData(paymentType)
                onPaymentAdded?.invoke(paymentData)

                dismiss()
            }

        } else {
            val extraChargeTypeText = binding.dropDownExtraChargeType.text.toString()
            val extraChargeTypeAmount = binding.etExtraChargeAmount.text.toString()

            if (extraChargeTypeText.isBlank()) {
                binding.extraChargeLayout.error = "Please select a extra charge type"
            } else {
                binding.extraChargeLayout.error = null
            }

            val extraChargeType = ExtraChargeType.valueOf(extraChargeTypeText)
            if (validateExtraChargeType(extraChargeType)) {
                val extraChargeData: PaymentRecived =
                    PaymentRecived(amount = extraChargeTypeAmount.toIntOrNull() ?: 0,
                        type = RecivedPaymentType.EXTRA_CHARGE,
                        extraChargeType = ExtraChargeType.values()[ExtraChargeType.values()
                            .map { it.name.replace("_", " ") }
                            .indexOf(binding.dropDownExtraChargeType.text.toString())],
                        date = binding.etExtraChargeDate.text.toString()
                    )
                onPaymentAdded?.invoke(extraChargeData)
                dismiss()
            }

        }

//        // Create payment object
//        val payment = PaymentRecived(
//            amount = amount,
//            type = if (isReceived) RecivedPaymentType.RECEIVED else RecivedPaymentType.EXTRA_CHARGE,
//            date = selectedDate,
//            paymentMethod = if (isReceived)
//                PaymentMethod.values()[PaymentMethod.values()
//                    .map { it.name.replace("_", " ") }
//                    .indexOf(binding.dropdownPaymentMethod.text.toString())]
//            else null,
//            extraChargeType = if (!isReceived)
//                ExtraChargeType.values()[ExtraChargeType.values()
//                    .map { it.name.replace("_", " ") }
//                    .indexOf(binding.dropdownExtraChargeType.text.toString())]
//            else null
//        )
    }
}