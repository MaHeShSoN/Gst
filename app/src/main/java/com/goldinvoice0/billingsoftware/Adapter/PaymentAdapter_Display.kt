package com.goldinvoice0.billingsoftware.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.Payment
import com.goldinvoice0.billingsoftware.Model.PaymentMethod
import com.goldinvoice0.billingsoftware.Model.PaymentType
import com.goldinvoice0.billingsoftware.databinding.PaymentItemLayoutDispalyBinding
import com.goldinvoice0.billingsoftware.format
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentAdapter_Display(
    private val onItemClick: (Payment, Int) -> Unit
) :
    ListAdapter<Payment, PaymentAdapter_Display.PaymentViewHolder2>(PaymentDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder2 {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PaymentItemLayoutDispalyBinding.inflate(inflater, parent, false)
        return PaymentViewHolder2(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder2, position: Int) {
        holder.bind(getItem(position), position)

    }


    inner class PaymentViewHolder2(
        private val binding: PaymentItemLayoutDispalyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment, position: Int) {
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
                        valueText.text = "₹${payment.value.format(2)}"
                    }

                    else -> {
                        detailsText.visibility = View.GONE
                        valueText.text = "₹${payment.value.format(2)}"
                    }
                }
                binding.paymentLayoutDispaly.setOnClickListener {
                    onItemClick(payment,position)
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