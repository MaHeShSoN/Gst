//package com.goldinvoice0.billingsoftware.Adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.cardview.widget.CardView
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.goldinvoice0.billingsoftware.R
//
//
//class PdfDataAdapter(
//    private var pdfDataList: List<PdfFinalData>,
//    private val listener: OnItemClickListener
//) : RecyclerView.Adapter<PdfDataAdapter.PdfDataViewHolder>() {
//
//    // Define the interface for item clicks
//    interface OnItemClickListener {
//        fun onItemClick(pdfData: PdfFinalData)
//    }
//
//    class PdfDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val customerName: TextView = itemView.findViewById(R.id.customerNameLayout)
//        val date: TextView = itemView.findViewById(R.id.date)
//        val amount: TextView = itemView.findViewById(R.id.textForDuePym)
//        val color: TextView = itemView.findViewById(R.id.colourPaidDue)
//        val colourPaidDue: TextView = itemView.findViewById(R.id.colourPaidDue)
//        val cardView: CardView = itemView.findViewById(R.id.cardViewItem)
//
//        // Add other views here
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfDataViewHolder {
//        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.recyclerview_layoutpdf, parent, false)
//        return PdfDataViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: PdfDataViewHolder, position: Int) {
//        val currentItem = pdfDataList[position]
//
//        val date = "Date : ${currentItem.date}"
//        val paymentDue = "₹${currentItem.totalAmount}"
//        val paymentValue = currentItem.totalAmount
//        val paidText = "Paid"
//        val dueText = "Due"
//
//        holder.customerName.text = currentItem.name
//        holder.date.text = date
//        holder.amount.text = paymentDue
//
//        if (paymentValue > 0) {
//            holder.colourPaidDue.text = dueText
//            holder.colourPaidDue.setBackgroundColor(
//                ContextCompat.getColor(
//                    holder.colourPaidDue.context, R.color.Red
//                )
//            )
//        } else if (paymentValue == 0) {
//            holder.colourPaidDue.text = paidText
//            holder.colourPaidDue.setBackgroundColor(
//                ContextCompat.getColor(
//                    holder.colourPaidDue.context, R.color.Green
//                )
//            )
//        }
//
//        // Set the click listener for the item
//        holder.itemView.setOnClickListener {
//            listener.onItemClick(currentItem)
//        }
//
//
//    }
//    fun updateList(newList: List<PdfFinalData>) {
//        pdfDataList = newList
//        notifyDataSetChanged()
//    }
//    override fun getItemCount() = pdfDataList.size
//}