package com.goldinvoice0.billingsoftware

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.ExpandableAdapter_Display
import com.goldinvoice0.billingsoftware.Adapter.ReceviedPaymentAdapter_Display
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction
import com.goldinvoice0.billingsoftware.Model.RecivedPaymentType
import com.goldinvoice0.billingsoftware.ViewModel.BillInputViewModel
import com.goldinvoice0.billingsoftware.ViewModel.OrderViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentConvertOrderToBillBinding

class ConvertOrderToBillFragment : Fragment() {

    private var _binding: FragmentConvertOrderToBillBinding? = null
    private val binding get() = _binding!!
    private val orderViewModel: OrderViewModel by viewModels()


    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: JewelryViewModel by activityViewModels()
    private val billInputViewModel: BillInputViewModel by activityViewModels()

    private lateinit var adapter: ExpandableAdapter_Display
    private lateinit var adapterForPayment: ReceviedPaymentAdapter_Display
    private var itemList: MutableList<ItemModel> = mutableListOf()
    private var paymentList: MutableList<PaymentRecived> = mutableListOf()

    private lateinit var customer: Customer

    companion object {
        private const val ARG_BILL_ID = "billId"
        private const val ARG_EDIT_BILL = "EditBill"
        private const val ARG_EDIT_PAYMENT = "EditPayment"
        private const val ARG_TOTAL_AMOUNT = "totalAmount"
    }

    override fun onStart() {
        super.onStart()

        // Check if the arguments bundle contains the key "orderNumber"
        if (requireArguments().containsKey("orderNumber")) {
            val orderId = requireArguments().getInt("orderNumber")
            orderViewModel.getOrderById(orderId).observe(viewLifecycleOwner) { order ->
                // Null safety check on the order object
                order?.let {

                    customer = Customer(
                        name = order.customerName,
                        address = order.address,
                        address2 = order.address,
                        number = order.customerNumber
                    )

                    itemList = convertJewelryItemsToItemModels(order.jewelryItems)
                    paymentList = convertPaymentTransactionsToPaymentRecived(order.payments).toMutableList()

                    adapter.submitList(itemList)
                    adapterForPayment.submitList(paymentList)

                    //only have to set the viewModel




                    Log.d("Order", it.customerName)
                }
            }
        }
    }

    private fun navigateToNewBill(item: ItemModel?) {
        findNavController().navigate(
            com.goldinvoice0.billingsoftware.R.id.action_listBillView_to_newBill,
            Bundle().apply {
                putBoolean(ARG_EDIT_BILL, true)
            }
        )
    }

    

    private fun navigateToPaymentEntry() {
        val totalAmount = calculateTotalAmount()
        if (totalAmount > 0) {
            findNavController().navigate(
                com.goldinvoice0.billingsoftware.R.id.action_listBillView_to_paymentEntry,
                Bundle().apply {
                    putBoolean(ARG_EDIT_PAYMENT, true)
                    putInt(ARG_TOTAL_AMOUNT, totalAmount)
                }
            )
        } else {
//            showError("Total amount must be greater than 0")
        }
    }

    private fun calculateTotalAmount(): Int {

        //total amount should

//        return jewelryViewModel.items.value?.sumOf { it.totalValue.toInt() } ?: 0
        return 0
    }
    private fun setUpRecyclerView() {
        adapter = ExpandableAdapter_Display { item ->
            navigateToNewBill(item)
        }

        adapterForPayment = ReceviedPaymentAdapter_Display { payment ->
            navigateToPaymentEntry()
        }

        binding.apply {
            rvBillItems.apply {
                // Add layout animation when the list first loads
                layoutAnimation = LayoutAnimationController(
                    AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
                ).apply {
                    delay = 0.15f
                    order = LayoutAnimationController.ORDER_NORMAL
                }
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapter
            }

            rvPayments.apply {
                // Add layout animation when the list first loads
                layoutAnimation = LayoutAnimationController(
                    AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
                ).apply {
                    delay = 0.15f
                    order = LayoutAnimationController.ORDER_NORMAL
                }
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterForPayment
            }
        }
    }

    fun convertPaymentTransactionsToPaymentRecived(transactions: List<PaymentTransaction>): List<PaymentRecived> {
        return transactions.map { transaction ->
            PaymentRecived(
                // Keep the same ID from the original transaction
                id = transaction.id,

                // Convert amount from Double to Int
                amount = transaction.amount.toInt(),

                // Keep the same date
                date = transaction.date,

                // Default type is RECEIVED
                type = RecivedPaymentType.RECEIVED,

                // Map the payment method directly
                paymentMethod = transaction.type,

                // Default extraChargeType to null
                extraChargeType = null,

                // Copy all the other fields directly
                goldWeight = transaction.goldWeight,
                goldRate = transaction.goldRate,
                silverWeight = transaction.silverWeight,
                silverRate = transaction.silverRate,
                upiService = transaction.upiService,
                fromUpi = transaction.fromUpi,
                toUpi = transaction.toUpi,
                bankName = transaction.bankName,
                fromName = transaction.fromName,
                toName = transaction.toName,
                checkNumber = transaction.checkNumber
            )
        }
    }


    fun convertJewelryItemsToItemModels(jewelryItems: List<JewelryItem>): MutableList<ItemModel> {
        val itemModels = mutableListOf<ItemModel>()

        for (item in jewelryItems) {
            val grossWeight = item.weight.toFloat()
            val netWeight = grossWeight // As per requirement, use same value
            val rateOfJewellery = item.goldRate.toInt()
            val makingCharges = 300
            val makingChargesType = "PER GRAM"

            // Calculate finalMakingCharges: netWeight * goldRate + makingCharges * netWeight
            val finalMakingCharges =
                (netWeight * rateOfJewellery + makingCharges * netWeight).toInt()

            // Calculate totalValue: (netWeight * goldRate) + stoneValue + finalMakingCharges
            val stoneValue = 0
            val totalValue =
                ((netWeight * rateOfJewellery) + stoneValue + finalMakingCharges).toLong()

            val itemModel = ItemModel(
                name = item.name,
                grossWeight = grossWeight,
                netWeight = netWeight,
                rateOfJewellery = rateOfJewellery,
                stoneValue = stoneValue,
                finalMakingCharges = finalMakingCharges,
                makingCharges = makingCharges,
                makingChargesType = makingChargesType,
                piece = 1, // Default to 1 piece
                totalValue = totalValue,
                karat = "22K",
                wastage = 0f,
                isExpanded = false
            )

            itemModels.add(itemModel)
        }

        return itemModels
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConvertOrderToBillBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}