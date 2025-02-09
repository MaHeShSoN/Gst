//package com.goldinvoice0.billingsoftware
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import com.goldinvoice0.billingsoftware.Model.Order
//import com.goldinvoice0.billingsoftware.Model.OrderItem
//import com.goldinvoice0.billingsoftware.Model.Shop
//import com.goldinvoice0.billingsoftware.Model.toModel
//import com.goldinvoice0.billingsoftware.ViewModel.OrderViewModel
//import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
//import com.goldinvoice0.billingsoftware.databinding.FragmentViewOrderBinding
//import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
//import java.io.File
//
//
//class ViewOrderFragment : Fragment() {
//
//
//    private var _binding: FragmentViewOrderBinding? = null
//    private val binding get() = _binding!!
//    lateinit var f: File
//    private val sharedViewModel: SharedViewModel by activityViewModels()
//
//    private val shopViewModel: ShopViewModel by viewModels()
//    private lateinit var shop: Shop
//    private var itemList: List<OrderItemEntity> = listOf()
//    private lateinit var orderItemEntities: List<OrderItemEntity>
//    private lateinit var orderItems: MutableList<OrderItem>
//    private lateinit var order: Order
//    private lateinit var orderEntity: OrderEntity
//    private lateinit var orderNumber: String
//    private lateinit var fileName: String
//    private val orderViewModel: OrderViewModel by viewModels()
//
//    var fromWhichFragment: Boolean = false
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentViewOrderBinding.inflate(inflater, container, false)
//
//        val bundal = arguments
//        val file = bundal!!.getString("fileName")
//        fromWhichFragment = bundal.getBoolean("FromWhichFragment")
//        f = File(file!!)
//
//
//        shopViewModel.shop.observe(viewLifecycleOwner) {
//            shop = it
//        }
//
//        sharedViewModel.getOrder().observe(viewLifecycleOwner) {
//            order = it
//        }
//
//        sharedViewModel.getOrderData().observe(viewLifecycleOwner) {
//            orderEntity = it.order
//
//
//            itemList = it.items
//            orderItemEntities = it.items
//            // Convert to MutableList<OrderItem>
//            orderItems = orderItemEntities.toModelList()
//
//            order = it.order.toModel(it.items.map { item -> item.toModel() })
//            fileName = it.order.customerName.replace("/", "") + it.order.address.replace("/", "")
//            //file name, invoice number
//            orderNumber = it.order.orderNumber
//        }
//
//        PDFBoxResourceLoader.init(binding.root.context)
//        binding.pdfViewMF.fromFile(f).load()
//        // Inflate the layout for this fragment
//        return binding.root
//    }
//
//    // Function to convert List<OrderItemEntity> to MutableList<OrderItem>
//    fun List<OrderItemEntity>.toModelList(): MutableList<OrderItem> {
//        return this.map { it.toModel() }.toMutableList()
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}