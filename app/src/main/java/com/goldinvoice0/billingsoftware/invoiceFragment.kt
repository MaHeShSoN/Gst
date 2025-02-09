package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.PdfDataAdapter
import com.goldinvoice0.billingsoftware.Model.PdfFinalData
import com.goldinvoice0.billingsoftware.ViewModel.PdfFinalDataViewModel
import com.goldinvoice0.billingsoftware.ViewModel.SearchViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentInvoiceBinding


class invoiceFragment : Fragment(), PdfDataAdapter.OnItemClickListener {


    private var _binding: FragmentInvoiceBinding? = null
    private val binding get() = _binding!!
    private val shopViewModel: ShopViewModel by viewModels()
    private val pdfFinalDataViewModel: PdfFinalDataViewModel by viewModels()
    private lateinit var pdfDataAdapter: PdfDataAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var originalList: List<PdfFinalData> = emptyList()
    val searchViewModel: SearchViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        binding.recyclerViewMain.visibility = View.GONE
        binding.placeHolderView.visibility = View.GONE

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        searchViewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            // Fetch and filter data based on the query
            performSearch(query)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupRecyclerView() {
        pdfDataAdapter = PdfDataAdapter(emptyList(), this)
        binding.recyclerViewMain.apply {
            adapter = pdfDataAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun setupObservers() {
        // Observe shop data


        // Observe PDF data
        pdfFinalDataViewModel.getAllPdfFinalData { pdfList ->
            originalList = pdfList
            pdfDataAdapter.updateList(pdfList)
            updateVisibility(pdfList.isNotEmpty())
            Log.d("Tag", "Received data: ${pdfList.map { it.name }}")
        }

    }

    private fun updateVisibility(hasData: Boolean) {
        binding.recyclerViewMain.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.placeHolderView.visibility = if (hasData) View.GONE else View.VISIBLE
    }

    private fun setupClickListeners() {
        binding.addButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("FromOrderList", false)

            val parentNavController = requireActivity().findNavController(R.id.nav_host_fragment)
            parentNavController.navigate(R.id.action_mainScreen_to_customerList, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performSearch(query: String) {
        Log.d("Tag", "Search query: $query")
        Log.d("Tag", "Original list size: ${originalList.size}")
        if (query.isEmpty()) {
            pdfDataAdapter.updateList(originalList)
            return
        }

        val filteredList = originalList.filter { pdfFinalData ->
            pdfFinalData.name.contains(query, ignoreCase = true) ||
                    pdfFinalData.address.contains(query, ignoreCase = true) ||
                    pdfFinalData.phone.contains(query, ignoreCase = true) ||
                    pdfFinalData.date.contains(query, ignoreCase = true) ||
                    pdfFinalData.invoiceNumber.contains(query, ignoreCase = true)
        }
        Log.d("TAG", filteredList.size.toString())
        pdfDataAdapter.updateList(filteredList)
    }

    override fun onItemClick(pdfData: PdfFinalData) {
        sharedViewModel.setReceivedList(pdfData)
        val parentNavController = requireActivity().findNavController(R.id.nav_host_fragment)
        parentNavController.navigate(R.id.action_mainScreen_to_listBillView)
//        findNavController().navigate(R.id.action_mainScreen_to_listBillView)
    }

}