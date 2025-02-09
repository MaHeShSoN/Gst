package com.goldinvoice0.billingsoftware

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.goldinvoice0.billingsoftware.Model.PdfData
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.ViewModel.PdfFinalDataViewModel
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentViewBillBinding
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import come.Gst.pdf.PdfGenerationClasses.PdfGeneratorclass2
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class viewBill : Fragment() {


    private var _binding: FragmentViewBillBinding? = null
    private val binding get() = _binding!!
    lateinit var f: File
    lateinit var pdfData1: PdfData
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val shopViewModel: ShopViewModel by viewModels()
    private lateinit var shop: Shop
    private var list = mutableListOf("")
    private var amount12: Int = 0
    private val pdfFinalDataViewModel: PdfFinalDataViewModel by viewModels()
    var nextNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewBillBinding.inflate(inflater, container, false)

        val bundal = arguments
//        amount12 = bundal!!.getInt("amount")
        val file = bundal!!.getString("fileName")
        f = File(file!!)

        lifecycleScope.launch {
            sharedViewModel.getReceivedList().observe(viewLifecycleOwner) {
                pdfData1 = PdfData(
                    it.name,
                    it.fileName,
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
                Log.d("Tag","${pdfData1.date}")

                amount12 = it.totalAmount
                list = it.receivedList
                nextNumber = it.invoiceNumber
            }
            shopViewModel.shop.observe(viewLifecycleOwner) { it ->
                shop = it
            }
        }

        PDFBoxResourceLoader.init(binding.root.context)
        binding.pdfViewMF.fromFile(f).load()

        binding.addButton.setOnClickListener {
            sharePdfFromPath()
        }
        binding.subbtractButton.setOnClickListener {
            printFile(binding.root.context, f.absolutePath)
        }



        // Inflate the layout for this fragment
        return binding.root
    }

    fun printFile(context: Context, filePath: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = "PrintJob"
        val printAdapter = WebViewPrintAdapter(context, filePath)

        printManager.print(jobName, printAdapter, null)
    }

    class WebViewPrintAdapter(private val context: Context, private val filePath: String) :
        PrintDocumentAdapter() {
        override fun onLayout(
            oldAttributes: PrintAttributes,
            newAttributes: PrintAttributes,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback,
            extras: Bundle
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback.onLayoutCancelled()
            } else {
                val builder = PrintDocumentInfo.Builder("file")
                builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build()
                callback.onLayoutFinished(builder.build(), !newAttributes.equals(oldAttributes))
            }
        }

        override fun onWrite(
            pages: Array<PageRange>,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal,
            callback: WriteResultCallback
        ) {
            val input = FileInputStream(filePath)
            val output = FileOutputStream(destination.fileDescriptor)

            try {
                input.copyTo(output)
                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback.onWriteFailed(e.message)
            } finally {
                input.close()
                output.close()
            }
        }
    }

    private fun sharePdfFromPath() {

//        val pdfFilePath = path.replace(Regex("\\.pdf"), "")
//        val newPdfPath = "$pdfFilePath.pdf"
//        val pdfFile = File(path)
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

//    private fun showColorPickerDialog() {
//        ColorPickerDialogBuilder
//            .with(binding.root.context)
//            .setTitle("Choose color")
//            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
//            .density(12)
//            .setOnColorSelectedListener {
//                // Handle color selection if needed
//            }
//            .setPositiveButton("OK") { _, selectedColor, _ ->
//                val rgb = convertColorIntToRgb(selectedColor)
//                saveSelectedColorToSharedPreferences(selectedColor)
//                updateSelectedColorButton(rgb.first, rgb.second, rgb.third)
//            }
//            .setNegativeButton("Cancel") { _, _ ->
//                // Handle cancel if needed
//            }
//            .build()
//            .show()
//    }

    private fun convertColorIntToRgb(colorInt: Int): Triple<Int, Int, Int> {
        val red = Color.red(colorInt)
        val green = Color.green(colorInt)
        val blue = Color.blue(colorInt)
        return Triple(red, green, blue)
    }

//    private fun saveSelectedColorToSharedPreferences(selectedColor: Int) {
//        val selectedColor = selectedColor.toString()
//        val sharedPreferences =
//            requireContext().getSharedPreferences("colorPreferences", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("selectedColor", selectedColor)
//        editor.apply()
//    }
//
//    private fun updateSelectedColorButton(red: Int, green: Int, blue: Int) {
//        // Set the color of the button or any other UI element you want to update
//        val mnuFB: MenuItem = binding.topAppBar.menu.findItem(R.id.colorPicker)
//        val iconTintColor = Color.rgb(red, green, blue)
//        val iconTintList = ColorStateList.valueOf(iconTintColor)
//        mnuFB.iconTintList = iconTintList
//
//        if(pdfData1.descriptionList.isNotEmpty()){
//            val fileName = PdfGeneratorclass2(binding.root.context).createPdfFromView(
//                pdfData = pdfData1,
//                binding.root.context,
//                list,
//                amount12,
//                shop,
//                nextNumber
//            )
//
//            binding.pdfViewMF.fromFile(fileName).load()
//        }else{
//            Toast.makeText(requireContext(),"Please Try Again",Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun getSelectedColorFromSharedPreferences(): Int {
        val sharedPreferences =
            requireContext().getSharedPreferences("colorPreferences", Context.MODE_PRIVATE)
        val colorString = sharedPreferences.getString("selectedColor", "-650051840")
        return colorString?.toInt() ?: 0
    }

//    private fun setMenuIconTint() {
//        val mnuFB: MenuItem = binding.topAppBar.menu.findItem(R.id.colorPicker)
//        val selectedColor = getSelectedColorFromSharedPreferences()
//        val rgbColor = convertColorIntToRgb(selectedColor)
//        val red = rgbColor.first
//        val green = rgbColor.second
//        val blue = rgbColor.third
//        val iconTintColor = Color.rgb(red, green, blue)
//        val iconTintList = ColorStateList.valueOf(iconTintColor)
//        mnuFB.iconTintList = iconTintList
//    }

    override fun onStart() {
        super.onStart()
//        setMenuIconTint()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

















