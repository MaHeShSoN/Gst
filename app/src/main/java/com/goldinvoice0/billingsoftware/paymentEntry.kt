package com.goldinvoice0.billingsoftware

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.ItemsAdapter
import com.goldinvoice0.billingsoftware.Model.PdfData
import com.goldinvoice0.billingsoftware.Model.PdfFinalData
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.InvoiceNumberViewModel
import com.goldinvoice0.billingsoftware.ViewModel.PdfFinalDataViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentPaymentEntryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import come.Gst.pdf.PdfGenerationClasses.PdfGeneratorclass2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class paymentEntry : Fragment() {


    private var _binding: FragmentPaymentEntryBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var pdfData1: PdfData
    var total: Int = 0
    private var totalAmount: Int = 0


    private var amountTextList = mutableListOf<String>()


    private lateinit var adapter1: ItemsAdapter
    private var list = mutableListOf<String>()
    private val shopViewModel: ShopViewModel by viewModels()
    private val pdfFinalDataViewModel: PdfFinalDataViewModel by viewModels()
    private val invoiceNumberViewModel: InvoiceNumberViewModel by viewModels()
    private lateinit var shop: Shop

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentEntryBinding.inflate(inflater, container, false)



        shopViewModel.shop.observe(viewLifecycleOwner, Observer {
            this.shop = it
        })



        sharedViewModel.getPdfData().observe(viewLifecycleOwner) { pdfData ->
            // Access pdfData properties in Fragment

            pdfData1 = pdfData
//            Log.d("Tag", pdfData.shop.gstNumber)

            val itemCountText = "Item"

            binding.name.text = pdfData.name

            binding.date.text = pdfData.date
            binding.itemCount.text = "$itemCountText (${pdfData.descriptionList.size})"

            binding.desciptionList.text = pdfData.descriptionList.joinToString(",")
            binding.totalList.text = pdfData.totalList.joinToString(",")

            for (i in pdfData.totalList) {
                total += i.toInt()
            }
            totalAmount = total
            binding.amount.text = totalAmount.toString()

            check()

//            viewModel = ViewModelProvider(this)[ListModelViewModel::class.java]


        }
        binding.addButton.setOnClickListener {
            val customView = layoutInflater.inflate(R.layout.inputdialog1, null)

            val note =
                customView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.datePickerd)
            val amount =
                customView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextTotalAmount)

            context?.let { it1 ->
                val d = MaterialAlertDialogBuilder(it1)
                    .setView(customView)
                    .setPositiveButton("Accept", null)
                    .setNegativeButton("Cancel", null)

                val dd = d.create()
                dd.show()
                dd.window?.setBackgroundDrawableResource(android.R.color.white)

                val pButton = dd.getButton(DialogInterface.BUTTON_POSITIVE)
                pButton.setOnClickListener {
                    // Respond to positive button press
                    if (amount.text!!.isEmpty()) {
                        amount.error = "amount is empty"
                    } else {
                        //Add the amount to the list and recycler view
                        amountTextList.add(note.text.toString())
                        list.add("+" + amount.text.toString())
                        totalAmount += amount.text.toString().toInt()
                        binding.amount.text = totalAmount.toString()
                        binding.recyclerViewRP.adapter!!.notifyItemInserted(list.size - 1)
                        check()
                        dd.dismiss()
                    }
                }
                val nButton = dd.getButton(DialogInterface.BUTTON_NEGATIVE)
                nButton.setTextColor(resources.getColor(R.color.Red))
                nButton.setOnClickListener {
                    dd.dismiss()
                }

            }

        }


        binding.subbtractButton.setOnClickListener {
            val customView = layoutInflater.inflate(R.layout.inputdialog1, null)

            val note =
                customView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.datePickerd)
            val amount =
                customView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextTotalAmount)
            context?.let { it1 ->
                val d = MaterialAlertDialogBuilder(it1)
                    .setView(customView)
                    .setPositiveButton("Accept", null)
                    .setNegativeButton("Cancel", null)

                // Respond to positive button press

                val dd = d.create()
                dd.show()
                dd.window?.setBackgroundDrawableResource(android.R.color.white)


                val pButton: Button = dd.getButton(DialogInterface.BUTTON_POSITIVE)
                pButton.setOnClickListener {
                    if (amount.text!!.isEmpty()) {
                        amount.error = "amount is empty"
                    } else if (totalAmount < amount.text!!.toString().toInt()) {
//                        AndroidUtils().SneakerError(binding.root, "Entered amount too large")
                        Toast.makeText(
                            requireContext(),
                            "Entered amount too large",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //Add the amount to the list and recycler view
                        amountTextList.add(note.text.toString())
                        list.add("-" + amount.text.toString())
                        totalAmount -= amount.text.toString().toInt()
                        binding.amount.text = totalAmount.toString()
                        binding.recyclerViewRP.adapter!!.notifyItemInserted(list.size - 1)
                        check()
                        dd.dismiss()
                    }
                }

                val nButton = dd.getButton(DialogInterface.BUTTON_NEGATIVE)
                nButton.setTextColor(resources.getColor(R.color.Red))
                nButton.setOnClickListener {
                    dd.dismiss()
                }

            }
        }
        setUpRecyclerView()

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nextButton -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        PDFBoxResourceLoader.init(requireContext())
                        // Access pdfData properties in Fragment C

                        val nextNumber: Long = invoiceNumberViewModel.getNextInvoiceNumber()
                        val invoiceNumber: String = "INV-${String.format("%06d", nextNumber)}"


                        Log.d("Tag", totalAmount.toString()+"inPEF")


                        val fileName = PdfGenerationClass001().createPdfFromView(
                            pdfData = pdfData1,
                            binding.root.context,
                            list,
                            totalAmount,
                            shop,
                            invoiceNumber,
                            amountTextList
                        )



                        val data = PdfFinalData(
                            0,
                            fileName = pdfData1.fileName,
                            pdfData1.name,
                            pdfData1.address,
                            pdfData1.phone,
                            pdfData1.date,
                            totalAmount,
                            pdfData1.descriptionList,
                            pdfData1.grWtList,
                            pdfData1.ntWtList,
                            pdfData1.makingChargeList,
                            pdfData1.stoneValueList,
                            pdfData1.goldPriceList,
                            pdfData1.totalList,
                            pdfData1.pcsList,
                            pdfData1.karatList,
                            list,
                            pdfData1.listOfWastage,
                            amountTextList,
                            invoiceNumber
                        )
                        Log.d("TAG", nextNumber.toString())
                        pdfFinalDataViewModel.insertPdfFinalData(data)
                        sharedViewModel.setReceivedList(data)

                        val bundle = Bundle()
                        bundle.putString("fileName", fileName.toString())
                        if (fileName.toString().isNotEmpty()) {
                            withContext(Dispatchers.Main) {
                                findNavController().navigate(
                                    R.id.action_paymentEntry_to_viewBill,
                                    bundle
                                )
                            }
                        }
                    }
                    true
                }

                else -> false
            }
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun deleteItem(position: Int) {
        if (position in 0 until list.size) {
            if (list[position].contains('+')) {
//                val amount = list[position].substring()

                // Define the regular expression pattern
                val regex = Regex("""\+(\d+)""")

                // Find the match in the input string
                val matchResult = regex.find(list[position])

                // Extract the digits after the '+'
                val digitsAfterPlus = matchResult?.groupValues?.get(1)
                totalAmount -= digitsAfterPlus!!.toInt()

                list.removeAt(position)
                amountTextList.removeAt(position)
                binding.amount.text = totalAmount.toString()
                adapter1.notifyItemRemoved(position)
                adapter1.notifyItemRangeChanged(position, list.size - position)
            } else if (list[position].contains('-')) {
                // Define the regular expression pattern
                val regex = Regex("""-(\d+)""")

                // Find the match in the input string
                val matchResult = regex.find(list[position])

                // Extract the digits after the '+'
                val digitsAfterPlus = matchResult?.groupValues?.get(1)
                totalAmount += digitsAfterPlus!!.toInt()
                list.removeAt(position)
                amountTextList.removeAt(position)
                binding.amount.text = totalAmount.toString()
                adapter1.notifyItemRemoved(position)
                adapter1.notifyItemRangeChanged(position, list.size - position)
            } else {
                Toast.makeText(requireContext(), "Something is wrong", Toast.LENGTH_SHORT).show()
//                AndroidUtils().Toast(, binding.root.context)
            }
        }


        //How to delete the item or how to check if the item is positive or negative
    }

    private fun setUpRecyclerView() {
        adapter1 = ItemsAdapter(amountTextList,
            list, editItemClickListener = { position ->
                //Delete the item from the list
                deleteItem(position)
            })
        val mLayoutManager = LinearLayoutManager(binding.root.context)
        binding.recyclerViewRP.adapter = adapter1
        binding.recyclerViewRP.layoutManager = mLayoutManager
    }

    fun check() {
        val amount = totalAmount
        if (amount > 0) {
            binding.colourPaidDue.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.AWTYellow
                )
            )
            binding.colourPaidDue.text = "Pending"

        } else if (amount == 0) {
            binding.colourPaidDue.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.Green
                )
            )
            binding.colourPaidDue.text = "Paid"
        }
    }

}