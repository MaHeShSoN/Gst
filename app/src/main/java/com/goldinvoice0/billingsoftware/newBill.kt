package com.goldinvoice0.billingsoftware

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.ExpandableAdapter
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.Model.PdfData
import com.goldinvoice0.billingsoftware.databinding.FragmentNewBillBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class newBill : Fragment() {

    private var _binding: FragmentNewBillBinding? = null
    private val binding get() = _binding!!

    private var listOfJewelleryItems: MutableList<String> = mutableListOf()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val listOfDescription: MutableList<String> = mutableListOf<String>()
    private val listOfGrWt = mutableListOf<String>()
    private val listOfNetWt = mutableListOf<String>()
    private val listOfMakingCharges = mutableListOf<String>()
    private val listOfStoneValue = mutableListOf<String>()
    private val listOfGoldPrice = mutableListOf<String>()
    private val listOfTotal = mutableListOf<String>()
    private val listOfPcs = mutableListOf<String>()
    private val listOfKarat = mutableListOf<String>()
    private val listOfWastage = mutableListOf<String>()

    private val itemList: MutableList<ItemModel> = mutableListOf()

    private lateinit var adapter: ExpandableAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewBillBinding.inflate(inflater, container, false)

        val bundle = arguments
        val name = bundle!!.getString("name").toString()
        val address = bundle.getString("address").toString()
        val phone = bundle.getString("email").toString()
        binding.name.text = name
        binding.address.text = address
        binding.number.text = phone

        setUpRecyclerView()

        val jewelleryItemsArray = resources.getStringArray(R.array.listOfJewelleryItems)
        val ktArray = resources.getStringArray(R.array.listOfKt)
        val listOfJewelleryItems: MutableList<String> = jewelleryItemsArray.toMutableList()
        val listOfKt: MutableList<String> = ktArray.toMutableList()
        // Now you can add more items to the list if needed
//        listOfJewelleryItems.add("Anklet")

        //Add Item Button Clicked
        binding.addItem.setOnClickListener {
            createAlertDialog(itemList, listOfJewelleryItems,listOfKt)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nextButtonNewBill -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        //After finish button is clicked , take the value of lists and put them into the view model like list ,
                        // customer name address phone numner , fileName and date
                        val customerName = binding.name.text.toString().trim()
                        val customerAddress = binding.address.text.toString().trim()
                        val customerNumber = binding.number.text.toString()
                        // Get the current date and time
                        val now = LocalDateTime.now()
                        // Format the date and time using a custom pattern
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                        val formattedDateTime = now.format(formatter).toString()
                        val fdT = formattedDateTime.subSequence(0, 10)


                        //Save the details to the view model
                        sharedViewModel.setPdfData(
                            PdfData(
                                customerName.replace("/", "") + fdT,
                                customerName,
                                customerNumber,
                                customerAddress,
                                formattedDateTime,
                                listOfDescription,
                                listOfGrWt,
                                listOfNetWt,
                                listOfMakingCharges,
                                listOfStoneValue,
                                listOfGoldPrice,
                                listOfTotal,
                                listOfPcs,
                                listOfKarat,
                                listOfWastage
                            )
                        )
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(R.id.action_newBill_to_paymentEntry)
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

    private fun setUpRecyclerView() {

        adapter = ExpandableAdapter(itemList, onEdit = { position, item ->
            // Handle edit action
            showEditDialog(position, item)

        }, onDelete = { position ->
            // Handle delete action
            handleDelete(position)

        })

        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewItems.adapter = adapter

    }

    private fun createAlertDialog(
        itemList: MutableList<ItemModel>, listOfJewelleryItems: MutableList<String>, listOfKt: MutableList<String>
    ) {
        val builder =
            AlertDialog.Builder(requireContext(), R.style.fullScreenMaterialAlertDialogBuilder)
        builder.setTitle("Enter Item Details")

        val dialogView = layoutInflater.inflate(R.layout.dialog_jewellery_input, null)
        builder.setView(dialogView)


        val arrayAdapter = ArrayAdapter(
            binding.root.context, android.R.layout.simple_dropdown_item_1line, listOfJewelleryItems
        )
        val arrayAdapterKt = ArrayAdapter(
            binding.root.context, android.R.layout.simple_dropdown_item_1line, listOfKt
        )

        dialogView.findViewById<AutoCompleteTextView>(R.id.editViewJewelleryName_03)
            .setAdapter(arrayAdapter)
        dialogView.findViewById<AutoCompleteTextView>(R.id.editViewKt)
            .setAdapter(arrayAdapterKt)


        val editTextItemName =
            dialogView.findViewById<AutoCompleteTextView>(R.id.editViewJewelleryName_03)
        val editTextGrossWt =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.grosswtTextField)
        val editTextNetWt =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.netswtTextField)
        val editTextMakingChargs =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editViewMobilNumber_03)
        val editTextMakingGoldPrice =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editViewAddress_03)
        val editTextStoneWt =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.stoneValueTextField)
        val editTextPce =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editViewpice)
        val editTextKt =
            dialogView.findViewById<AutoCompleteTextView>(R.id.editViewKt)
        val editTextWastage =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editViewWastage)

        builder.setPositiveButton("Add", null)
        builder.setNegativeButton("Cancel", null)


        // Set up the dialog's buttons
        val alertDialog = builder.create()
        alertDialog.show()

        val pButton: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        pButton.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryVariant))
        pButton.setTextColor(requireContext().getColor(R.color.white))
        pButton.setOnClickListener {
            if (editTextItemName.text.toString().isEmpty()) {
                editTextItemName.error = "required"
            }
            if (editTextGrossWt.text.toString().isEmpty()) {
                editTextGrossWt.error = "required"
            }
            if (editTextNetWt.text.toString().isEmpty()) {
                editTextNetWt.error = "required"
            }
            if (editTextMakingChargs.text.toString().isEmpty()) {
                editTextMakingChargs.error = "required"
            }
            if (editTextMakingGoldPrice.text.toString().isEmpty()) {
                editTextMakingGoldPrice.error = "required"
            }
            if (editTextStoneWt.text.toString().isEmpty()) {
                editTextStoneWt.error = "required"
            }
            if (editTextPce.text.toString().isEmpty()) {
                editTextPce.error = "required"
            }
            if (editTextKt.text.toString().isEmpty()) {
                editTextKt.error = "required"
            }
            if (editTextWastage.text.toString().isEmpty()) {
                editTextKt.error = "required"
            }
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view?.windowToken, 0
            )

            val list = mutableListOf<EditText>(
                editTextItemName,
                editTextGrossWt,
                editTextNetWt,
                editTextMakingChargs,
                editTextMakingGoldPrice,
                editTextStoneWt,
                editTextPce,
                editTextKt
            )
            val isAnyEditTextEmpty = list.any { it.text.toString().trim().isEmpty() }
            if (!isAnyEditTextEmpty) {

                val jewelleryName = editTextItemName.text.toString()
                val wastage = editTextWastage.text.toString().toFloat()
                val grwt = editTextGrossWt.text.toString().toFloat()
                val netwt = editTextNetWt.text.toString().toFloat()
                val makingcharges = editTextMakingChargs.text.toString().toInt()
                val goldprive = editTextMakingGoldPrice.text.toString().toInt() / 10
                val stonewt = editTextStoneWt.text.toString().toInt()
                val pce = editTextPce.text.toString()
                val karat = editTextKt.text.toString()
                val makingcharges2: Int = makingcharges * pce.toInt()
                val ttlGold: Long =
                    ((((netwt + wastage) * goldprive.toFloat()) + makingcharges2) * pce.toFloat() + stonewt.toFloat()).toLong()

                listOfDescription.add(jewelleryName)
                listOfGrWt.add(grwt.toString())
                listOfNetWt.add(netwt.toString())
                listOfMakingCharges.add(makingcharges2.toString())
                listOfStoneValue.add(stonewt.toString())
                listOfGoldPrice.add(goldprive.toString())
                listOfTotal.add(ttlGold.toString())
                listOfPcs.add(pce)
                listOfKarat.add(karat)
                listOfWastage.add(wastage.toString())
                val itemModel = ItemModel(
                    jewelleryName,
                    grwt,
                    netwt,
                    goldprive,
                    stonewt,
                    makingcharges2,
                    ttlGold,
                    karat,
                    wastage
                )
                itemList.add(itemModel)
                binding.recyclerViewItems.adapter!!.notifyItemInserted(itemList.size - 1)
                //enable the next button and itemBar
                val mnuFB: MenuItem = binding.topAppBar.menu.findItem(R.id.nextButtonNewBill)
                mnuFB.isVisible = true
                alertDialog.dismiss()

            }

        }
        val nButton: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        nButton.setTextColor(requireContext().getColor(R.color.white))
        nButton.setBackgroundColor(requireContext().getColor(R.color.redForSub))
        nButton.setOnClickListener {
            alertDialog.dismiss()

        }

    }

    private fun showEditDialog(position: Int, item: ItemModel) {
        val description = listOfDescription[position]
        val grWt = listOfGrWt[position]
        val netWt = listOfNetWt[position]
        val stoneValue = listOfStoneValue[position]
        val goldPrice: String = (listOfGoldPrice[position].toInt() * 10).toString()
        val pcs = listOfPcs[position]
        val makingChages: String = listOfMakingCharges[position]
        val karat: String = listOfKarat[position]
        val wastage: String = listOfWastage[position]

        val builder =
            AlertDialog.Builder(requireContext(), R.style.fullScreenMaterialAlertDialogBuilder)
        builder.setTitle("Edit Item Details")

        val dialogView = layoutInflater.inflate(R.layout.dialog_jewellery_input, null)
        builder.setView(dialogView)


        val arrayAdapter = ArrayAdapter(
            binding.root.context, android.R.layout.simple_dropdown_item_1line, listOfJewelleryItems
        )

        dialogView.findViewById<AutoCompleteTextView>(R.id.editViewJewelleryName_03)
            .setAdapter(arrayAdapter)


        val editTextItemName =
            dialogView.findViewById<AutoCompleteTextView>(R.id.editViewJewelleryName_03)
        val editTextGrossWt =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.grosswtTextField)
        val editTextNetWt =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.netswtTextField)
        val editTextMakingChargs =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editViewMobilNumber_03)
        val editTextMakingGoldPrice =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editViewAddress_03)
        val editTextStoneWt =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.stoneValueTextField)
        val editTextPce =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editViewpice)
        val editTextKt =
            dialogView.findViewById<AutoCompleteTextView>(R.id.editViewKt)
        val editTextWastage =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editViewWastage)

        editTextItemName.setText(description)
        editTextGrossWt.setText(grWt)
        editTextNetWt.setText(netWt)
        editTextMakingChargs.setText(makingChages)
        editTextStoneWt.setText(stoneValue)
        editTextPce.setText(pcs)
        editTextMakingGoldPrice.setText(goldPrice)
        editTextKt.setText(karat)
        editTextWastage.setText(wastage)


        builder.setPositiveButton("Submit", null)
        builder.setNegativeButton("Cancel", null)
        builder.setNeutralButton("Delete", null)


        // Set up the dialog's buttons
        val alertDialog = builder.create()
        alertDialog.show()

        val neButton: Button = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
        neButton.setTextColor(requireContext().getColor(R.color.white))
        neButton.setBackgroundColor(requireContext().getColor(R.color.redForSub))
        neButton.setOnClickListener {
            handleDelete(position)
            alertDialog.dismiss()
        }


        val pButton: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        pButton.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryVariant))
        pButton.setTextColor(requireContext().getColor(R.color.white))
        pButton.setOnClickListener {
            if (editTextItemName.text.toString().isEmpty()) {
                editTextItemName.error = "required"
            }
            if (editTextGrossWt.text.toString().isEmpty()) {
                editTextGrossWt.error = "required"
            }
            if (editTextNetWt.text.toString().isEmpty()) {
                editTextNetWt.error = "required"
            }
            if (editTextMakingChargs.text.toString().isEmpty()) {
                editTextMakingChargs.error = "required"
            }
            if (editTextMakingGoldPrice.text.toString().isEmpty()) {
                editTextMakingGoldPrice.error = "required"
            }
            if (editTextStoneWt.text.toString().isEmpty()) {
                editTextStoneWt.error = "required"
            }
            if (editTextPce.text.toString().isEmpty()) {
                editTextPce.error = "required"
            }
            if (editTextKt.text.toString().isEmpty()) {
                editTextKt.error = "required"
            }
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view?.windowToken, 0
            )

            val list = mutableListOf<EditText>(
                editTextItemName,
                editTextGrossWt,
                editTextNetWt,
                editTextMakingChargs,
                editTextMakingGoldPrice,
                editTextStoneWt,
                editTextPce,
                editTextKt,
            )
            val isAnyEditTextEmpty = list.any { it.text.toString().trim().isEmpty() }
            if (!isAnyEditTextEmpty) {

                val jewelleryName = editTextItemName.text.toString()
                val grwt = editTextGrossWt.text.toString().toFloat()
                val netwt = editTextNetWt.text.toString().toFloat()
                val makingcharges = editTextMakingChargs.text.toString().toInt()
                val goldprive = editTextMakingGoldPrice.text.toString().toInt() / 10
                val stonewt = editTextStoneWt.text.toString().toInt()
                val pce = editTextPce.text.toString()
                val karat = editTextKt.text.toString()
                val wastage = editTextWastage.text.toString().toFloat()
                val makingcharges2: Int = (makingcharges) * pce.toInt()
                val ttlGold: Long =
                    ((((netwt + wastage) * goldprive.toFloat()) + makingcharges2) * pce.toFloat() + stonewt.toFloat()).toLong()


                listOfDescription[position] = jewelleryName
                listOfGrWt[position] = grwt.toString()
                listOfNetWt[position] = netwt.toString()
                listOfMakingCharges[position] = makingcharges2.toString()
                listOfStoneValue[position] = stonewt.toString()
                listOfGoldPrice[position] = goldprive.toString()
                listOfTotal[position] = ttlGold.toString()
                listOfPcs[position] = pce
                listOfKarat[position] = karat
                listOfWastage[position] = wastage.toString()

                val itemModel = ItemModel(
                    jewelleryName,
                    grwt,
                    netwt,
                    goldprive,
                    stonewt,
                    makingcharges2,
                    ttlGold,
                    karat,
                    wastage

                )
                itemList[position] = itemModel
                binding.recyclerViewItems.adapter!!.notifyItemChanged(position)
                alertDialog.dismiss()

            }

        }
        val nButton: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        nButton.setTextColor(requireContext().getColor(R.color.white))
        nButton.setBackgroundColor(requireContext().getColor(R.color.redForSub))
        nButton.setOnClickListener {
            alertDialog.dismiss()

        }


    }

    private fun handleDelete(position: Int) {
        if (position in 0 until itemList.size) {
            itemList.removeAt(position)
            listOfDescription.removeAt(position)
            listOfGrWt.removeAt(position)
            listOfNetWt.removeAt(position)
            listOfMakingCharges.removeAt(position)
            listOfStoneValue.removeAt(position)
            listOfGoldPrice.removeAt(position)
            listOfTotal.removeAt(position)
            listOfPcs.removeAt(position)
            listOfKarat.removeAt(position)
            listOfWastage.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, itemList.size - position)
        }
    }

    override fun onResume() {
        super.onResume()

        val mnuFB: MenuItem = binding.topAppBar.menu.findItem(R.id.nextButtonNewBill)
        if (binding.recyclerViewItems.adapter!!.itemCount > 0) {
            mnuFB.isVisible = true
        } else if (binding.recyclerViewItems.adapter!!.itemCount == 0) {
            mnuFB.isVisible = false
        }
    }

    override fun onStart() {
        super.onStart()
        val mnuFB: MenuItem = binding.topAppBar.menu.findItem(R.id.nextButtonNewBill)

        if (binding.recyclerViewItems.adapter!!.itemCount > 0) {
            mnuFB.isVisible = true
        } else if (binding.recyclerViewItems.adapter!!.itemCount == 0) {
            mnuFB.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}