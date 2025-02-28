package com.goldinvoice0.billingsoftware.Adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.goldinvoice0.billingsoftware.R
import com.goldinvoice0.billingsoftware.databinding.ItemRowDisplayBinding
class ExpandableAdapter_Display (    private val onItemClick: (ItemModel) -> Unit)  : ListAdapter<ItemModel, ExpandableAdapter_Display.ExpandableViewHolder1>(ItemDiffCallback()) {

    private var expandedPosition = RecyclerView.NO_POSITION

    class ItemDiffCallback : DiffUtil.ItemCallback<ItemModel>() {
        override fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
            return oldItem == newItem  // Compare unique IDs
        }

        override fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
            return oldItem == newItem  // Compare the entire object
        }
    }

    inner class ExpandableViewHolder1(private val binding: ItemRowDisplayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemModel) {
            Log.d("Tag", item.name.toString())

            binding.apply {
                // Bind basic information
                tvName.text = item.name
                tvKarat.text = item.karat

                // Bind weights
                tvGrossWeight.text = "${item.grossWeight}gm"
                tvNetWeight.text = "${item.netWeight}gm"

                // Bind charges and values
                tvRate.text = item.rateOfJewellery.toString()
                tvStoneValue.text = item.stoneValue.toString()
                tvMakingCharges.text = item.makingCharges.toString()
                tvWastage.text = "${item.wastage}%"
                tvTotalValue.text = item.totalValue.toString()

                // Set arrow image based on expanded state
                expandArrow.setImageResource(
                    if (item.isExpanded) R.drawable.keyboard_arrow_down_24px
                    else R.drawable.keyboard_arrow_right_24px
                )

                headerLayout.setOnClickListener {
                    onItemClick(item)
                }

                // Handle expansion click
                expandArrow.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val previousExpandedPosition = expandedPosition

                    if (item.isExpanded) {
                        // Collapsing current item
                        item.isExpanded = false
                        expandedPosition = RecyclerView.NO_POSITION
                    } else {
                        // Collapse previous item if exists
                        if (previousExpandedPosition != RecyclerView.NO_POSITION) {
                            getItem(previousExpandedPosition).isExpanded = false
                            notifyItemChanged(previousExpandedPosition)
                        }
                        // Expand current item
                        item.isExpanded = true
                        expandedPosition = position
                    }

                    // Update arrow
                    expandArrow.setImageResource(
                        if (item.isExpanded) R.drawable.keyboard_arrow_down_24px
                        else R.drawable.keyboard_arrow_right_24px
                    )

                    // Show/hide expandable content
                    expandableLayout.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
                }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableViewHolder1 {
        val binding = ItemRowDisplayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpandableViewHolder1(binding)
    }

    override fun onBindViewHolder(holder: ExpandableViewHolder1, position: Int) {

        holder.bind(getItem(position))
    }
}