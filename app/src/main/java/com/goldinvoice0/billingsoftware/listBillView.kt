package com.goldinvoice0.billingsoftware

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import com.goldinvoice0.billingsoftware.ViewModel.PdfFinalDataViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentListBillViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import come.Gst.pdf.PdfGenerationClasses.PdfGeneratorclass2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class listBillView : Fragment() {

    private var _binding: FragmentListBillViewBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val pdfFinalDataViewModel: PdfFinalDataViewModel by viewModels()
    private var totalAmount: Int = 0
    private var list = mutableListOf<String>()
    private lateinit var adapter1: ItemsAdapter
    private lateinit var pdfData1: PdfData
    private var id: Int = 0
    private val shopViewModel: ShopViewModel by viewModels()
    private lateinit var shop: Shop
    private var number = 0L
    private lateinit var pdfFinalData1: PdfFinalData
    private lateinit var nextNumber: String
    private var amountTextList = mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBillViewBinding.inflate(inflater, container, false)
        sharedViewModel.getReceivedList().observe(viewLifecycleOwner) {
            amountTextList = it.amountTextList
            list = it.receivedList
            pdfFinalData1 = it
            totalAmount = it.totalAmount
            nextNumber = it.invoiceNumber
            Log.d("amountText",totalAmount.toString()+"first")
            id = it.id
            number = it.phone.toLong()
            binding.amount.text = it.totalAmount.toString()
            binding.name.text = it.name
            binding.date.text = it.date
            val itemCountText = "Item"
            binding.itemCount.text = "$itemCountText (${it.descriptionList.size})"
            binding.desciptionList.text = it.descriptionList.joinToString(",")
            binding.totalList.text = it.totalList.joinToString(",")

            check()


            setUpRecyclerView()
            pdfData1 = PdfData(
                it.fileName,
                it.name,
                it.phone,
                it.address,
                it.date,
                it.descriptionList,
                it.grWtList,
                it.ntWtList,
                it.makingChargeList,
                it.stoneValueList,
                it.goldPriceList,
                it.totalList,
                it.pcsList,
                it.karatList,
                it.listOfWastage,
                it.invoiceNumber
            )
        }

        shopViewModel.shop.observe(viewLifecycleOwner, Observer {
            shop = it
        })

        binding.topAppBar.menu.findItem(R.id.buttonSubmit)

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
                        Log.d("amountText",totalAmount.toString()+"whenAddButtonClicked")
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
                        Log.d("amountText",totalAmount.toString()+"When SubButtonClicked")
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


        // Edit button click listener
        binding.editBill.setOnClickListener {
            toggleEditMode(true)
        }

        //Delete The Bill
        binding.deleteBill.setOnClickListener {
            val d = pdfFinalData1.date.toString()
            if (d.isNotEmpty()) {
                pdfFinalDataViewModel.deletePdfFinalData(pdfFinalData1)
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Please Try Again", Toast.LENGTH_SHORT).show()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            toggleEditMode2()
        }

        binding.viewPdf.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                PDFBoxResourceLoader.init(requireContext())
                val fileName = PdfGenerationClass001().createPdfFromView(
                    pdfData = pdfData1,
                    binding.root.context,
                    list,
                    totalAmount,
                    shop,
                    nextNumber,
                    amountTextList
                )
                val bundle = Bundle()
                bundle.putString("fileName", fileName.toString())
                if (fileName.toString().isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(
                            R.id.action_listBillView_to_viewBill,
                            bundle
                        )
                    }
                }
            }
        }


        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.callButton -> {
                    val number = number
                    if(number!=0L){
                        val call = Uri.parse("tel:$number")
                        val surf = Intent(Intent.ACTION_DIAL, call)
                        startActivity(surf)
                    }else{
                        Toast.makeText(requireContext(),"Try Again", Toast.LENGTH_SHORT).show()
                    }

                    true
                }

                R.id.buttonSubmit -> {
                    //hide the add and sub button , and show the three edit delete and view button also hide the close and save button , then save the save the list
                    //first lets save the list
                    Log.d("amountText",totalAmount.toString()+"InSubmutButton")
                    val data = PdfFinalData(
                        id,
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
                        amountTextList
                    )
                    pdfFinalDataViewModel.updatePdfFinalData(data)

                    //then lets hide and show the buttons
                    toggleEditMode2()

                    true
                }
                else -> false
            }
        }

        // Inflate the layout for this fragment
        return binding.root
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

        } else if (amount <= 0) {
            binding.colourPaidDue.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.Green
                )
            )
            binding.colourPaidDue.text = "Paid"
        }
    }

    private fun toggleEditMode(isEditing: Boolean) {
        if (isEditing) {
            // Hide the delete button and show add/subtract buttons
            binding.editBill.visibility = View.INVISIBLE
            binding.deleteBill.visibility = View.INVISIBLE
            binding.addButton.visibility = View.VISIBLE
            binding.subbtractButton.visibility = View.VISIBLE
            binding.viewPdf.visibility = View.INVISIBLE
            binding.recyclerViewRP.visibility = View.VISIBLE

            // Change toolbar icon to save button and hide the toolbar title
            binding.topAppBar.menu.findItem(R.id.buttonSubmit).isVisible = true
            binding.topAppBar.menu.findItem(R.id.callButton).isVisible = false
            binding.topAppBar.title = ""

            // Set Nevigation Icon
            binding.topAppBar.setNavigationIcon(R.drawable.baseline_close_24)
            binding.topAppBar.setNavigationIconTint(resources.getColor(R.color.redForSub))

            // Start a transition animation
            val transition = android.transition.TransitionSet()
            transition.addTransition(android.transition.Fade())
            transition.duration = 300
            android.transition.TransitionManager.beginDelayedTransition(binding.root, transition)
        }
    }

    private fun toggleEditMode2() {
        // Hide the delete button and show add/subtract buttons
        binding.editBill.visibility = View.VISIBLE
        binding.deleteBill.visibility = View.VISIBLE
        binding.addButton.visibility = View.INVISIBLE
        binding.subbtractButton.visibility = View.INVISIBLE
        binding.viewPdf.visibility = View.VISIBLE
        binding.recyclerViewRP.visibility = View.INVISIBLE

        // Change toolbar icon to save button and hide the toolbar title
        binding.topAppBar.menu.findItem(R.id.buttonSubmit).isVisible = false
        binding.topAppBar.menu.findItem(R.id.callButton).isVisible = true
        binding.topAppBar.title = "Invoice Detail"

        // Set Nevigation Icon
        binding.topAppBar.navigationIcon = null

        // Start a transition animation
        val transition = android.transition.TransitionSet()
        transition.addTransition(android.transition.Fade())
        transition.duration = 300
        android.transition.TransitionManager.beginDelayedTransition(binding.root, transition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}