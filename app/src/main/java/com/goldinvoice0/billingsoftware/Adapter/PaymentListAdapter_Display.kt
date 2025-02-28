package com.goldinvoice0.billingsoftware.Adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction
import com.goldinvoice0.billingsoftware.databinding.ItemPaymentTransactionDisplayBinding
import com.goldinvoice0.billingsoftware.format


// PaymentListAdapter.kt
class PaymentListAdapter_Display(private val onItemClick: (PaymentTransaction) -> Unit) :
    ListAdapter<PaymentTransaction, PaymentListAdapter_Display.PaymentViewHolder>(
        PaymentDiffCallback_Display()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PaymentViewHolder {
        val binding = ItemPaymentTransactionDisplayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PaymentViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class PaymentViewHolder(
        private val binding: ItemPaymentTransactionDisplayBinding,
        private val onItemClick: (PaymentTransaction) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: PaymentTransaction) {
            binding.paymentTypeChip.text = payment.type.name.replace("_", " ")
            binding.detailsText.text = payment.getDetailsText()
            binding.valueText.text = "₹${payment.amount.format(2)}"
            binding.dateText.text = payment.date
            // Delete button functionality
            binding.cvItemPayment.setOnClickListener {
                onItemClick(payment)
            }
        }
    }

    private class PaymentDiffCallback_Display : DiffUtil.ItemCallback<PaymentTransaction>() {
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