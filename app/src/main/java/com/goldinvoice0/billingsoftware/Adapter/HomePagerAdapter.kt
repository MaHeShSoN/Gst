package com.goldinvoice0.billingsoftware.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldinvoice0.billingsoftware.OrderList
import com.goldinvoice0.billingsoftware.invoiceFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> invoiceFragment()
            1 -> OrderList()
            else -> invoiceFragment()
        }
    }
}
