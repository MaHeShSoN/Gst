package com.goldinvoice0.billingsoftware.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.goldinvoice0.billingsoftware.databinding.ReceviedPaymentItemBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ReceviedPaymentAdapter(    private val onDeleteClick: (PaymentRecived) -> Unit)
    : ListAdapter<PaymentRecived, ReceviedPaymentAdapter.PaymentRecivedViewHolder>(PaymentRecivedDiffCallback()) {


    inner class PaymentRecivedViewHolder(private val binding: ReceviedPaymentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(PaymentRecived: PaymentRecived) {
            binding.apply {
                val amount = formatNumberToIndian(PaymentRecived.amount.toLong())
//                 Handle paymentMethod and extraChargeType
                when {
                    PaymentRecived.paymentMethod == null -> {
                        tvPaymentType.text = PaymentRecived.extraChargeType.toString()
                        tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(PaymentRecived.date))
                        tvAmount.text = "₹$amount"
                        tvType.text = PaymentRecived.type.toString()
                    }
                    PaymentRecived.extraChargeType == null -> {
                        tvAmount.text = "₹$amount"
                        tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(PaymentRecived.date))
                        tvType.text = PaymentRecived.type.toString()


                        tvPaymentTypeText.text = PaymentRecived.getDetailsText()
                        tvPaymentType.text = PaymentRecived.paymentMethod.toString()
                    }
                    else -> {
                        tvPaymentType.text = "Unknown" // or some default value
                    }
                }

                // Setup delete button
                btnDelete.setOnClickListener {
                    onDeleteClick(PaymentRecived)
                }

            }

        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentRecivedViewHolder {
        val binding = ReceviedPaymentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PaymentRecivedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentRecivedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

fun formatNumberToIndian(number: Long): String {
    val formatter = DecimalFormat("##,##,###")
    return formatter.format(number)
}

class PaymentRecivedDiffCallback : DiffUtil.ItemCallback<PaymentRecived>() {
    override fun areItemsTheSame(oldItem: PaymentRecived, newItem: PaymentRecived) =
        oldItem == newItem
    override fun areContentsTheSame(oldItem: PaymentRecived, newItem: PaymentRecived) =
        oldItem == newItem
}