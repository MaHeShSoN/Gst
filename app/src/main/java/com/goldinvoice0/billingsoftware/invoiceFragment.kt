package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.BillAdapter
import com.goldinvoice0.billingsoftware.Model.BillInputs
import com.goldinvoice0.billingsoftware.ViewModel.BillInputViewModel
//import com.goldinvoice0.billingsoftware.Adapter.PdfDataAdapter
import com.goldinvoice0.billingsoftware.ViewModel.SearchViewModel
import com.goldinvoice0.billingsoftware.ViewModel.SharedNavigationViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentInvoiceBinding


class invoiceFragment : Fragment() {


    private var _binding: FragmentInvoiceBinding? = null
    private val binding get() = _binding!!
    private val shopViewModel: ShopViewModel by viewModels()
//    private lateinit var pdfDataAdapter: PdfDataAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    val searchViewModel: SearchViewModel by activityViewModels()
    private val viewModel: JewelryViewModel by activityViewModels()
    private val vm: SharedNavigationViewModel by activityViewModels()
    var billAdapter = BillAdapter(
        onClicked = {
            onClicked(it)
        }
    )

    // In your Fragment/Activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewMain.apply {
            // Add layout animation when the list first loads
            layoutAnimation = LayoutAnimationController(
                AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            ).apply {
                delay = 0.15f
                order = LayoutAnimationController.ORDER_NORMAL
            }

            adapter = billAdapter
        }
    }


    private fun onClicked(it: BillInputs) {
        val bundle = Bundle()
        bundle.putInt("billId",it.id)
        Log.d("billId",it.id.toString())

        vm.requestTab(0)
        val parentNavController = requireActivity().findNavController(R.id.nav_host_fragment)
        parentNavController.navigate(R.id.action_mainScreen_to_listBillView,bundle)

        Log.d("DEBUG",it.customerName)
    }

    val billInputViewModel: BillInputViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        binding.recyclerViewMain.visibility = View.GONE
        binding.placeHolderView.visibility = View.GONE

        setupClickListeners()

        setupObservers()
        setupRecyclerView()

//        searchViewModel.searchQuery.observe(viewLifecycleOwner) { query ->
//            // Fetch and filter data based on the query
//            performSearch(query)
//        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupRecyclerView() {
        billAdapter = BillAdapter(
            onClicked = {
                onClicked(it)
            }
        )
        binding.recyclerViewMain.apply {
            adapter = billAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun setupObservers() {

        billInputViewModel.getAllBillInput().observe(viewLifecycleOwner){
            billAdapter.submitList(it)
            updateVisibility(it.isNotEmpty())
        }


        // Observe Bill data
//        pdfFinalDataViewModel.getAllPdfFinalData { pdfList ->
//            originalList = pdfList
//            pdfDataAdapter.updateList(pdfList)
//            Log.d("Tag", "Received data: ${pdfList.map { it.name }}")
//        }

    }

    private fun updateVisibility(hasData: Boolean) {
        binding.recyclerViewMain.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.placeHolderView.visibility = if (hasData) View.GONE else View.VISIBLE
    }

    private fun setupClickListeners() {
        binding.addButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("FromOrderList", false)
            vm.requestTab(0)
            val parentNavController = requireActivity().findNavController(R.id.nav_host_fragment)
            parentNavController.navigate(R.id.action_mainScreen_to_billsInputsFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun performSearch(query: String) {
//        Log.d("Tag", "Search query: $query")
//        Log.d("Tag", "Original list size: ${originalList.size}")
//        if (query.isEmpty()) {
//            pdfDataAdapter.updateList(originalList)
//            return
//        }
//
//        val filteredList = originalList.filter { pdfFinalData ->
//            pdfFinalData.name.contains(query, ignoreCase = true) ||
//                    pdfFinalData.address.contains(query, ignoreCase = true) ||
//                    pdfFinalData.phone.contains(query, ignoreCase = true) ||
//                    pdfFinalData.date.contains(query, ignoreCase = true) ||
//                    pdfFinalData.invoiceNumber.contains(query, ignoreCase = true)
//        }
//        Log.d("TAG", filteredList.size.toString())
//        pdfDataAdapter.updateList(filteredList)
//    }

//    override fun onItemClick(pdfData: PdfFinalData) {
//        sharedViewModel.setReceivedList(pdfData)
//        val parentNavController = requireActivity().findNavController(R.id.nav_host_fragment)
//        parentNavController.navigate(R.id.action_mainScreen_to_listBillView)
////        findNavController().navigate(R.id.action_mainScreen_to_listBillView)
//    }


    override fun onStart() {
        super.onStart()
        sharedViewModel.clearRevivedPayments()
        sharedViewModel.clearPayments()
        sharedViewModel.clearJewelryItem()
        viewModel.clearItems()
        viewModel.resetChanges()
        sharedViewModel.resetChanges()
        sharedViewModel.resetPaymentChanges()


    }

}