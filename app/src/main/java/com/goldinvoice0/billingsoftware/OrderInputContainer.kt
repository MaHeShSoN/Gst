package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Adapter.PaymentListAdapter
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction
import com.goldinvoice0.billingsoftware.databinding.FragmentOrderInputContainerBinding


// OrderInputContainer.kt
class OrderInputContainer : Fragment(), AdvancePaymentInputBottomSheetFragment.PaymentAddListener {
    private var _binding: FragmentOrderInputContainerBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Initialize payments list immediately
    private var payments: MutableList<PaymentTransaction> = mutableListOf()

    private val paymentAdapter = PaymentListAdapter { paymentTransaction ->
        HandleDeleteClick(paymentTransaction)
    }

    private fun HandleDeleteClick(paymentTransaction: PaymentTransaction) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Payment")
            .setMessage("Are you sure you want to delete this payment?")
            .setPositiveButton("Delete") { _, _ ->
                sharedViewModel.removePaymentAdvance(paymentTransaction)
                payments.remove(paymentTransaction) // Update local list
                paymentAdapter.removePayment(paymentTransaction)
                updateTotalAmount() // Use the new method name
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderInputContainerBinding.inflate(inflater, container, false)

        setupInitialState() // New method to setup initial state
        setupClickListeners() // New method for click listeners

        return binding.root
    }

    private fun setupInitialState() {
        // Initialize with empty state
        updateTotalAmount()
        paymentAdapter.submitList(emptyList())
    }

    private fun setupClickListeners() {
        binding.addPaymentButton.setOnClickListener {
            val bottomSheet = AdvancePaymentInputBottomSheetFragment()
            bottomSheet.setPaymentAddListener(this)
            bottomSheet.show(parentFragmentManager, "PaymentBottomSheet")
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Done -> {
                    findNavController().popBackStack()
                    true
                }

                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (arguments?.getBoolean("EditAdvancePayment") == true) {
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        // Observe payments from ViewModel
        sharedViewModel.paymentsAdvance.observe(viewLifecycleOwner) { paymentsList ->
            payments = paymentsList.toMutableList()
            paymentAdapter.submitList(paymentsList)
            updateTotalAmount()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
//        setupScrollListener()
    }

    private fun setupRecyclerView() {
        binding.paymentsRecyclerView.apply {
            adapter = paymentAdapter
            layoutManager = LinearLayoutManager(requireContext())

            layoutAnimation = LayoutAnimationController(
                AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            ).apply {
                delay = 0.15f
                order = LayoutAnimationController.ORDER_NORMAL
            }
        }
    }



    private fun updateTotalAmount() {
        try {
            val totalAmount = payments.sumOf { it.amount }
            binding.totalAdvanceText.text = "₹${totalAmount.format(2)}"
        } catch (e: Exception) {
            binding.totalAdvanceText.text = "₹0.00"
            e.printStackTrace()
        }
    }

    override fun onPaymentAdded(payment: PaymentTransaction) {
        try {
            payments.add(payment)
            sharedViewModel.addPaymentAdvance(payment)
            paymentAdapter.addPayment(payment)
            updateTotalAmount()
        } catch (e: Exception) {
            // Handle error (maybe show a toast or snackbar)
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        binding.paymentsRecyclerView.clearAnimation()
        binding.paymentsRecyclerView.removeAllViews()
        _binding = null
        super.onDestroyView()
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)