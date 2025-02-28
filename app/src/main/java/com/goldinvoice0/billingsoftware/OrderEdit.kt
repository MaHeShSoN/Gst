package com.goldinvoice0.billingsoftware

import android.app.AlertDialog
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
import com.goldinvoice0.billingsoftware.Adapter.PaymentListAdapter_Display
import com.goldinvoice0.billingsoftware.Model.DeliveryStatus
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction
import com.goldinvoice0.billingsoftware.ViewModel.OrderViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentOrderEditBinding

class OrderEdit : Fragment() {
    // View Binding
    private var _binding: FragmentOrderEditBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val orderViewModel: OrderViewModel by viewModels()

    // Data
    private var originalOrder: Order? = null
    private var hasChanges = false

    // Adapters
    private lateinit var paymentAdapter: PaymentListAdapter_Display
    private lateinit var itemAdapter: JewelleryItemAdapter_Display

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderEditBinding.inflate(inflater, container, false)
        setupAdapters()
        setupRecyclerViews()
        observeData()
        setupClickListeners()
        return binding.root
    }

    // region Setup Methods

    /**
     * Initialize adapters with click listeners
     */
    private fun setupAdapters() {
        paymentAdapter = PaymentListAdapter_Display { payment ->
            handlePaymentClick(payment)
        }

        itemAdapter = JewelleryItemAdapter_Display { item, position ->
            handleJewelryItemClick(item, position)
        }
    }

    /**
     * Setup RecyclerViews with adapters and layouts
     */
    private fun setupRecyclerViews() {
        binding.apply {
            rvAdvancePayments.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = paymentAdapter
            }

            rvOrderItems.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = itemAdapter
            }
        }
    }

    /**
     * Observe data changes from SharedViewModel
     */
    private fun observeData() {
        // Observe order data
        sharedViewModel.getOrderData().observe(viewLifecycleOwner) { order ->
            originalOrder = order
            updateUI(order)
        }

        // Observe payments changes
        sharedViewModel.paymentsAdvance.observe(viewLifecycleOwner) { payments ->
            paymentAdapter.submitList(payments)
            checkForChanges()
        }

        // Observe jewelry items changes
        sharedViewModel.jewelleryItems.observe(viewLifecycleOwner) { items ->
            itemAdapter.submitList(items)
            checkForChanges()
        }
    }

    /**
     * Setup click listeners for buttons and menu items
     */
    private fun setupClickListeners() {
        binding.apply {
            // Add Order Item button
            btnAddOrderItem.setOnClickListener {
                navigateToOrderInput(true)
            }

            // Add Payment button
            btnAddAdvancePayment.setOnClickListener {
                navigateToPaymentInput(true)
            }

            // Update Order button
            btnSubmitOrder.setOnClickListener {
                updateOrder()
            }

            // Top App Bar menu items
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.buttonDeleteInvoice -> {
                        showDeleteConfirmation()
                        true
                    }

                    R.id.buttonPaid -> {
                        val bundle = Bundle()
                        bundle.putInt("orderNumber", originalOrder!!.id)

                        Log.d("Order",originalOrder!!.id.toString())


                        findNavController().navigate(R.id.action_orderEdit_to_convertOrderToBillFragment,bundle)
                        true
                    }


                    else -> false
                }
            }
        }
    }


    // endregion

    // region UI Update Methods

    /**
     * Update the UI with order details
     */
    private fun updateUI(order: Order) {
        binding.apply {
            // Update customer details
            tvCustomerName.text = order.customerName
            tvAddress.text = order.address
            tvCustomerNumber.text = order.customerNumber
            tvDeliveryDate.text = order.deliveryDate
            tvOrderNumber.text = order.orderNumber
//            chipDeliveryStatus.text = order.deliveryStatus.toString()

            val billStatus = when {
                // If any item is IN_PROGRESS, the whole bill is IN_PROGRESS
                order.jewelryItems.any { it.status == DeliveryStatus.IN_PROGRESS } -> DeliveryStatus.IN_PROGRESS

                // If all items are COMPLETED, the bill is COMPLETED
                order.jewelryItems.all { it.status == DeliveryStatus.COMPLETED } -> DeliveryStatus.COMPLETED

                // If all items are PENDING, the bill is PENDING
                order.jewelryItems.all { it.status == DeliveryStatus.PENDING } -> DeliveryStatus.PENDING

                // Mixed statuses (some completed, some pending) default to IN_PROGRESS
                else -> DeliveryStatus.IN_PROGRESS
            }


            // Set delivery status and chip color
            when (billStatus) {
                DeliveryStatus.PENDING -> {
                    chipDeliveryStatus.text = "PENDING"
                    chipDeliveryStatus.setChipBackgroundColorResource(R.color.status_pending)
                }

                DeliveryStatus.IN_PROGRESS -> {
                    chipDeliveryStatus.text = "IN PROGRESS"
                    chipDeliveryStatus.setChipBackgroundColorResource(R.color.status_in_progress)
                }

                DeliveryStatus.COMPLETED -> {
                    chipDeliveryStatus.text = "COMPLETED"
                    chipDeliveryStatus.setChipBackgroundColorResource(R.color.status_completed)
                }
            }


            // Initialize data if needed
            initializeDataIfNeeded(order)

            // Update visibility of sections
            updateSectionsVisibility()
        }
    }

    /**
     * Initialize SharedViewModel data if empty
     */
    private fun initializeDataIfNeeded(order: Order) {
        if (!sharedViewModel.paymentsDeleted && sharedViewModel.paymentsAdvance.value.isNullOrEmpty()) {
            order.payments.forEach { sharedViewModel.addPaymentAdvance(it) }
        }
        if (!sharedViewModel.jewelleryItemDeleted && sharedViewModel.jewelleryItems.value.isNullOrEmpty()) {
            order.jewelryItems.forEach { sharedViewModel.addJewelleryItem(it) }
        }
    }

    /**
     * Update visibility of sections based on data availability
     */
    private fun updateSectionsVisibility() {
        binding.apply {
            // Order Items section visibility
            val hasItems = !sharedViewModel.jewelleryItems.value.isNullOrEmpty()
            tvOrderItemsHeader.visibility = if (hasItems) View.VISIBLE else View.GONE
            rvOrderItems.visibility = if (hasItems) View.VISIBLE else View.GONE
            btnAddOrderItem.visibility = if (hasItems) View.GONE else View.VISIBLE

            // Payments section visibility
            val hasPayments = !sharedViewModel.paymentsAdvance.value.isNullOrEmpty()
            tvAdvancePaymentsHeader.visibility = if (hasPayments) View.VISIBLE else View.GONE
            rvAdvancePayments.visibility = if (hasPayments) View.VISIBLE else View.GONE
            btnAddAdvancePayment.visibility = if (hasPayments) View.GONE else View.VISIBLE
        }
    }

    /**
     * Check for changes in data and update button visibility
     */
    private fun checkForChanges() {
        val originalPayments = originalOrder?.payments ?: emptyList()
        val originalItems = originalOrder?.jewelryItems ?: emptyList()

        val currentPayments = sharedViewModel.paymentsAdvance.value ?: emptyList()
        val currentItems = sharedViewModel.jewelleryItems.value ?: emptyList()

        hasChanges = originalPayments != currentPayments || originalItems != currentItems

        binding.btnSubmitOrder.visibility = if (hasChanges) View.VISIBLE else View.GONE
    }

    // endregion

    // region Navigation Methods

    private fun navigateToOrderInput(isEdit: Boolean) {
        val bundle = Bundle().apply {
            putBoolean("EditJewelleryItem", isEdit)
        }
        findNavController().navigate(R.id.action_orderEdit_to_orderInputContainer2, bundle)
    }

    private fun navigateToPaymentInput(isEdit: Boolean) {
        val bundle = Bundle().apply {
            putBoolean("EditAdvancePayment", isEdit)
        }
        findNavController().navigate(R.id.action_orderEdit_to_orderInputContainer, bundle)
    }

    // endregion

    // region Click Handlers

    private fun handlePaymentClick(payment: PaymentTransaction) {
        navigateToPaymentInput(true)
    }

    private fun handleJewelryItemClick(item: JewelryItem, position: Int) {
        navigateToOrderInput(true)
    }

    private fun updateOrder() {
        originalOrder?.let { currentOrder ->
            val updatedOrder = currentOrder.copy(
                payments = sharedViewModel.paymentsAdvance.value?.toList() ?: emptyList(),
                jewelryItems = sharedViewModel.jewelleryItems.value?.toList() ?: emptyList()
            )

            if (hasChanges) {
                orderViewModel.updateOrder(updatedOrder)
                findNavController().popBackStack()
                Log.d("OrderEdit", "Order updated successfully")
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete this order?")
            .setPositiveButton("Delete") { _, _ ->
                originalOrder?.let { order ->
                    orderViewModel.deleteOrder(order)
                    findNavController().popBackStack()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // endregion

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}