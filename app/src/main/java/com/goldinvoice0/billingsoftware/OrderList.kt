package com.goldinvoice0.billingsoftware

//import com.goldinvoice0.billingsoftware.ViewModel.OrderViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.OrderAdapter
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.ViewModel.OrderViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentOrderListBinding


class OrderList : Fragment() {

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderAdapter: OrderAdapter
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var orders: List<Order>

    private val shareViewModel:SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)
//        binding.orderListRV.visibility = View.GONE
        binding.placeHolderView.visibility = View.GONE

        setupClickListeners()
        setUpRecyclerView()
        setupObservers()


        return binding.root
    }

    private fun setupObservers() {
        orderViewModel.getAllOrders().observe(viewLifecycleOwner) {
            orders = it
            orderAdapter.updateList(orders)
            Log.d("order", it.size.toString())

        }
    }

    private fun setUpRecyclerView() {
        orderAdapter = OrderAdapter(emptyList(),
            onItemClick = { order -> HandleItemClickListner(order) })
        binding.orderListRV.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun HandleItemClickListner(item: Order) {
        shareViewModel.setOrder(item)
        val parentNavController = requireActivity().findNavController(R.id.nav_host_fragment)
        parentNavController.navigate(R.id.action_mainScreen_to_orderEdit)
    }

    private fun updateVisibility(hasData: Boolean) {
        binding.orderListRV.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.placeHolderView.visibility = if (hasData) View.GONE else View.VISIBLE
    }


    private fun setupClickListeners() {
        binding.addOrder.setOnClickListener {
            // Navigate to add order screen
            val bundle = Bundle()
            bundle.putBoolean("FromOrderList", true)
            val parentNavController = requireActivity().findNavController(R.id.nav_host_fragment)
            parentNavController.navigate(R.id.action_mainScreen_to_customerList, bundle)

        }
    }

    override fun onStart() {
        super.onStart()
        shareViewModel.clearPayments()
        shareViewModel.clearJewelryItem()
        shareViewModel.jewelleryItemDeleted = false
        shareViewModel.paymentsDeleted = false
    }
}