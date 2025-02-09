package com.goldinvoice0.billingsoftware

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.JewelleryItemAdapter_Display
import com.goldinvoice0.billingsoftware.Adapter.JewelryItemAdapter
import com.goldinvoice0.billingsoftware.Adapter.PaymentAdapter_Display
import com.goldinvoice0.billingsoftware.KeyboardUtils.hideSoftKeyboard
import com.goldinvoice0.billingsoftware.Model.DeliveryStatus
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.Payment
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.OrderNumberViewModel
import com.goldinvoice0.billingsoftware.ViewModel.OrderViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentOrderInputBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class OrderInput : Fragment() {
    private var _binding: FragmentOrderInputBinding? = null
    private val binding get() = _binding!!


    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var paymentAdapter: PaymentAdapter_Display
    private lateinit var itemAdapter: JewelleryItemAdapter_Display

    private val shopViewModel: ShopViewModel by viewModels()
    private lateinit var shop: Shop
    private var customerName: String = ""
    private var customerNumber: String = ""
    private var customerAddress: String = ""
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var  paymentList: List<Payment>
    private lateinit var  itemList: List<JewelryItem>

    private val orderNumberViewModel: OrderNumberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderInputBinding.inflate(inflater, container, false)

        shopViewModel.shop.observe(viewLifecycleOwner, Observer {
            this.shop = it
        })

        binding.btnAddAdvancePayment.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("EditPayment", false)
            findNavController().navigate(R.id.action_orderInput_to_orderInputContainer)
        }

        binding.btnAddOrderItem.setOnClickListener {
            Log.d("Tag", "btnClicked2")
            findNavController().navigate(R.id.action_orderInput_to_orderInputContainer2)
        }


        sharedViewModel.payments.observe(viewLifecycleOwner) { paymentsList ->
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

        val bundle = arguments
        val name = bundle!!.getString("name").toString()
        val address = bundle.getString("address").toString()
        val phone = bundle.getString("email").toString()

        customerName = name
        customerNumber = phone
        customerAddress = address

        binding.edtCustomerName.setText(customerName)

        binding.tilCustomerName.setEndIconOnClickListener {
            findNavController().popBackStack()
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
            val order = Order(
                customerName = customerName,
                customerNumber = customerNumber,
                address = address,
                deliveryDate = binding.edtDeliveryDate.text.toString(),
                deliveryStatus = DeliveryStatus.PENDING,
                payments = paymentList,
                jewelryItems = itemList,
                orderNumber = "001"
            )
            orderViewModel.insertOrder(order)
            sharedViewModel.clearPayments()
            sharedViewModel.clearJewelryItem()
            findNavController().navigate(R.id.action_orderInput_to_mainScreen)
        }

        return binding.root
    }



    private fun setupRecyclerViews() {
        paymentAdapter = PaymentAdapter_Display(
            onItemClick = { item, position -> handleEdit3(item, position) }
        )
        binding.rvAdvancePayments.apply {
            adapter = paymentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        itemAdapter = JewelleryItemAdapter_Display(onItemClick = { item, position -> handleEdit2(item, position) })
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

    private fun handleEdit3(item: Payment, position: Int) {
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