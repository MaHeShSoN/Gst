package com.goldinvoice0.billingsoftware.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.R

class CustomerListAdapter(private val onItemClick: (Customer) -> Unit) : ListAdapter<Customer, CustomerListAdapter.CustomerViewHolder>(CUSTOMER_COMPARATOR) {


    private var originalList = listOf<Customer>()

    fun setOriginalList(items: List<Customer>) {
        originalList = items
        submitList(items)
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { customer ->
                customer.name.contains(query, ignoreCase = true) ||
                        customer.number.contains(query, ignoreCase = true) ||
                        customer.address.contains(query, ignoreCase = true)
            }
        }
        submitList(filteredList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onItemClick)
    }

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewCustomerName)
        private val emailTextView: TextView = itemView.findViewById(R.id.textViewCustomerNumber)
        private val addressTextView: TextView = itemView.findViewById(R.id.textViewCustomerAddress)

        fun bind(customer: Customer, onItemClick: (Customer) -> Unit) {
            nameTextView.text = customer.name
            emailTextView.text = customer.number
            addressTextView.text = customer.address
            itemView.setOnClickListener { onItemClick(customer) }
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
