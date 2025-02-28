package com.goldinvoice0.billingsoftware.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.BillInputs
import com.goldinvoice0.billingsoftware.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import java.text.NumberFormat
import java.util.Locale

class BillAdapter(
                  private val onClicked: (BillInputs) -> Unit) :
    ListAdapter<BillInputs, BillAdapter.BillViewHolder>(BillDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_layoutpdf, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        // Add loading animation for each item
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 50f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay(position * 50L)
            .start()

        holder.bind(getItem(position))
    }

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvInvoiceNumber: TextView = itemView.findViewById(R.id.tvInvoiceNumber)
        private val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        private val tvReceivedAmount: TextView = itemView.findViewById(R.id.tvReceivedAmount)
        private val tvDueAmount: TextView = itemView.findViewById(R.id.tvDueAmount)
        private val tvBillingDate: TextView = itemView.findViewById(R.id.tvBillingDate)
        private val tvCard: MaterialCardView = itemView.findViewById(R.id.pdfCardViewLayout)
        private val cpPaymentStatus: Chip = itemView.findViewById(R.id.chipPaymentStatus)


        fun bind(bill: BillInputs) {
            val indianCurrency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            // Animate text changes
            animateTextChange(tvInvoiceNumber, "#${bill.invoiceNumber}")
            animateTextChange(tvCustomerName, bill.customerName)
            animateTextChange(tvTotalAmount, indianCurrency.format(bill.totalAmount))
            animateTextChange(tvReceivedAmount, indianCurrency.format(bill.receviedAmount))
            animateTextChange(tvDueAmount, indianCurrency.format(bill.dueAmount))
            animateTextChange(tvBillingDate, bill.date)





            // Animate chip status change
            cpPaymentStatus.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    when (bill.status) {
                        "Pending" -> {
                            cpPaymentStatus.text = "Pending"
                            cpPaymentStatus.setChipBackgroundColorResource(R.color.Red)
                        }
                        "Paid" -> {
                            cpPaymentStatus.text = "Paid"
                            cpPaymentStatus.setChipBackgroundColorResource(R.color.status_completed)
                        }
                        "Partially Paid" -> {
                            cpPaymentStatus.text = "Partially Paid"
                            cpPaymentStatus.setChipBackgroundColorResource(R.color.status_pending)
                        }
                    }
                    cpPaymentStatus.animate()
                        .alpha(1f)
                        .setDuration(150)
                        .start()
                }
                .start()





            cpPaymentStatus.text = bill.status


            tvCard.setOnClickListener {
                onClicked(bill)
            }

        }
        private fun animateTextChange(textView: TextView, newText: String) {
            textView.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    textView.text = newText
                    textView.animate()
                        .alpha(1f)
                        .setDuration(150)
                        .start()
                }
                .start()
        }
    }
}

class BillDiffCallback : DiffUtil.ItemCallback<BillInputs>() {
    override fun areItemsTheSame(oldItem: BillInputs, newItem: BillInputs): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BillInputs, newItem: BillInputs): Boolean {
        return oldItem == newItem
    }
}