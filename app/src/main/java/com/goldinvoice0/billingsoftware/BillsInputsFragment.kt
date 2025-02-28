package com.goldinvoice0.billingsoftware

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.ExpandableAdapter_Display
import com.goldinvoice0.billingsoftware.Adapter.ReceviedPaymentAdapter_Display
import com.goldinvoice0.billingsoftware.Model.BillInputs
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.goldinvoice0.billingsoftware.Model.RecivedPaymentType
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.BillInputViewModel
import com.goldinvoice0.billingsoftware.ViewModel.InvoiceNumberViewModel
import com.goldinvoice0.billingsoftware.ViewModel.SharedNavigationViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentBillsInputsBinding
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BillsInputsFragment : Fragment() {

    private var _binding: FragmentBillsInputsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: JewelryViewModel by activityViewModels()
    private val billInputViewModel: BillInputViewModel by activityViewModels()

    private lateinit var adapter: ExpandableAdapter_Display
    private lateinit var adapterForPayment: ReceviedPaymentAdapter_Display
    private var itemList: MutableList<ItemModel> = mutableListOf()
    private var paymentList: MutableList<PaymentRecived> = mutableListOf()
    private val shopViewModel: ShopViewModel by activityViewModels()
    private lateinit var shop: Shop
    private var selectedCustomer: Customer? = null
    val vm: SharedNavigationViewModel by activityViewModels()

    // Save state
    private var customerState: String? = null
    val invoiceViewModel: InvoiceNumberViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCustomerResultListener()
    }


    private fun setupCustomerResultListener() {
        setFragmentResultListener("customerRequest") { _, bundle ->
            BundleCompat.getParcelable(bundle, "selectedCustomer", Customer::class.java)
                ?.let { customer ->
                    handleSelectedCustomer(customer)
                }
        }
    }


    private fun handleSelectedCustomer(customer: Customer) {
        selectedCustomer = customer
        billInputViewModel.setCustomer(customer)
        binding.edtCustomerName.setText(customer.name)
        binding.tilCustomerName.setEndIconDrawable(R.drawable.ink_pen_24px)
        binding.btnAddBillItem.visibility =
            if (customer.name.isNotEmpty()) View.VISIBLE else View.GONE
        binding.cardBillItems.visibility =
            if (customer.name.isNotEmpty()) View.VISIBLE else View.GONE
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillsInputsBinding.inflate(inflater, container, false)


        shopViewModel.shop.observe(viewLifecycleOwner) {
            shop = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
        setupRecyclerViews()
        setupDatePicker()

        // Restore state if available
        savedInstanceState?.let {
            it.getParcelable<Customer>(KEY_SELECTED_CUSTOMER)?.let { customer ->
                handleSelectedCustomer(customer)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedCustomer?.let {
            outState.putParcelable(KEY_SELECTED_CUSTOMER, it)
        }
    }


    private fun setupObservers() {
        // Persist ViewModels data
        viewModel.items.observe(viewLifecycleOwner) { items ->
            itemList = items.toMutableList()
            adapter.submitList(items)
            updateUIBasedOnItems(items)
        }

        sharedViewModel.paymentEntry.observe(viewLifecycleOwner) { payments ->
            paymentList = payments.toMutableList()
            adapterForPayment.submitList(payments)
            updateUIBasedOnPayments(payments)
        }

        // Customer selection result listener
        setFragmentResultListener("customerRequest") { _, bundle ->
            @Suppress("DEPRECATION")
            val customer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("selectedCustomer", Customer::class.java)
            } else {
                bundle.getParcelable("selectedCustomer")
            }

            customer?.let {
                handleSelectedCustomer(customer)
                Log.d("customrList", "selected customer is ${it.name}")
            }
        }
    }

    private fun updateUIBasedOnItems(items: List<ItemModel>) {
        binding.apply {
            val hasItems = items.isNotEmpty()
            rvBillItems.visibility = if (hasItems) View.VISIBLE else View.GONE
            tvBillItemsHeader.visibility = if (hasItems) View.VISIBLE else View.GONE
            btnAddBillItem.visibility = if (hasItems) View.GONE else View.VISIBLE
            Log.d("DEBUG", "hasItems: $hasItems")
            if (hasItems) {
                billInputViewModel.customerSelected.observe(viewLifecycleOwner) {
                    Log.d("DEBUG", it!!.name)
                    selectedCustomer = it
                    edtCustomerName.setText(selectedCustomer!!.name)
                    tilCustomerName.setEndIconDrawable(R.drawable.ink_pen_24px)
                }
            }


            // Show payments section if there are items
            if (hasItems) {
                cardPayments.visibility = View.VISIBLE
                btnSubmitOrder.visibility = View.VISIBLE
            }
        }
    }

    private fun updateUIBasedOnPayments(payments: List<PaymentRecived>) {
        binding.apply {

            val hasPayments = payments.isNotEmpty()
            Log.d("DEBUG", "hasPayments: $hasPayments")
            if (hasPayments) {
                selectedCustomer = billInputViewModel.customerSelected.value
                edtCustomerName.setText(selectedCustomer!!.name)
                tilCustomerName.setEndIconDrawable(R.drawable.ink_pen_24px)
            }

            rvPayments.visibility = if (hasPayments) View.VISIBLE else View.GONE
            tvPaymentsHeader.visibility = if (hasPayments) View.VISIBLE else View.GONE
            btnPayment.visibility = if (hasPayments) View.GONE else View.VISIBLE
        }
    }

    private fun setupListeners() {
        binding.apply {

            tilCustomerName.setEndIconOnClickListener {
                navigateToCustomerList()
            }


            btnAddBillItem.setOnClickListener {
                if (selectedCustomer == null) {
                    showToast("Please select a customer first")
                    return@setOnClickListener
                }
                navigateToNewBill(false)

            }

            btnPayment.setOnClickListener {
                handlePaymentClick()
            }

            btnSubmitOrder.setOnClickListener {
                if (validateInputs()) {
                    submitOrder()
                }
            }
        }
    }

    private fun navigateToCustomerList() {
        findNavController().navigate(
            R.id.action_billsInputsFragment_to_customerList,
            customerList.newInstance(selectionMode = true).arguments
        )
    }

    private fun navigateToNewBill(isEdit: Boolean) {
        val bundle = Bundle().apply {
            putBoolean("EditBill", isEdit)
        }
        findNavController().navigate(R.id.action_billsInputsFragment_to_newBill, bundle)
    }

    private fun handlePaymentClick() {
        val totalAmount = calculateTotalAmount()
        if (totalAmount > 0) {
            val bundle = Bundle().apply {
                putBoolean("EditPayment", false)
                putInt("totalAmount", totalAmount)
            }
            findNavController().navigate(R.id.action_billsInputsFragment_to_paymentEntry, bundle)
        } else {
            showToast("Please add items first")
        }
    }


    private fun calculateTotalAmount(): Int {
        return itemList.sumOf { it.totalValue.toInt() }
    }

    private fun validateInputs(): Boolean {
        if (selectedCustomer == null) {
            showToast("Please select a customer")
            return false
        }
        if (itemList.isEmpty()) {
            showToast("Please add at least one item")
            return false
        }
        return true
    }

    private fun submitOrder() {
        val totalAmount = calculateTotalAmount()
        var receivedAmount = 0
        var extraCharges = 0

        paymentList.forEach { payment ->

            when (payment.type) {
                RecivedPaymentType.RECEIVED -> receivedAmount += payment.amount
                RecivedPaymentType.EXTRA_CHARGE -> extraCharges += payment.amount
                else -> {}
            }
        }
        lifecycleScope.launch {

            val finalTotalAmount = totalAmount + extraCharges
            val dueAmount = finalTotalAmount - receivedAmount
            var status = "Pending"


            if (dueAmount > 0 && receivedAmount == 0) {
                status = "Pending"
            } else if (dueAmount == 0) {
                status = "Paid"
            } else if (receivedAmount > 0 && dueAmount > 0) {
                status = "Partially Paid"
            }

            val invoiceNumber = invoiceViewModel.getNextInvoiceNumber()

            selectedCustomer?.let { customer ->
                val bill = BillInputs(
                    customerName = customer.name,
                    customerNumber = customer.number,
                    customerAddress = customer.address,
                    date = binding.edtDeliveryDate.text.toString(),
                    invoiceNumber = invoiceNumber.toString(),
                    paymentList = paymentList.toList(),
                    itemList = itemList.toList(),
                    totalAmount = finalTotalAmount,
                    receviedAmount = receivedAmount,
                    dueAmount = dueAmount,
                    status = status
                )

                billInputViewModel.insertBillInputs(bill)

                vm.requestTab(0)

                binding.spinner.visibility = View.VISIBLE
                val fileName = bill.customerName.replace("/", "") + bill.invoiceNumber
                PDFBoxResourceLoader.init(binding.root.context)

                val file = PdfGenerationClass001().createPdfFromView(
                    requireContext(),
                    bill,
                    shop,
                    fileName
                )
                clearAllData()
                val bundle = Bundle()
                bundle.putString("file", file.toString())
                Log.d("file", "file $file")
                if (file.toString().isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(
                            R.id.action_billsInputsFragment_to_viewBill,
                            bundle
                        )
                    }
                }


            }
            // Clear all data after successful submission

        }
    }


    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            binding.edtDeliveryDate.setText(selectedDate)
        }

        binding.apply {
            edtDeliveryDate.setText(dateFormat.format(calendar.time))
            tilDeliveryDate.setEndIconOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    datePickerListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        adapter = ExpandableAdapter_Display { item ->
            val bundle = Bundle().apply {
                putBoolean("EditBill", true)
            }
            findNavController().navigate(R.id.action_billsInputsFragment_to_newBill, bundle)
        }

        adapterForPayment = ReceviedPaymentAdapter_Display { item ->


            val totalAmount = calculateTotalAmount()
            if (totalAmount > 0) {
                val bundle = Bundle().apply {
                    putBoolean("EditPayment", true)
                    putInt("totalAmount", totalAmount)
                }
                findNavController().navigate(R.id.action_billsInputsFragment_to_paymentEntry, bundle)
            } else {
                showToast("Please add items first")
            }
        }

        binding.apply {
            rvBillItems.layoutManager = LinearLayoutManager(requireContext())
            rvBillItems.adapter = adapter

            rvPayments.layoutManager = LinearLayoutManager(requireContext())
            rvPayments.adapter = adapterForPayment
        }
    }

    private fun clearAllData() {
        selectedCustomer = null
        customerState = null
        itemList.clear()
        paymentList.clear()
        viewModel.clearItems()
        sharedViewModel.clearRevivedPayments()

    }

    private fun generateInvoiceNumber(): String {


        // Implement your invoice number generation logic
        return "INV-${System.currentTimeMillis()}"
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val KEY_CUSTOMER_NAME = "customer_name"
        private const val KEY_SELECTED_CUSTOMER = "selected_customer" // Add this line
    }

}
