package com.goldinvoice0.billingsoftware.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.DeliveryStatus
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.JewelryType
import com.goldinvoice0.billingsoftware.R
import com.goldinvoice0.billingsoftware.convertes.JewelryItemListConverter
import com.goldinvoice0.billingsoftware.databinding.RvItemJewellery00DisplayBinding


class JewelleryItemAdapter_Display(
    private val onItemClick: (JewelryItem, Int) -> Unit

) : ListAdapter<JewelryItem, JewelleryItemAdapter_Display.JewelryViewHolder>(JewelryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JewelryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemJewellery00DisplayBinding.inflate(inflater, parent, false)
        return JewelryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JewelryViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class JewelryViewHolder(
        private val binding: RvItemJewellery00DisplayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: JewelryItem, position: Int) {
            binding.apply {
                // Set basic info
                jewelryNameText.text = item.name
                typeText.text = "Type: ${item.type.name}"
                weightAndRateText.text =
                    "Weight: ${item.weight}g | Rate: ₹${item.goldRate}"

                // Handle ring size
                if (item.type == JewelryType.RING && item.size != null) {
                    sizeText.visibility = View.VISIBLE
                    sizeText.text = "Size: ${item.size}"
                } else {
                    sizeText.visibility = View.GONE
                }

                // Set notes if available
                notesText.visibility = if (item.notes.isNullOrEmpty()) View.GONE else View.VISIBLE
                notesText.text = item.notes

                jewelleryItemDisplay.setOnClickListener {
                    onItemClick(item,position)
                }

                // Setup status chip
                setupStatusChip(item)

            }
        }

        private fun setupStatusChip(item: JewelryItem) {
            binding.statusChip.apply {
                text = item.status.name
                setChipBackgroundColorResource(
                    when (item.status) {
                        DeliveryStatus.PENDING -> R.color.status_pending
                        DeliveryStatus.IN_PROGRESS -> R.color.status_in_progress
                        DeliveryStatus.COMPLETED -> R.color.status_completed
                    }
                )
            }
        }
    }

    class JewelryDiffCallback : DiffUtil.ItemCallback<JewelryItem>() {
        override fun areItemsTheSame(oldItem: JewelryItem, newItem: JewelryItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: JewelryItem, newItem: JewelryItem): Boolean {
            return oldItem == newItem
        }
    }
}