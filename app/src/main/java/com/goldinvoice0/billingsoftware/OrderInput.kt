package com.goldinvoice0.billingsoftware

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.JewelleryItemAdapter_Display
import com.goldinvoice0.billingsoftware.Adapter.PaymentListAdapter_Display
import com.goldinvoice0.billingsoftware.KeyboardUtils.hideSoftKeyboard
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.Model.DeliveryStatus
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.BillInputViewModel
import com.goldinvoice0.billingsoftware.ViewModel.OrderNumberViewModel
import com.goldinvoice0.billingsoftware.ViewModel.OrderViewModel
import com.goldinvoice0.billingsoftware.ViewModel.SharedNavigationViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentOrderInputBinding
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class OrderInput : Fragment() {
    private var _binding: FragmentOrderInputBinding? = null
    private val binding get() = _binding!!


    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var paymentAdapter: PaymentListAdapter_Display
    private lateinit var itemAdapter: JewelleryItemAdapter_Display

    private val shopViewModel: ShopViewModel by viewModels()
    private lateinit var shop: Shop
    private var selectedCustomer: Customer? = null

    // Save state
    private var customerState: String? = null
    private val billInputsViewModel: BillInputViewModel by activityViewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var paymentList: List<PaymentTransaction>
    val vm: SharedNavigationViewModel by activityViewModels()
    private lateinit var itemList: List<JewelryItem>

    private val orderNumberViewModel: OrderNumberViewModel by viewModels()
    private var orderNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderInputBinding.inflate(inflater, container, false)

        shopViewModel.shop.observe(viewLifecycleOwner, Observer {
            this.shop = it
        })

        lifecycleScope.launch {
            orderNumber = orderNumberViewModel.getNextOrderNumber().toString()

        }

        binding.btnAddAdvancePayment.setOnClickListener {
            if (selectedCustomer == null) {
                showToast("Please select a customer first")
                return@setOnClickListener
            }
            val bundle = Bundle()
            bundle.putBoolean("EditPayment", false)
            findNavController().navigate(R.id.action_orderInput_to_orderInputContainer)
        }

        binding.btnAddOrderItem.setOnClickListener {
            Log.d("Tag", "btnClicked2")
            if (selectedCustomer == null) {
                showToast("Please select a customer first")
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_orderInput_to_orderInputContainer2)
        }


        sharedViewModel.paymentsAdvance.observe(viewLifecycleOwner) { paymentsList ->
            Log.d("Tag", paymentsList.size.toString())
            this.paymentList = paymentsList
            paymentAdapter.submitList(paymentsList.toList()) // Update the RecyclerView adapter
            binding.rvAdvancePayments.visibility =
                if (paymentsList.isEmpty()) View.GONE else View.VISIBLE
            binding.tvAdvancePaymentsHeader.visibility =
                if (paymentsList.isEmpty()) View.GONE else View.VISIBLE
            binding.btnAddAdvancePayment.visibility =
                if (paymentsList.isEmpty()) View.VISIBLE else View.GONE
        }

        sharedViewModel.jewelleryItems.observe(viewLifecycleOwner) { jewelleryList ->
            itemList = jewelleryList
            itemAdapter.submitList(jewelleryList.toList())
            binding.rvOrderItems.visibility =
                if (jewelleryList.isEmpty()) View.GONE else View.VISIBLE
            binding.tvOrderItemsHeader.visibility =
                if (jewelleryList.isEmpty()) View.GONE else View.VISIBLE
            binding.btnAddOrderItem.visibility =
                if (jewelleryList.isEmpty()) View.VISIBLE else View.GONE
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


        val calendar = Calendar.getInstance()


        // Create a DatePickerDialog instance
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // Format and set the selected date
            val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            binding.edtDeliveryDate.setText(selectedDate)
        }
        // Format and set the current date initially
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.edtDeliveryDate.setText(dateFormat.format(calendar.time))


        setupRecyclerViews()


        binding.tilCustomerName.setEndIconOnClickListener {
            findNavController().navigate(
                R.id.action_orderInput_to_customerList,
                customerList.newInstance(selectionMode = true).arguments
            )
        }

        binding.tilDeliveryDate.setEndIconOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePickerListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        view?.let { it1 -> setupUI(it1) };




        binding.btnSubmitOrder.setOnClickListener {

            lifecycleScope.launch {


                val order = Order(
                    customerName = selectedCustomer!!.name,
                    customerNumber = selectedCustomer!!.number,
                    address = selectedCustomer!!.address,
                    deliveryDate = binding.edtDeliveryDate.text.toString(),
                    deliveryStatus = DeliveryStatus.PENDING,
                    payments = paymentList,
                    jewelryItems = itemList,
                    orderNumber = orderNumber!!
                )
                orderViewModel.insertOrder(order)
                sharedViewModel.clearPayments()
                sharedViewModel.clearJewelryItem()


                PDFBoxResourceLoader.init(binding.root.context)
                val file = PdfGenerationClassOrder().createPdfFromList(
                    requireContext(),
                    order,
                    shop,
                    orderNumber!!,
                    order.customerName + " " + order.orderNumber
                )

                val bundle = Bundle()
                bundle.putString("file", file.toString())
                if (file.toString().isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(
                            R.id.action_orderInput_to_viewOrderFragment,
                            bundle
                        )
                    }
                }









//                findNavController().navigate(R.id.action_orderInput_to_mainScreen)
            }
        }

        return binding.root
    }


    private fun handleSelectedCustomer(customer: Customer) {
        selectedCustomer = customer
        billInputsViewModel.setCustomer(customer)
        binding.edtCustomerName.setText(customer.name)
        binding.tilCustomerName.setEndIconDrawable(R.drawable.ink_pen_24px)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCustomerResultListener()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setupCustomerResultListener() {
        setFragmentResultListener("customerRequest") { _, bundle ->
            BundleCompat.getParcelable(bundle, "selectedCustomer", Customer::class.java)
                ?.let { customer ->
                    handleSelectedCustomer(customer)
                }
        }
    }


    private fun setupRecyclerViews() {
        paymentAdapter = PaymentListAdapter_Display(
            onItemClick = { item -> handleEdit3(item) }
        )
        binding.rvAdvancePayments.apply {
            adapter = paymentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        itemAdapter = JewelleryItemAdapter_Display(onItemClick = { item, position ->
            handleEdit2(
                item,
                position
            )
        })
        binding.rvOrderItems.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleEdit2(item: JewelryItem, position: Int) {
        Log.d("Tag", "btnClicked1")
        val bundle = Bundle()
        bundle.putBoolean("EditJewelleryItem", true)
        findNavController().navigate(R.id.action_orderInput_to_orderInputContainer2, bundle)
    }

    private fun handleEdit3(item: PaymentTransaction) {
        Log.d("Tag", "btnClicked1")
        val bundle = Bundle()
        bundle.putBoolean("EditAdvancePayment", true)
        findNavController().navigate(R.id.action_orderInput_to_orderInputContainer, bundle)
    }


    private fun setupUI(view: View) {
        // Set up a touch listener for non-EditText views
        if (view !is EditText) {
            view.setOnTouchListener { v: View?, event: MotionEvent? ->
                hideSoftKeyboard(requireContext(), view)
                false
            }
        }

        // If it's a container (like ConstraintLayout), iterate over its children
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setupUI(child) // Recursively apply to child views
            }
        }
    }

}