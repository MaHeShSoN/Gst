package com.goldinvoice0.billingsoftware.Adapter

import android.graphics.Color
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.R

class CustomerListAdapter(
    private val onItemClick: (Customer) -> Unit,
    private val onDeleteClick: (Customer, Int) -> Unit,
    private val onEditClick: (Customer) -> Unit

) :
    ListAdapter<Customer, CustomerListAdapter.CustomerViewHolder>(CUSTOMER_COMPARATOR) {


    private var originalList = listOf<Customer>()
    private var searchQuery: String = ""
    private var filteredList = listOf<Customer>()
    fun setOriginalList(items: List<Customer>) {
        originalList = items
        submitList(items)
    }

    //    fun filter(query: String) {
//        val filteredList = if (query.isEmpty()) {
//            originalList
//        } else {
//            originalList.filter { customer ->
//                customer.name.contains(query, ignoreCase = true) ||
//                        customer.number.contains(query, ignoreCase = true) ||
//                        customer.address.contains(query, ignoreCase = true)
//            }
//        }
//        submitList(filteredList)
//    }
//    fun filter(query: String) {
//        searchQuery = query
//        val filteredList = if (query.isEmpty()) {
//            originalList
//        } else {
//            originalList.filter { customer ->
//                customer.name.contains(query, ignoreCase = true) ||
//                        customer.number.contains(query, ignoreCase = true) ||
//                        customer.address.contains(query, ignoreCase = true)
//            }
//        }
//        submitList(filteredList)
//    }
    fun filter(query: String) {
        searchQuery = query.trim()
        filteredList = if (searchQuery.isEmpty()) {
            originalList
        } else {
            originalList.filter { customer ->
                customer.name.contains(searchQuery, ignoreCase = true) ||
                        customer.number.contains(searchQuery, ignoreCase = true) ||
                        customer.address.contains(searchQuery, ignoreCase = true)
            }
        }
        submitList(filteredList)
        notifyDataSetChanged() // Force refresh to ensure highlighting updates
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onItemClick, onDeleteClick, onEditClick, searchQuery)
    }


    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewCustomerName)
        private val emailTextView: TextView = itemView.findViewById(R.id.tv_phone)
        private val addressTextView: TextView = itemView.findViewById(R.id.tv_street)
        private val addressTextView2: TextView = itemView.findViewById(R.id.tv_city_state)
        private val customerId: TextView = itemView.findViewById(R.id.tv_customer_id)
        private val editBtn: Button = itemView.findViewById(R.id.btn_edit)
        private val deleteBtn: Button = itemView.findViewById(R.id.btn_delete)
        private val image: ImageView = itemView.findViewById(R.id.iv_profile_photo)

        fun bind(
            customer: Customer,
            onItemClick: (Customer) -> Unit,
            onDeleteClick: (Customer, Int) -> Unit,
            onEditClick: (Customer) -> Unit,
            searchQuery: String // Passing search query
        ) {

            Log.d("SeaechQuiry", searchQuery)
            // Set regular text first as fallback
            nameTextView.text = customer.name

            // Only attempt highlighting if search query isn't empty
            if (searchQuery.isNotEmpty()) {
                // Handle name highlighting
                if (customer.name.contains(searchQuery, ignoreCase = true)) {
                    val spannable = SpannableString(customer.name)
                    val startPos = customer.name.lowercase().indexOf(searchQuery.lowercase())
                    if (startPos >= 0) {
                        spannable.setSpan(
                            BackgroundColorSpan(Color.YELLOW),
                            startPos,
                            startPos + searchQuery.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(
                            ForegroundColorSpan(Color.BLACK),
                            startPos,
                            startPos + searchQuery.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        nameTextView.text = spannable
                    }
                }
            }

            // Rest of your binding code remains the same
            emailTextView.text = customer.number
            addressTextView.text = customer.address
            addressTextView2.text = customer.address2
            customerId.text = "ID:${customer.customerId}"

            itemView.setOnClickListener { onItemClick(customer) }
            editBtn.setOnClickListener { onEditClick(customer) }
            deleteBtn.setOnClickListener { onDeleteClick(customer, absoluteAdapterPosition) }

            // Load image with Glide
            val imageUri = Uri.parse(customer.imageUrl)
            try {
                if (customer.imageUrl.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(imageUri)
                        .fallback(R.drawable.account_circle_24px)
                        .into(image)
                } else {
                    image.setImageResource(R.drawable.account_circle_24px)
                }
            } catch (e: Exception) {
                Log.e("CustomerAdapter", "Error loading image: ${e.message}")
                image.setImageResource(R.drawable.account_circle_24px)
            }
        }
    }


    companion object {
        private val CUSTOMER_COMPARATOR = object : DiffUtil.ItemCallback<Customer>() {
            override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
                return oldItem == newItem
            }
        }
    }
}
