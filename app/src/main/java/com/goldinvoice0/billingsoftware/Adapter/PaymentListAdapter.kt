package com.goldinvoice0.billingsoftware.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction
import com.goldinvoice0.billingsoftware.databinding.ItemPaymentTransactionBinding
import com.goldinvoice0.billingsoftware.format


// PaymentListAdapter.kt
class PaymentListAdapter(private val onDeleteClick: (PaymentTransaction) -> Unit) :
    ListAdapter<PaymentTransaction, PaymentListAdapter.PaymentViewHolder>(PaymentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PaymentViewHolder {
        val binding = ItemPaymentTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PaymentViewHolder(binding,onDeleteClick)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun addPayment(payment: PaymentTransaction) {
        val currentList = currentList.toMutableList()
        currentList.add(payment)
        submitList(currentList)
    }


    fun removePayment(payment: PaymentTransaction) {
        val currentList = currentList.toMutableList()
        currentList.remove(payment)
        submitList(currentList)
    }

    class PaymentViewHolder(
        private val binding: ItemPaymentTransactionBinding,
        private val onDeleteClick: (PaymentTransaction) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: PaymentTransaction) {
            binding.paymentTypeChip.text = payment.type.name.replace("_", " ")
            binding.detailsText.text = payment.getDetailsText()
            binding.valueText.text = "₹${payment.amount.format(2)}"
            binding.dateText.text = payment.date
            // Delete button functionality
            binding.btnDelete.setOnClickListener {
                onDeleteClick(payment)
            }
        }
    }

    private class PaymentDiffCallback : DiffUtil.ItemCallback<PaymentTransaction>() {
        override fun areItemsTheSame(
            oldItem: PaymentTransaction,
            newItem: PaymentTransaction
        ): Boolean {
            // In a real app, you might want to add an ID field to PaymentTransaction
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PaymentTransaction,
            newItem: PaymentTransaction
        ): Boolean {
            return oldItem == newItem
        }
    }
}