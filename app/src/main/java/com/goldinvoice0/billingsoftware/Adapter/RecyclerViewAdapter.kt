package com.goldinvoice0.billingsoftware.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.R


class RecyclerViewAdapter(private val itemList: List<ItemModel>, private var editItemClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.itemNamRV)
        val ntWtTV : TextView = itemView.findViewById(R.id.ntWtRV)
        val makCharTv : TextView = itemView.findViewById(R.id.makingChargesRV)
        val totalTv : TextView = itemView.findViewById(R.id.totalRV)
        val editButton : ImageButton = itemView.findViewById(R.id.editButtonNI001)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }
//    fun setDeleteClickListener(clickListener: (Int) -> Unit) {
//        deleteItemClickListener = clickListener
//    }
//    fun setEditClickListener(clickListener: (Int) -> Unit) {
//        editItemClickListener = clickListener
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.nameTextView.text = currentItem.name.toString()
        holder.ntWtTV.text = currentItem.netWeight.toString()
        holder.makCharTv.text = currentItem.makingCharges.toString()
        holder.totalTv.text = currentItem.totalValue.toString()
        holder.editButton.setOnClickListener {
            editItemClickListener.invoke(position)
        }

        // Set other TextViews with data (gross weight, net weight, etc.)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}
