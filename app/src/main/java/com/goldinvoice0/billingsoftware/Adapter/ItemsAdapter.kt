package com.goldinvoice0.billingsoftware.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.R

class ItemsAdapter(
    private val amountTextList: MutableList<String>,
    private val itemList: MutableList<String>,
    private var editItemClickListener: (Int) -> Unit
) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val minusButton: ImageButton = itemView.findViewById(R.id.minusButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val description = amountTextList[position] +" "+ itemList[position]
        holder.descriptionTextView.text = description.toString()
        holder.minusButton.setOnClickListener {
            editItemClickListener.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}