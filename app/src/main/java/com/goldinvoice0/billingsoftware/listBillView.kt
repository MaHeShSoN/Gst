package com.goldinvoice0.billingsoftware

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.ExpandableAdapter_Display
import com.goldinvoice0.billingsoftware.Adapter.ReceviedPaymentAdapter_Display
import com.goldinvoice0.billingsoftware.Model.BillInputs
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.Model.PaymentMethod
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.goldinvoice0.billingsoftware.Model.RecivedPaymentType
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.BillInputViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentListBillViewBinding
import com.google.android.material.snackbar.Snackbar
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class listBillView : Fragment() {
    private var _binding: FragmentListBillViewBinding? = null
    private val binding get() = _binding!!

    private val billInputViewModel: BillInputViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    //for items like add,bau
    private val jewelryViewModel: JewelryViewModel by activityViewModels()

    private lateinit var adapterForItems: ExpandableAdapter_Display
    private lateinit var adapterForPayment: ReceviedPaymentAdapter_Display

    var phoneNumber: String = ""


    private lateinit var shop: Shop
    private val shopViewModel: ShopViewModel by activityViewModels()
    private lateinit var file: File


    private var pendingFile: File? = null
    private var pendingFileName: String? = null

    private var currentBill: BillInputs? = null

    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    )

    companion object {
        private const val ARG_BILL_ID = "billId"
        private const val ARG_EDIT_BILL = "EditBill"
        private const val ARG_EDIT_PAYMENT = "EditPayment"
        private const val ARG_TOTAL_AMOUNT = "totalAmount"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBillViewBinding.inflate(inflater, container, false)

        binding.spinner.progress = 45
        binding.spinner2.progress = 45

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpRecyclerView()
        initializeBillData()
        setupClickListeners()
        observeChanges()
        getTheShop()
        createFileFromBill()

    }

    private fun getTheShop() {
        shopViewModel.shop.observe(viewLifecycleOwner) {
            shop = it
        }
    }

//    private fun createFileFromBill() {
//        val billId = arguments?.getInt(ARG_BILL_ID) ?: return
//
//        billInputViewModel.getBillInputsById(billId).observe(viewLifecycleOwner) { billData ->
//            billData?.let { bill ->
//                val sanitizedFileName =
//                    "${bill.customerName.trim().replace("[^a-zA-Z0-9]", "_")}_${bill.invoiceNumber}"
//
//                lifecycleScope.launch {
//                    try {
//                        PDFBoxResourceLoader.init(binding.root.context)
//
//                        val f = PdfGenerationClass001().createPdfFromView(
//                            requireContext(),
//                            bill,
//                            shop,
//                            sanitizedFileName
//                        )
//
//                        file = f
//                    } catch (e: Exception) {
//                        Log.e("PDF_ERROR", "Error generating PDF", e)
//                        Toast.makeText(
//                            requireContext(),
//                            "Failed to generate PDF",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } ?: run {
//                Log.e("BILL_ERROR", "Bill data is null")
//                Toast.makeText(requireContext(), "Bill not found", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun createFileFromBill() {
        val billId = arguments?.getInt(ARG_BILL_ID) ?: return

        // Observe bill data once
        billInputViewModel.getBillInputsById(billId).observe(viewLifecycleOwner) { billData ->
            currentBill = billData
            generatePdfFile(billData)

//            if (file == null) { // Only generate if not already generated
//            }
        }
    }

    private fun generatePdfFile(billData: BillInputs?) {
        billData?.let { bill ->
            val sanitizedFileName =
                "${bill.customerName.trim().replace("[^a-zA-Z0-9]", "_")}_${bill.invoiceNumber}"

            binding.spinner.visibility = View.VISIBLE // Show loading

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    PDFBoxResourceLoader.init(binding.root.context)
                    file = PdfGenerationClass001().createPdfFromView(
                        requireContext(),
                        bill,
                        shop,
                        sanitizedFileName
                    )
                    binding.spinner.visibility = View.GONE // Hide loading
                    binding.spinner2.visibility = View.GONE // Hide loading

                } catch (e: Exception) {
                    binding.spinner.visibility = View.GONE // Hide loading
                    binding.spinner2.visibility = View.GONE // Hide loading

                    Log.e("PDF_ERROR", "Error generating PDF", e)
                    Toast.makeText(
                        requireContext(),
                        "Failed to generate PDF",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } ?: run {
            Log.e("BILL_ERROR", "Bill data is null")
            Toast.makeText(requireContext(), "Bill not found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun observeChanges() {
        jewelryViewModel.items.observe(viewLifecycleOwner) { items ->
            adapterForItems.submitList(items)
            updateItemsVisibility(items.isNotEmpty())
        }

        sharedViewModel.paymentEntry.observe(viewLifecycleOwner) { paymentEntry ->
            adapterForPayment.submitList(paymentEntry)
            updatePaymentsVisibility(paymentEntry.isNotEmpty())
        }

        jewelryViewModel.hasChanges.observe(viewLifecycleOwner) {
            updateUpdateButtonVisibility()
        }

        sharedViewModel.hasChangesPayments.observe(viewLifecycleOwner) {
            updateUpdateButtonVisibility()
        }
    }

    private fun updateUpdateButtonVisibility() {
        val shouldShowUpdateButton = jewelryViewModel.hasChanges.value == true ||
                sharedViewModel.hasChangesPayments.value == true
        binding.btnUpdateBill.visibility = if (shouldShowUpdateButton) View.VISIBLE else View.GONE
    }

    private fun initializeBillData() {
        val billId = arguments?.getInt(ARG_BILL_ID) ?: run {
            showError("No bill ID provided")
            findNavController().navigateUp()
            return
        }

        if (!jewelryViewModel.hasChanges.value!!) {
            billInputViewModel.getBillInputsById(billId).observe(viewLifecycleOwner) { bill ->
                bill?.let {
                    jewelryViewModel.initializeItemList(it.itemList)
                    updateBillDetails(it)
                } ?: showError("Bill not found")
            }
        }

        if (!sharedViewModel.hasChangesPayments.value!!) {
            billInputViewModel.getBillInputsById(billId).observe(viewLifecycleOwner) { bill ->
                bill?.let {
                    sharedViewModel.initializePaymentList(it.paymentList)
                    updateBillDetails(it)
                }
            }
        }
    }

    private fun updateBillDetails(bill: BillInputs) {
        phoneNumber = bill.customerNumber
        binding.apply {
            tvCustomerName.text = bill.customerName
            tvCustomerNumber.text = bill.customerNumber
            tvAddress.text = bill.customerAddress
            tvInvoiceNumber.text = "#${bill.invoiceNumber}"
            tvBillingDate.text = bill.date


            when (bill.status) {
                "Pending" -> {
                    chipBillingStatus.text = "Pending"
                    chipBillingStatus.setChipBackgroundColorResource(R.color.Red)
                }

                "Paid" -> {
                    chipBillingStatus.text = "Paid"
                    chipBillingStatus.setChipBackgroundColorResource(R.color.status_completed)
                }

                "Partially Paid" -> {
                    chipBillingStatus.text = "Partially Paid"
                    chipBillingStatus.setChipBackgroundColorResource(R.color.status_pending)
                }
            }


        }
    }

    private fun setUpRecyclerView() {
        adapterForItems = ExpandableAdapter_Display { item ->
            navigateToNewBill(item)
        }

        adapterForPayment = ReceviedPaymentAdapter_Display { payment ->
            navigateToPaymentEntry()
        }

        binding.apply {
            rvBillItems.apply {
                // Add layout animation when the list first loads
                layoutAnimation = LayoutAnimationController(
                    AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
                ).apply {
                    delay = 0.15f
                    order = LayoutAnimationController.ORDER_NORMAL
                }
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterForItems
            }

            rvPayments.apply {
                // Add layout animation when the list first loads
                layoutAnimation = LayoutAnimationController(
                    AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
                ).apply {
                    delay = 0.15f
                    order = LayoutAnimationController.ORDER_NORMAL
                }
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterForPayment
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnAddBillItem.setOnClickListener {
                navigateToNewBill(null)
            }

            btnPayment.setOnClickListener {
                navigateToPaymentEntry()
            }

            btnUpdateBill.setOnClickListener {
                updateBill()
            }
            // Set toolbar menu item click listener
            topAppBar.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.buttonCall -> {
                        callPhoneNumber(phoneNumber) // Replace with a dynamic phone number
                        true
                    }

                    R.id.buttonOpenPdf -> {
                        openPdfFromFileName()
//                        openPdf("file://path_to_your_pdf") // Replace with the actual PDF path
                        true
                    }

                    R.id.buttonPrintPdf -> {
                        printFile(requireContext(), file.toString())
                        true
                    }


                    R.id.buttonSharePdf -> {
                        sharePdfFromPath(file)
                        true
                    }

                    R.id.buttonShareWhatsApp -> {
                        shareFileToWhatsApp(file)
                        true
                    }

                    R.id.buttonPaid -> {

                        val billId = arguments?.getInt("billId")
                        val bill = billInputViewModel.getBillInputsById(billId!!)

                        bill.observe(viewLifecycleOwner) {
                            val paymentReceived = PaymentRecived(
                                amount = it!!.dueAmount,
                                type = RecivedPaymentType.RECEIVED,
                                date = formattedDate.toString(),
                                paymentMethod = PaymentMethod.LATE_PAYMENT,
                            )
                            val paymentList: MutableList<PaymentRecived> =
                                it.paymentList.toMutableList()
                            paymentList.add(paymentReceived)

                            val newRecivedAmount = it.receviedAmount + it.dueAmount

                            val newBill = it.copy(
                                paymentList = paymentList,
                                dueAmount = 0,
                                receviedAmount = newRecivedAmount,
                                status = "Paid"

                            )
                            billInputViewModel.updateBillInputs(newBill)
                            findNavController().popBackStack()

                        }



                        true
                    }

//                    R.id.buttonSendSms -> {
//                        val billId = arguments?.getInt("billId")
//                        billInputViewModel.getBillInputsById(billId!!).observe(viewLifecycleOwner) {
//                            sendSMS(
//                                it!!.customerNumber,
//                                " Dear ${it.customerNumber}, your outstanding bill of ${it.dueAmount} is due. Kindly make the payment at your earliest convenience. Please ignore if already paid. Contact us at [Your Contact] for any queries. Thank you."
//                            )
//                        }
//                        true
//                    }


                    R.id.buttonSavePdf -> {
                        val success = saveFile(file)
                        if (success) {
                            // File saved successfully
                            Toast.makeText(context, "File saved successfully", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            // Handle error
                            Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT)
                                .show()
                        }

                        true
                    }


                    R.id.buttonDeleteInvoice -> {
                        val billId = arguments?.getInt(ARG_BILL_ID)
                        billInputViewModel.deleteBillInputsById(billId = billId!!)
                        findNavController().popBackStack()

                        true
                    }

                    else -> false
                }
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }

        if (!allGranted) {
            Toast.makeText(requireContext(), "Some permissions were denied!", Toast.LENGTH_SHORT)
                .show()
        }

        // Handle file operation if permission was granted
        if (allGranted && pendingFile != null) {
            lifecycleScope.launch {
                processSaveFile(pendingFile!!, pendingFileName)
            }
            pendingFile = null
            pendingFileName = null
        }
    }


//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (!isGranted) {
//                Toast.makeText(requireContext(), "SMS permission denied!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//
//    // Register permission launcher
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        val allGranted = permissions.entries.all { it.value }
//        if (allGranted) {
//            pendingFile?.let { file ->
//                lifecycleScope.launch {
//                    processSaveFile(file, pendingFileName)
//                }
//            }
//        }
//        // Reset pending operations
//        pendingFile = null
//        pendingFileName = null
//    }

    // Function to save file
    fun saveFile(sourceFile: File, newFileName: String? = null): Boolean {
        return try {
            if (checkAndRequestPermissions(sourceFile, newFileName)) {
                processSaveFile(sourceFile, newFileName)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Check and request storage permissions
    private fun checkAndRequestPermissions(sourceFile: File, newFileName: String?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // Android 10 and above use scoped storage
        } else {
            val writePermission = ContextCompat.checkSelfPermission(
                requireContext(),
                WRITE_EXTERNAL_STORAGE
            )

            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                // Store pending operation
                pendingFile = sourceFile
                pendingFileName = newFileName

                // Request permissions
                requestPermissionLauncher.launch(
                    arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
                )
                false
            } else {
                true
            }
        }
    }

    // Process file saving
    private fun processSaveFile(sourceFile: File, newFileName: String?): Boolean {
        val fileName = newFileName ?: sourceFile.name
        val destinationFile = createFile(fileName)
        return try {
            copyFile(sourceFile, destinationFile)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Create file in downloads directory
    private fun createFile(fileName: String): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        return File(downloadsDir, fileName)
    }

    // Copy file content
    private fun copyFile(sourceFile: File, destinationFile: File) {
        FileInputStream(sourceFile).use { input ->
            FileOutputStream(destinationFile).use { output ->
                val buffer = ByteArray(4 * 1024) // 4KB buffer
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
    }


    fun shareFileToWhatsApp(file: File) {
        val uri: Uri =
            FileProvider.getUriForFile(requireContext(), "com.Gst.pdfs.fileprovider", file)

        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf" // Adjust based on file type
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val packageManager = requireContext().packageManager
        val resolveInfoList = packageManager.queryIntentActivities(whatsappIntent, 0)
        val targetedShareIntents = mutableListOf<Intent>()

        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == "com.whatsapp" || packageName == "com.whatsapp.w4b") {
                val targetedIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    `package` = packageName
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                targetedShareIntents.add(targetedIntent)
            }
        }

        if (targetedShareIntents.isNotEmpty()) {
            val chooserIntent =
                Intent.createChooser(targetedShareIntents.removeAt(0), "Share file with")
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                targetedShareIntents.toTypedArray()
            )
            startActivity(chooserIntent)
        } else {
            // Handle case when neither WhatsApp nor WhatsApp Business is installed
            Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun sharePdfFromPath(f: File) {

        if (f.exists()) {
            // Create an intent to share the PDF file
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"

            // Use a FileProvider to get a content URI for the file
            val pdfUri = FileProvider.getUriForFile(
                binding.root.context,
                "com.Gst.pdfs.fileprovider", // Replace with your app's package name
                f.absoluteFile
            )

            // Set the URI as the data to be shared
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)

            // Add a subject for the shared content (optional)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing PDF")


            // Start an activity to show the sharing options
            startActivity(Intent.createChooser(shareIntent, "Share PDF"))

        } else {
            // Handle the case where the PDF file doesn't exist
            Toast.makeText(binding.root.context, "PDF file not found", Toast.LENGTH_SHORT).show()
        }
    }

    fun printFile(context: Context, filePath: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = "PrintJob"
        val printAdapter = viewBill.WebViewPrintAdapter(context, filePath)

        printManager.print(jobName, printAdapter, null)
    }


    private fun openPdfFromFileName() {
        val bundle = Bundle()
        bundle.putString("file", file.toString())
        Log.d("file", "file $file")
        if (file.toString().isNotEmpty()) {
            findNavController().navigate(
                R.id.action_listBillView_to_viewBill,
                bundle
            )
        }
    }

    private fun callPhoneNumber(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(callIntent)
    }


    private fun updateBill() {
        val billId = arguments?.getInt(ARG_BILL_ID) ?: run {
            showError("No bill ID provided")
            return
        }

        billInputViewModel.getBillInputsById(billId).observe(viewLifecycleOwner) { currentBill ->
            currentBill?.let { bill ->
                val totalAmount = calculateTotalAmount()
                val (receivedAmount, extraCharges) = calculatePaymentTotals()
                val finalTotalAmount = totalAmount + extraCharges
                val dueAmount = finalTotalAmount - receivedAmount

                var status = "Pending"


                if (dueAmount > 0 && receivedAmount == 0) {
                    status = "Pending"
                } else if (dueAmount == 0) {
                    status = "Paid"
                } else if (receivedAmount > 0 && dueAmount > 0) {
                    status = "Partially Paid"
                }

                val updatedBill = bill.copy(
                    itemList = jewelryViewModel.items.value ?: emptyList(),
                    paymentList = sharedViewModel.paymentEntry.value ?: emptyList(),
                    totalAmount = totalAmount,
                    receviedAmount = receivedAmount,
                    dueAmount = dueAmount,
                    status = status
                )

                billInputViewModel.updateBillInputs(updatedBill)
                jewelryViewModel.resetChanges()
                sharedViewModel.clearRevivedPayments()

                findNavController().navigate(R.id.action_listBillView_to_mainScreen)
            } ?: showError("Bill not found")
        }
    }

    private fun calculatePaymentTotals(): Pair<Int, Int> {
        var receivedAmount = 0
        var extraCharges = 0

        sharedViewModel.paymentEntry.value?.forEach { payment ->
            when (payment.type) {
                RecivedPaymentType.RECEIVED -> receivedAmount += payment.amount
                RecivedPaymentType.EXTRA_CHARGE -> extraCharges += payment.amount
                else -> {}
            }
        }

        return Pair(receivedAmount, extraCharges)
    }

    private fun navigateToNewBill(item: ItemModel?) {
        findNavController().navigate(
            R.id.action_listBillView_to_newBill,
            Bundle().apply {
                putBoolean(ARG_EDIT_BILL, true)
            }
        )
    }

    private fun navigateToPaymentEntry() {
        val totalAmount = calculateTotalAmount()
        if (totalAmount > 0) {
            findNavController().navigate(
                R.id.action_listBillView_to_paymentEntry,
                Bundle().apply {
                    putBoolean(ARG_EDIT_PAYMENT, true)
                    putInt(ARG_TOTAL_AMOUNT, totalAmount)
                }
            )
        } else {
            showError("Total amount must be greater than 0")
        }
    }

    private fun updateItemsVisibility(hasItems: Boolean) {
        binding.apply {
            rvBillItems.visibility = if (hasItems) View.VISIBLE else View.GONE
            tvBillItemsHeader.visibility = if (hasItems) View.VISIBLE else View.GONE
            btnAddBillItem.visibility = if (hasItems) View.GONE else View.VISIBLE
        }
    }

    private fun updatePaymentsVisibility(hasPayments: Boolean) {
        binding.apply {
            rvPayments.visibility = if (hasPayments) View.VISIBLE else View.GONE
            tvPaymentsHeader.visibility = if (hasPayments) View.VISIBLE else View.GONE
            btnPayment.visibility = if (hasPayments) View.GONE else View.VISIBLE
        }
    }

    private fun calculateTotalAmount(): Int {

        //total amount should

        return jewelryViewModel.items.value?.sumOf { it.totalValue.toInt() } ?: 0
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        Log.e("ListBillViewFragment", message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}