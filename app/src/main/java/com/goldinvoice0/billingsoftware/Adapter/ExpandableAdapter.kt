import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.R
import com.goldinvoice0.billingsoftware.databinding.ItemRowBinding

class ExpandableAdapter(
    private val onEdit: (Int, ItemModel) -> Unit,
    private val onDelete: (Int) -> Unit
) : ListAdapter<ItemModel, ExpandableAdapter.ExpandableViewHolder>(ItemDiffCallback()) {

    private var expandedPosition = RecyclerView.NO_POSITION

    class ItemDiffCallback : DiffUtil.ItemCallback<ItemModel>() {
        override fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
            return oldItem == newItem  // Compare unique IDs
        }

        override fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
            return oldItem == newItem  // Compare the entire object
        }
    }

    inner class ExpandableViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemModel) {
            binding.apply {
                tvName.text = item.name
                tvKarat.text = item.karat
                tvGrossWeight.text = "${item.grossWeight}gm"
                tvNetWeight.text = "${item.netWeight}gm"
                tvRate.text = item.rateOfJewellery.toString()
                tvStoneValue.text = item.stoneValue.toString()
                tvMakingCharges.text = item.finalMakingCharges.toString()
                tvWastage.text = "${item.wastage}%"
                tvTotalValue.text = item.totalValue.toString()

                // Handle expansion
                expandArrow.setImageResource(
                    if (item.isExpanded) R.drawable.keyboard_arrow_down_24px
                    else R.drawable.keyboard_arrow_right_24px
                )

                expandableLayout.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

                headerLayout.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                    val previousExpandedPosition = expandedPosition

                    if (item.isExpanded) {
                        item.isExpanded = false
                        expandedPosition = RecyclerView.NO_POSITION
                    } else {
                        // Safely collapse previously expanded item
                        if (previousExpandedPosition != RecyclerView.NO_POSITION &&
                            previousExpandedPosition < currentList.size) {
                            getItem(previousExpandedPosition).isExpanded = false
                            notifyItemChanged(previousExpandedPosition)
                        }
                        item.isExpanded = true
                        expandedPosition = position
                    }

                    expandArrow.setImageResource(
                        if (item.isExpanded) R.drawable.keyboard_arrow_down_24px
                        else R.drawable.keyboard_arrow_right_24px
                    )

                    expandableLayout.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
                }

                btnEdit.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onEdit(position, item)
                    }
                }

                btnDelete.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onDelete(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpandableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpandableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}