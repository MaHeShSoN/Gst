package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.goldinvoice0.billingsoftware.Adapter.HomePagerAdapter
//import com.goldinvoice0.billingsoftware.Adapter.PdfDataAdapter
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.SearchViewModel
import com.goldinvoice0.billingsoftware.ViewModel.SharedNavigationViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentBillsBinding
import com.google.android.material.tabs.TabLayoutMediator


class Bills : Fragment() {

    private var _binding: FragmentBillsBinding? = null
    private val binding get() = _binding!!
    private val shopViewModel: ShopViewModel by viewModels()
//    private lateinit var pdfDataAdapter: PdfDataAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    val searchViewModel: SearchViewModel by activityViewModels()
    private val vm: SharedNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBillsBinding.inflate(inflater, container, false)


        setupToolbar()
        setUpSerachView()

        shopViewModel.shop.observe(viewLifecycleOwner) { shop ->
            updateHeaderView(shop)
        }

        binding.topAppBar.setNavigationOnClickListener {
            val parentNavController = requireActivity().findNavController(R.id.nav_host_fragment)
            parentNavController.navigate(R.id.action_mainScreen_to_storeFragmentEdit)
        }

        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Bills"
                1 -> "Orders"
                else -> "Bills"
            }
        }.attach()





        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewPager.adapter = HomePagerAdapter(this)

        vm.requestedTab.observe(viewLifecycleOwner) { position ->
            binding.viewPager.setCurrentItem(position, false)
        }
    }
    // Function to switch ViewPager2 tab to Orders
    private fun setupToolbar() {
        // Enable options menu in fragment
        setHasOptionsMenu(true)
        binding.appBarLayout.isLiftOnScroll = true
    }


    private fun updateHeaderView(shop: Shop) {
        binding.topAppBar.title = shop.shopName
        binding.topAppBar.subtitle = shop.gstNumber
    }

    private fun setUpSerachView() {
        val searchBar = binding.topAppBar.menu.findItem(R.id.action_search).actionView as SearchView
        searchBar.queryHint = "Search Bills..."
        searchBar.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS


        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchViewModel.setSearchQuery(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchViewModel.setSearchQuery(newText)
                }
                return false
            }
        })
        searchBar.setOnCloseListener {
            searchBar.onActionViewCollapsed()
            binding.topAppBar.setNavigationIcon(R.drawable.store_24px)

            true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}