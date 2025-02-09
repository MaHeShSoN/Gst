package com.goldinvoice0.billingsoftware.Adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.R
import com.goldinvoice0.billingsoftware.databinding.ItemRowBinding



class ExpandableAdapter(
    private val items: List<ItemModel>,
    private val onEdit: (Int,ItemModel) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ExpandableAdapter.ExpandableViewHolder>() {
    // Track the currently expanded position
    private var expandedPosition = RecyclerView.NO_POSITION

    inner class ExpandableViewHolder(private val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemModel) {
            binding.jewellerynameItem001.text = item.name
            binding.weightItem001.text = item.netWeight.toString()
            binding.labourItem001.text = item.makingCharges.toString()
            binding.rateItem001.text = item.rateOfJewellery.toString()
            binding.totalItem001.text = item.totalValue.toString()

            // Set arrow image based on expanded state
            binding.arrowButton.setImageResource(
                if (item.isExpanded) R.drawable.keyboard_arrow_down_24px
                else R.drawable.keyboard_arrow_right_24px
            )

            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                val previousExpandedPosition = expandedPosition

                if (item.isExpanded) {
                    // Collapsing current item
                    item.isExpanded = false
                    expandedPosition = RecyclerView.NO_POSITION
                } else {
                    // Collapse previous item if exists
                    if (previousExpandedPosition != RecyclerView.NO_POSITION) {
                        items[previousExpandedPosition].isExpanded = false
                        notifyItemChanged(previousExpandedPosition)
                    }
                    // Expand current item
                    item.isExpanded = true
                    expandedPosition = position
                }

                // Simply update the image resource instead of rotating
                binding.arrowButton.setImageResource(
                    if (item.isExpanded) R.drawable.keyboard_arrow_down_24px
                    else R.drawable.keyboard_arrow_right_24px
                )

                notifyItemChanged(position)
            }

            // Change background color based on expansion state
            val expandedColor = ContextCompat.getColor(binding.root.context, R.color.colorAccent)
            val defaultColor = ContextCompat.getColor(binding.root.context, R.color.white)
            binding.jewellerynameItem001.setBackgroundColor(if (item.isExpanded) expandedColor else defaultColor)
            binding.arrowButton.setBackgroundColor(if (item.isExpanded) expandedColor else defaultColor)

            // Show/Hide Edit/Delete Buttons
            binding.expandableLayoutHiddShow.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

            // Edit Button
            binding.buttonEdit.setOnClickListener {
                onEdit(absoluteAdapterPosition, item)
            }

            // Delete Button
            binding.buttonDelete.setOnClickListener {
                onDelete(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpandableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpandableViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}