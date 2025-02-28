package com.goldinvoice0.billingsoftware

import ExpandableAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.databinding.FragmentNewBillBinding


class newBill : Fragment() {
    private var _binding: FragmentNewBillBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JewelryViewModel by activityViewModels()
    private lateinit var adapter: ExpandableAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ExpandableAdapter(
            onEdit = { position, item -> showEditBottomSheet(position, item) },
            onDelete = { position -> viewModel.removeItem(position) }
        )

        binding.recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@newBill.adapter
        }
    }

    private fun setupObservers() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }
    }

//    override fun getTheme(): Int = R.style.FullScreenBottomSheetDialog

    private fun setupClickListeners() {
        binding.addItem.setOnClickListener {
            showAddBottomSheet()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Done -> {
                    findNavController().navigateUp()
                    true
                }

                else -> false
            }
        }
    }

    private fun showAddBottomSheet() {
        ItemInputBottomSheet.newInstance().apply {
            onItemAdded = { item -> viewModel.addItem(item) }
        }.show(childFragmentManager, "JewelryInputBottomSheet")
    }

    private fun showEditBottomSheet(position: Int, item: ItemModel) {
        ItemInputBottomSheet.newInstance(item, position).apply {
            onItemAdded = { updatedItem -> viewModel.updateItem(position, updatedItem) }
        }.show(childFragmentManager, "JewelryInputBottomSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}