package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.JewelleryItemAdapter_Display
import com.goldinvoice0.billingsoftware.Adapter.PaymentAdapter_Display
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.Payment
import com.goldinvoice0.billingsoftware.ViewModel.OrderViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentOrderEditBinding


class OrderEdit : Fragment() {

    private var _binding: FragmentOrderEditBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    lateinit var order: Order

    private lateinit var paymentAdapter: PaymentAdapter_Display
    private lateinit var itemAdapter: JewelleryItemAdapter_Display

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderEditBinding.inflate(inflater, container, false)

        // Initialize RecyclerView Adapters
        paymentAdapter = PaymentAdapter_Display(
            onItemClick = { payment, position -> handleItemClickListener(payment, position) }
        )
        itemAdapter = JewelleryItemAdapter_Display(
            onItemClick = { item, position -> HandleItemClickListner2(item, position) }
        )

        // Setup RecyclerViews
        binding.rvAdvancePayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentAdapter
        }

        binding.rvOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }

        // Observe Order Data from SharedViewModel
        sharedViewModel.getOrderData().observe(viewLifecycleOwner) { order1 ->
            order = order1
            updateUI(order1)
        }



        clickListner()


        return binding.root
    }

    private fun clickListner() {
        Log.d("Tag", sharedViewModel.payments.value!!.toList().toString())
        binding.btnSubmitOrder.setOnClickListener {
            order?.let { currentOrder ->
                val updatedOrder = currentOrder.copy(
                    payments = sharedViewModel.payments.value?.toList() ?: emptyList(),
                    jewelryItems = sharedViewModel.jewelleryItems.value?.toList() ?: emptyList()
                )
                if (updatedOrder != currentOrder) { // Only update if something changed
                    orderViewModel.updateOrder(updatedOrder)
                    Log.d("OrderEdit", "Order updated successfully: $updatedOrder")
                } else {
                    Log.d("OrderEdit", "No changes detected, skipping update")
                }
            } ?: Log.e("OrderEdit", "Order is null, cannot update")
        }

        binding.btnAddOrderItem.setOnClickListener {

            val bundle = Bundle()
            bundle.putBoolean("EditJewelleryItem", true)
            findNavController().navigate(R.id.action_orderEdit_to_orderInputContainer2, bundle)
        }
        binding.btnAddAdvancePayment.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("EditAdvancePayment", true)
            findNavController().navigate(R.id.action_orderEdit_to_orderInputContainer, bundle)
        }
    }

    private fun HandleItemClickListner2(item: JewelryItem, position: Int) {


        val bundle = Bundle()
        bundle.putBoolean("EditJewelleryItem", true)
        findNavController().navigate(R.id.action_orderEdit_to_orderInputContainer2, bundle)

        Log.d("Tag", "inOrderEdit")
    }


    private fun handleItemClickListener(payment: Payment, position: Int) {
        Log.d("Tag", "inOrderEdit")

        // Add all payments from order to ViewModel

        // Navigate to next screen
        val bundle = Bundle()
        bundle.putBoolean("EditAdvancePayment", true)
        findNavController().navigate(R.id.action_orderEdit_to_orderInputContainer, bundle)
    }


    private fun updateUI(order: Order) {
        // Set Customer Details
        binding.tvCustomerName.text = order.customerName
        binding.tvAddress.text = order.address
        binding.tvCustomerNumber.text = order.customerNumber
        binding.tvDeliveryDate.text = order.deliveryDate
        binding.tvOrderNumber.text = order.orderNumber


// Check and add payments if empty or null

        if (!sharedViewModel.paymentsDeleted && sharedViewModel.payments.value.isNullOrEmpty()) {
            order.payments.forEach { sharedViewModel.addPayment(it) }
        }
        if (!sharedViewModel.jewelleryItemDeleted && sharedViewModel.jewelleryItems.value.isNullOrEmpty()) {
            order.jewelryItems.forEach { sharedViewModel.addJewelleryItem(it) }
        }


// Update RecyclerViews properly
        itemAdapter.submitList(sharedViewModel.jewelleryItems.value ?: emptyList())
        paymentAdapter.submitList(sharedViewModel.payments.value ?: emptyList())

        // Manage Visibility for Order Items Section
        if (sharedViewModel.jewelleryItems.value.isNullOrEmpty()) {
            binding.tvOrderItemsHeader.visibility = View.GONE
            binding.rvOrderItems.visibility = View.GONE
            binding.btnAddOrderItem.visibility = View.VISIBLE
        } else {
            binding.tvOrderItemsHeader.visibility = View.VISIBLE
            binding.rvOrderItems.visibility = View.VISIBLE
            binding.btnAddOrderItem.visibility = View.GONE
        }

        // Manage Visibility for Payment Section
        if (sharedViewModel.payments.value.isNullOrEmpty()) {
            binding.tvAdvancePaymentsHeader.visibility = View.GONE
            binding.rvAdvancePayments.visibility = View.GONE
            binding.btnAddAdvancePayment.visibility = View.VISIBLE
        } else {
            binding.tvAdvancePaymentsHeader.visibility = View.VISIBLE
            binding.rvAdvancePayments.visibility = View.VISIBLE
            binding.btnAddAdvancePayment.visibility = View.GONE
        }


    }


//    private fun setupDeliveryStatusSpinner(order: Order) {
//        val statusList = DeliveryStatus.values().map { it.name } // Get Enum Values
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        binding.spinnerDeliveryStatus.adapter = adapter
//        binding.spinnerDeliveryStatus.setSelection(statusList.indexOf(order.deliveryStatus.name))
//
//        binding.spinnerDeliveryStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val newStatus = DeliveryStatus.valueOf(statusList[position])
//                if (newStatus != order.deliveryStatus) {
//                    orderViewModel.updateDeliveryStatus(order.id, newStatus)
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
