package com.goldinvoice0.billingsoftware.Adapter



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.DeliveryStatus
import com.goldinvoice0.billingsoftware.Model.PdfFinalData
import com.google.android.material.chip.Chip
import com.google.android.material.card.MaterialCardView
import com.goldinvoice0.billingsoftware.R
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter  (private var orders: List<Order>,
                     private val onItemClick: (Order) -> Unit
    ) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        val tvCustomerNumber: TextView = itemView.findViewById(R.id.tvCustomerNumber)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvDeliveryDate: TextView = itemView.findViewById(R.id.tvDeliveryDate)
        val chipDeliveryStatus: Chip = itemView.findViewById(R.id.chipDeliveryStatus)
        val tvTotalItems: TextView = itemView.findViewById(R.id.tvTotalItems)
        val tvTotalPrePayment: TextView = itemView.findViewById(R.id.tvTotalPrePayment)
        val view : MaterialCardView = itemView.findViewById(R.id.orderlist_rvitem_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_list_rv_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]


        holder.tvCustomerName.text = order.customerName
        holder.tvCustomerNumber.text = order.customerNumber
        holder.tvAddress.text = order.address
        holder.tvDeliveryDate.text = "${order.deliveryDate}"

        // Set total jewelry items count
        holder.tvTotalItems.text = "${order.jewelryItems.size}"

        // Calculate total pre-payment
        val totalPayment = order.payments.sumOf { it.value }
        holder.tvTotalPrePayment.text = "₹${String.format("%.2f", totalPayment)}"

        // Set delivery status and chip color
        when (order.deliveryStatus) {
            DeliveryStatus.PENDING -> {
                holder.chipDeliveryStatus.text = "PENDING"
                holder.chipDeliveryStatus.setChipBackgroundColorResource(R.color.Red)
            }
            DeliveryStatus.IN_PROGRESS -> {
                holder.chipDeliveryStatus.text = "IN PROGRESS"
                holder.chipDeliveryStatus.setChipBackgroundColorResource(R.color.light_blue_600)
            }
            DeliveryStatus.COMPLETED -> {
                holder.chipDeliveryStatus.text = "COMPLETED"
                holder.chipDeliveryStatus.setChipBackgroundColorResource(R.color.Green)
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

