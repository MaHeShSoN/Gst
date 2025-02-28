package com.goldinvoice0.billingsoftware.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.Payment
import com.goldinvoice0.billingsoftware.Model.PaymentMethod
import com.goldinvoice0.billingsoftware.Model.PaymentType
import com.goldinvoice0.billingsoftware.databinding.PaymentLtemLayoutBinding
import java.text.SimpleDateFormat
import java.util.Locale


// Payment Adapter
class PaymentAdapter( private val onDeleteClick: (Payment, Int) -> Unit) :
    ListAdapter<Payment, PaymentAdapter.PaymentViewHolder>(PaymentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PaymentLtemLayoutBinding.inflate(inflater, parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    inner class PaymentViewHolder(
        private val binding: PaymentLtemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment,position: Int) {
            binding.apply {
                // Set payment type
                paymentTypeText.text = payment.type.name

                // Format date
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateText.text = dateFormat.format(payment.date)

                // Set details based on payment type
                when (payment.type) {
                    PaymentMethod.GOLD_EXCHANGE, PaymentMethod.SILVER_EXCHANGE -> {
                        detailsText.visibility = View.VISIBLE
                        detailsText.text = "${payment.weight}g @ ₹${payment.rate}"
//                        valueText.text = "₹${payment.value.format(2)}"
                    }

                    else -> {
                        detailsText.visibility = View.GONE
//                        valueText.text = "₹${payment.value.format(2)}"
                    }
                }

                deleteButton.setOnClickListener {
                    onDeleteClick(payment, position)
                }

            }
        }
    }

    // DiffUtil Callback
    class PaymentDiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }
}