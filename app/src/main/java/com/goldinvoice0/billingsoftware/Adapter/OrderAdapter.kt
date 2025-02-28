package com.goldinvoice0.billingsoftware.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.DeliveryStatus
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit
) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        val tvDeliveryDate: TextView = itemView.findViewById(R.id.tvDeliveryDate)
        val chipDeliveryStatus: Chip = itemView.findViewById(R.id.chipDeliveryStatus)
        val tvTotalItems: TextView = itemView.findViewById(R.id.tvTotalItems)
        val tvTotalPrePayment: TextView = itemView.findViewById(R.id.tvTotalPrePayment)
        val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        val view: MaterialCardView = itemView.findViewById(R.id.orderlist_rvitem_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_list_rv_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]


        holder.tvCustomerName.text = order.customerName
        holder.tvOrderNumber.text = "#${order.orderNumber}"
        holder.tvDeliveryDate.text = "${order.deliveryDate}"

        // Set total jewelry items count
        holder.tvTotalItems.text = "${order.jewelryItems.size}"

        // Calculate total pre-payment
        val totalPayment = order.payments.sumOf { it.amount }
        holder.tvTotalPrePayment.text = "₹${String.format("%.2f", totalPayment)}"


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
                holder.chipDeliveryStatus.text = "PENDING"
                holder.chipDeliveryStatus.setChipBackgroundColorResource(R.color.status_pending)
            }

            DeliveryStatus.IN_PROGRESS -> {
                holder.chipDeliveryStatus.text = "IN PROGRESS"
                holder.chipDeliveryStatus.setChipBackgroundColorResource(R.color.status_in_progress)
            }

            DeliveryStatus.COMPLETED -> {
                holder.chipDeliveryStatus.text = "COMPLETED"
                holder.chipDeliveryStatus.setChipBackgroundColorResource(R.color.status_completed)
            }
        }
        holder.view.setOnClickListener {
            onItemClick(order)
        }
    }

    fun updateList(newList: List<Order>) {
        orders = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = orders.size

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

