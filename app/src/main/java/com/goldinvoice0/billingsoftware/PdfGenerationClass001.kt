package com.goldinvoice0.billingsoftware

//import com.goldinvoice0.billingsoftware.Model.PdfData
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import com.goldinvoice0.billingsoftware.Model.BillInputs
import com.goldinvoice0.billingsoftware.Model.Shop
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.tom_roush.harmony.awt.AWTColor
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat

class PdfGenerationClass001 {
    lateinit var font: PDType0Font
    lateinit var font2: PDType0Font
    fun createPdfFromView(
        context: Context,
        bill: BillInputs,
        shop: Shop?,
        fileName: String
    ): File {

        val descriptionList: MutableList<String> = mutableListOf()
        val grWtList: MutableList<Float> = mutableListOf()
        val ntWtList: MutableList<Float> = mutableListOf()
        val pcsList: MutableList<Int> = mutableListOf()
        val makingChargeList: MutableList<Int> = mutableListOf()
        val stoneValueList: MutableList<Int> = mutableListOf()
        val karatList: MutableList<String> = mutableListOf()
        val goldPriceList: MutableList<Int> = mutableListOf()
        val totalList: MutableList<Long> = mutableListOf()


        bill.itemList.forEach {
            descriptionList.add(it.name)
            grWtList.add(it.grossWeight)
            ntWtList.add(it.netWeight)
            pcsList.add(it.piece)
            makingChargeList.add(it.finalMakingCharges)
            stoneValueList.add(it.stoneValue)
            karatList.add(it.karat)
            goldPriceList.add(it.rateOfJewellery)
            totalList.add(it.totalValue)
        }


        val document = PDDocument()
        val page = PDPage(PDRectangle.A4)
        document.addPage(page)
        val contentStream = PDPageContentStream(document, page)

        val assetManager: AssetManager = context.assets
        var inputStream: InputStream? = null
        inputStream = assetManager.open("notosans.ttf")
        var inputStrem2: InputStream? = null
        inputStrem2 = assetManager.open("NotoSans_Condensed-SemiBold.ttf")
        // Load the Noto Sans font for both English and Hindi
        font = PDType0Font.load(
            document, inputStream
        )
        font2 = PDType0Font.load(document, inputStrem2)
        val pageWidth = page.mediaBox.width
        val pageHeight = page.mediaBox.height


        //Heading
        printStringInCenter(
            contentStream, "TAX INVOICE", 22f, pageWidth, pageHeight, AWTColor.BLACK
        )

        val customerName = bill.customerName
        val mobilNumber = bill.customerNumber
        val address = bill.customerAddress
        //14f
        val shopName = shop!!.shopName
        val shopAddress = shop.address1
        val shopAddres2 = shop.address2
        val dateForPurched = "Date:"
        val date = bill.date
        val gstText = "GSTIN :-"
        val gstNumber = shop.gstNumber
        val invoiceNumber = bill.invoiceNumber
        val list = mutableListOf<String>(customerName, mobilNumber, address)
        val list2 = mutableListOf<String>(shopName, shopAddress, shopAddres2)


        //This will draw the outline of the pdf
        drawRectangle(contentStream, 20f, 20f, pageWidth - 40f, pageHeight - 40f, AWTColor.BLACK)

        //draw a line
        drawLine(
            contentStream,
            20f,
            pageHeight - 45f,
            pageWidth - 20f,
            pageHeight - 45f,
            .5f,
            AWTColor.BLACK
        )


        //draw a line
        drawLine(
            contentStream,
            20f,
            pageHeight - 100f,
            pageWidth - 20f,
            pageHeight - 100f,
            .5f,
            AWTColor.BLACK
        )

        //Draw a line
        drawLine(
            contentStream,
            pageWidth / 2,
            pageHeight - 45f,
            pageWidth / 2,
            pageHeight - 100f - 34f,
            .5f,
            AWTColor.BLACK
        )

        val listOfPartyText = mutableListOf<String>("Party Name:", "Number:", "Address:")

        printList(contentStream, list, 100f, pageHeight - 60f)
        printListHeader(contentStream, listOfPartyText, 25f, pageHeight - 60f)


        drawTextHeading(
            contentStream,
            "Owner:",
            pageWidth / 2 + 5f,
            pageHeight - 60f,
            12f,
            AWTColor.BLACK
        )
        printListHeader(contentStream, list2, pageWidth / 2 + 75f, pageHeight - 60f)

        var yline = pageHeight - 100f

        //Date
        drawTextHeading(contentStream, dateForPurched, 25f, yline - 14f, 12f, AWTColor.BLACK)
        drawText(contentStream, date, 100f, yline - 14f, 12f, AWTColor.BLACK)
//        drawText(contentStream, date, pageWidth / 2 + 5f, yline - 14f, 12f, AWTColor.BLACK)
        drawLine(contentStream, 20f, yline - 19f, pageWidth - 20f, yline - 19f, .5f, AWTColor.BLACK)

        //Gst and Invoice Number

        val gline = yline - 15f
        drawTextHeading(contentStream, "GSTIN:", 25f, gline - 16f, 12f, AWTColor.BLACK)
        drawText(contentStream, gstNumber, 100f, gline - 16f, 12f, AWTColor.BLACK)
        Log.d("Tag", invoiceNumber + "inPGC001")

        drawTextHeading(
            contentStream,
            "INV NO.:",
            pageWidth / 2 + 6f,
            gline - 15f,
            12f,
            AWTColor.BLACK
        )

        drawText(
            contentStream,
            invoiceNumber,
            pageWidth / 2 + 75f,
            gline - 15f,
            12f,
            AWTColor.BLACK
        )
        drawLine(contentStream, 20f, gline - 19f, pageWidth - 20f, gline - 19f, .5f, AWTColor.BLACK)


        //Headline text
        val mline = gline - 19f

        drawTextHeading(contentStream, "S/N", 25f, mline - 13f, 10f, AWTColor.BLACK)
        drawTextHeading(contentStream, "DESCR ", 65f, mline - 13f, 10f, AWTColor.BLACK)
        drawTextHeading(contentStream, "Gr.Wt.", 132f, mline - 13f, 10f, AWTColor.BLACK)
        drawTextHeading(contentStream, "Net.Wt", 179f, mline - 13f, 10f, AWTColor.BLACK)
        drawTextHeading(contentStream, "PCS", 226f, mline - 13f, 10f, AWTColor.BLACK)
        drawTextHeading(contentStream, "Labour", 258f, mline - 13f, 10f, AWTColor.BLACK)
        drawTextHeading(contentStream, "Stone Val.", 304f, mline - 13f, 10f, AWTColor.BLACK) //329 x
        drawTextHeading(contentStream, "Purity", 361f, mline - 13f, 10f, AWTColor.BLACK)
        drawTextHeading(contentStream, "Rate", 421f, mline - 13f, 10f, AWTColor.BLACK)
        drawTextHeading(contentStream, "Net Total", 476f, mline - 13f, 10f, AWTColor.BLACK)

        drawLine(contentStream, 20f, mline - 20f, pageWidth - 20f, mline - 20f, .5f, AWTColor.BLACK)

        //list of pdf inputs
        val iline = mline - 19f
        bill.itemList.forEach {
            it.name
        }
        val listOfSN = mutableListOf<String>()
        for (i in 1..descriptionList.size) {
            listOfSN.add(i.toString())
        }

        val lastMainListItemY =
            iline - (descriptionList.size - 1).toFloat() * 15f - 13f - 6f

        printListHeader(contentStream, listOfSN, 26f, iline - 13f)
        drawLine(
            contentStream,
            43f,
            iline + 18.5f,
            43f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
//        printList(contentStream, descriptionList, 47f, iline - 13f)
        printListForDescriptionList(contentStream, descriptionList, 47f, iline - 13f)
        drawLine(
            contentStream,
            120f,
            iline + 18.5f,
            120f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
        printList(contentStream, grWtList.map { it.toString() }.toMutableList(), 122f, iline - 13f)
        drawLine(
            contentStream,
            170f,
            iline + 18.5f,
            170f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
        printList(contentStream, ntWtList.map { it.toString() }.toMutableList(), 173f, iline - 13f)
        drawLine(
            contentStream,
            220f,
            iline + 18.5f,
            220f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
        printList(contentStream, pcsList.map { it.toString() }.toMutableList(), 224f, iline - 13f)
        drawLine(
            contentStream,
            248f,
            iline + 18.5f,
            248f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
        printList(
            contentStream,
            makingChargeList.map { it.toString() }.toMutableList(),
            250f,
            iline - 13f
        ) //255 x
        drawLine(
            contentStream,
            300f,
            iline + 18.5f,
            300f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
        printList(
            contentStream,
            stoneValueList.map { it.toString() }.toMutableList(),
            304f,
            iline - 13f
        )
        drawLine(
            contentStream,
            351f,
            iline + 18.5f,
            351f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
        printList(contentStream, karatList, 357f, iline - 13f)
        drawLine(
            contentStream,
            397f,
            iline + 18.5f,
            397f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
        printList(
            contentStream,
            goldPriceList.map { it.toString() }.toMutableList(),
            401f,
            iline - 13f
        )
        drawLine(
            contentStream,
            470f,
            iline + 18.5f,
            470f,
            lastMainListItemY - 17f,
            .5f,
            AWTColor.BLACK
        )
        printListWithIndianStyle(
            contentStream,
            totalList.map { it.toString() }.toMutableList(),
            476f,
            iline - 13f
        )
        drawLine(
            contentStream,
            20f,
            lastMainListItemY,
            pageWidth - 20f,
            lastMainListItemY,
            .5f,
            AWTColor.BLACK
        )

        //total
        val tline = lastMainListItemY - 17f

        var totalNetValue = 0f
        for (i in ntWtList) {
            totalNetValue += i.toFloat()
        }
        var totalPcs = 0f
        for (i in pcsList) {
            totalPcs += i.toFloat()
        }
        var totalLabout = 0f
        for (i in makingChargeList) {
            totalLabout += i.toFloat()
        }
        var totalStoneValue = 0f
        for (i in stoneValueList) {
            totalStoneValue += i.toFloat()
        }
        var totalAmountLastFinal_001 = 0L
        for (i in totalList) {
            totalAmountLastFinal_001 += i.toLong()
        }
        val formattedValue_0001 = String.format("%.2f", totalNetValue)

        drawTextHeading(
            contentStream,
            "Net Total",
            63f,
            lastMainListItemY - 13f,
            10f,
            AWTColor.BLACK
        )
        drawTextHeading(
            contentStream,
            formattedValue_0001,
            173f,
            lastMainListItemY - 13f,
            12f,
            AWTColor.BLACK
        )
        drawTextHeading(
            contentStream,
            totalPcs.toInt().toString(),
            224f,
            lastMainListItemY - 13f,
            12f,
            AWTColor.BLACK
        )
        drawTextHeading(
            contentStream,
            totalLabout.toInt().toString(),
            250f,
            lastMainListItemY - 13f,
            12f,
            AWTColor.BLACK
        )
        drawTextHeading(
            contentStream,
            totalStoneValue.toInt().toString(),
            304f,
            lastMainListItemY - 13f,
            12f,
            AWTColor.BLACK
        )
        val temp1 = formatNumberToIndian(totalAmountLastFinal_001.toLong())
        drawTextHeading(
            contentStream,
            temp1.toString(),
            476f,
            lastMainListItemY - 13f,
            12f,
            AWTColor.BLACK
        )
        drawLine(contentStream, 20f, tline, pageWidth - 20f, tline, .5f, AWTColor.BLACK)

        //recived and added amount list
        var rlline = tline - 17f


        // Extract headerList and amountList
        val headerList: List<String> = bill.paymentList.map {
            it.paymentMethod?.name ?: it.extraChargeType?.name ?: "Unknown"
        }
        val amountList: List<Int> = bill.paymentList.map { it.amount }



        printListHeader(contentStream, headerList, 357f, rlline + 5f)
        printListWithIndianStyle(contentStream, amountList.map { it.toString() }, 476f, rlline + 5f)

        val d1 = lastMainListItemY - 17f
        val d2 = d1 - amountList.size.toFloat() * 15f

        drawLine(contentStream, 470f, d1, 470f, d2, .5f, AWTColor.BLACK)

        drawLine(contentStream, 351f, d1, 351f, d2, .5f, AWTColor.BLACK)

        drawLine(contentStream, 20f, d2, pageWidth - 20f, d2, .5f, AWTColor.BLACK)

        var s = ""
        if (bill.dueAmount > 0) {
            s = ("Due")
        } else if (bill.dueAmount == 0) {
            s = ("Cleared")
        }

        var latsLine = d2 - 17f
        val indianFormatNumber = formatNumberToIndian(bill.dueAmount.toLong())

        drawTextHeading(contentStream, s, 357f, latsLine + 2f, 12f, AWTColor.BLACK)
        drawTextHeading(
            contentStream,
            indianFormatNumber.toString(),
            476f,
            latsLine + 2f,
            12f,
            AWTColor.BLACK
        )

        val numberInWords = numberToWords(bill.dueAmount)
        drawTextHeading(
            contentStream,
            "In Words: " + numberInWords.toString(),
            25f,
            latsLine + 2f,
            8f,
            AWTColor.BLACK
        )


        drawLine(contentStream, 351f, d2, 351f, d2 - 18f, .5f, AWTColor.BLACK)
        drawLine(contentStream, 470f, d2, 470f, d2 - 18f, .5f, AWTColor.BLACK)

        drawLine(contentStream, 20f, d2 - 18f, pageWidth - 20f, d2 - 18f, .5f, AWTColor.BLACK)


//        val thankYouText = "Thank you for shopping with $shopName , Please Visit Again"
//        drawText(
//            contentStream,
//            thankYouText,
//            50.3f,
//            30.4f,
//            12f,
//            AWTColor.BLACK
//        )


        drawTextHeading(contentStream, "Authorized Signature", 453.25f, 30.3f, 12f, AWTColor.BLACK)
        drawTextHeading(contentStream, "Scan to Pay", 40f, 30.3f, 12f, AWTColor.BLACK)
        printStringInCenterFooter(
            contentStream, "Thank you for shopping with us!", 12f, pageWidth,
            AWTColor.BLACK
        )

        drawImage2(context, contentStream, document)


        //QR code scanner
        val qrCodeBitmap = generateUpiQrCode("9828441285@ybl")
        insertQRCodeImage(document, page, qrCodeBitmap, 25f, 42.5f)

        contentStream.close()
        // Save the PDF document
        val file = savePdfToFile(document, fileName, context)
        document.close()
        return file


    }

    private fun printListForDescriptionList(
        contentStream: PDPageContentStream,
        list: MutableList<String>,
        xPosition: Float,
        yPosition: Float
    ) {
        val lineHeight = 15f // Height of each line
        var yPosition = yPosition// Starting y-coordinate
        for (text in list) {
            drawText(contentStream, text, xPosition, yPosition, 8.2f, AWTColor.BLACK)
            yPosition -= lineHeight
        }
    }


    @Throws(IOException::class)
    private fun drawImageToPDF(
        contentStream: PDPageContentStream,
        imagePath: String,
        pdfDocument: PDDocument
    ) {
        try {
            val imageFile = File(imagePath)
            if (!imageFile.exists()) {
                Log.e("PDF Drawing", "Image file does not exist: $imagePath")
                return
            }

            // Load the image using PDFBox's PDImageXObject
            val pdImage = PDImageXObject.createFromFileByContent(imageFile, pdfDocument)

            // Set the position and size to draw the image
            val x = 300f // Adjust the X-coordinate as needed
            val y = 300f // Adjust the Y-coordinate as needed
            val width = 75f // Adjust the width as needed
            val height = 75f // Adjust the height as needed

            // Draw the image onto the PDF
            contentStream.drawImage(pdImage, x, y, width, height)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("PDF Drawing", "Error while drawing image: ${e.message}")
        }
    }

    fun printList(
        contentStream: PDPageContentStream,
        list: MutableList<String>,
        xPosition: Float,
        yPosition: Float
    ) {
        val lineHeight = 15f // Height of each line
        var yPosition = yPosition// Starting y-coordinate
        for (text in list) {
            drawText(contentStream, text, xPosition, yPosition, 10f, AWTColor.BLACK)
            yPosition -= lineHeight
        }
    }

    fun printListWithIndianStyle(
        contentStream: PDPageContentStream,
        list: List<String>,
        xPosition: Float,
        yPosition: Float
    ) {
        val lineHeight = 15f // Height of each line
        var yPosition = yPosition// Starting y-coordinate
        for (text in list) {
            val temp = formatNumberToIndian(text.toLong())
            drawText(contentStream, temp.toString(), xPosition, yPosition, 12f, AWTColor.BLACK)
            yPosition -= lineHeight
        }
    }

    fun printListHeader(
        contentStream: PDPageContentStream,
        list: List<String>,
        xPosition: Float,
        yPosition: Float
    ) {
        val lineHeight = 15f // Height of each line
        var yPosition = yPosition// Starting y-coordinate
        for (text in list) {
            drawTextHeading(contentStream, text, xPosition, yPosition, 12f, AWTColor.BLACK)
            yPosition -= lineHeight
        }
    }

    fun printListWithLine(
        contentStream: PDPageContentStream,
        list: MutableList<String>,
        xPosition: Float,
        yPosition: Float
    ) {
        val lineHeight = 15f // Height of each line
        var yPosition = yPosition// Starting y-coordinate
        for (text in list) {
            drawText(contentStream, text, xPosition, yPosition, 12f, AWTColor.BLACK)
            yPosition -= lineHeight
        }
    }

    fun drawText(
        contentStream: PDPageContentStream,
        text: String,
        x: Float,
        y: Float,
        fontSize: Float,
        color: AWTColor
    ) {

        contentStream.setFont(font, fontSize)
        contentStream.setNonStrokingColor(color)
        contentStream.beginText()
        contentStream.newLineAtOffset(x, y)
        contentStream.showText(text)
        contentStream.endText()
    }

    fun drawTextHeading(
        contentStream: PDPageContentStream,
        text: String,
        x: Float,
        y: Float,
        fontSize: Float,
        color: AWTColor
    ) {

        contentStream.setFont(font2, fontSize)
        contentStream.setNonStrokingColor(color)
        contentStream.beginText()
        contentStream.newLineAtOffset(x, y)
        contentStream.showText(text)
        contentStream.endText()
    }

    private fun savePdfToFile(pdfDocument: PDDocument, fileName: String, context: Context): File {
        val cacheDirectory = context.cacheDir
        val file = File(cacheDirectory, "$fileName.pdf")

        try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.save(fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("Print", "${e.message}")
        } finally {
            pdfDocument.close()

        }
        return file

    }

    fun drawLine(
        contentStream: PDPageContentStream,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        lineWidth: Float,
        color: AWTColor
    ) {
        contentStream.setStrokingColor(color)
        contentStream.setLineWidth(lineWidth)
        contentStream.moveTo(x1, y1)
        contentStream.lineTo(x2, y2)
        contentStream.stroke()
    }

    fun drawRectangle(
        contentStream: PDPageContentStream,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: AWTColor
    ) {
        contentStream.setStrokingColor(color)
        contentStream.setLineWidth(0.5f)
        contentStream.addRect(x, y, width, height)

        contentStream.stroke()
    }

    fun printStringInCenter(
        contentStream: PDPageContentStream,
        string: String,
        fontSize: Float,
        pageWidth: Float,
        pageHight: Float,
        color: AWTColor
    ) {
        val centerX = pageWidth / 2
        val font = font2
        val textWidth = font.getStringWidth(string) / 1000 * fontSize
        val textX = centerX - (textWidth / 2)

        drawTextForCenter(contentStream, string, textX, pageHight - 40f, fontSize, color)

    }

    fun printStringInCenterFooter(
        contentStream: PDPageContentStream,
        string: String,
        fontSize: Float,
        pageWidth: Float,
        color: AWTColor
    ) {
        val centerX = pageWidth / 2
        val font = font2
        val textWidth = font.getStringWidth(string) / 1000 * fontSize
        val textX = centerX - (textWidth / 2)

        drawTextForCenter(contentStream, string, textX, 30.3f, fontSize, color)

    }

    fun drawTextForCenter(
        contentStream: PDPageContentStream,
        text: String,
        x: Float,
        y: Float,
        fontSize: Float,
        color: AWTColor
    ) {
        contentStream.setFont(font, fontSize)
        contentStream.setNonStrokingColor(color)
        contentStream.beginText()
        contentStream.newLineAtOffset(x, y)
        contentStream.showText(text)
        contentStream.endText()
    }

    private fun generateUpiQrCode(upiId: String): Bitmap {
        // Create UPI payment string
        val upiPaymentString =
            "upi://pay?pa=${Uri.encode(upiId)}&cu=INR"


        // Generate QR code
        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix = writer.encode(
            upiPaymentString,
            BarcodeFormat.QR_CODE,
            512,
            512
        )

        // Convert to Bitmap
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }

    fun drawImage2(context: Context, contentStream: PDPageContentStream, document: PDDocument) {
        try {
            // Assuming the signature image is stored in the app's internal storage
            val signaturePath = File(context.filesDir, "signature.png").absolutePath
            val imageFile = File(signaturePath)

            if (imageFile.exists()) {
                // Load the image into the PDF
                val image = PDImageXObject.createFromFile(imageFile.absolutePath, document)

                // Define the position and size of the image
                val x = 460.25f // X position (left)
                val y = 40.3f   // Y position (top)
                val width = 100f  // Width of the image
                val height = 80f   // Height of the image

                // Draw the image on the page
                contentStream.drawImage(image, x, y, width, height)
            } else {
                Log.e("PdfGenerator", "Signature image not found at path: $signaturePath")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun insertQRCodeImage(
        document: PDDocument,
        page: PDPage,
        qrCodeBitmap: Bitmap,
        x: Float,
        y: Float
    ) {
        // Convert Android Bitmap to ByteArray
        val stream = ByteArrayOutputStream()
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageBytes = stream.toByteArray()

        // Create PDImageXObject from the byte array
        val qrCodeImage = LosslessFactory.createFromImage(document, qrCodeBitmap)

        // Create a content stream to draw the image
        PDPageContentStream(
            document,
            page,
            PDPageContentStream.AppendMode.APPEND,
            true,
            true
        ).use { contentStream ->
            // Draw the image at the specified position
            contentStream.drawImage(
                qrCodeImage,
                x,  // x position
                y,  // y position
                100f,  // width
                100f   // height
            )
        }
    }

    // Function to convert numbers to words
    fun numberToWords(number: Int): String {
        if (number == 0) return "Zero"
        if (number < 0) return "Negative " + numberToWords(-number)

        val units = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
        )
        val teens = arrayOf(
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf(
            "", "Ten", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"
        )

        fun convertLessThanThousand(n: Int): String {
            if (n == 0) return ""
            if (n < 10) return units[n]
            if (n < 20) return teens[n - 10]
            if (n < 100) return tens[n / 10] + if (n % 10 != 0) " " + units[n % 10] else ""
            return units[n / 100] + " Hundred" + if (n % 100 != 0) " and " + convertLessThanThousand(
                n % 100
            ) else ""
        }

        // Indian numbering system: thousand, lakh (100,000), crore (10,000,000)
        var result = ""
        var num = number

        // Handle crores (10,000,000+)
        if (num >= 10000000) {
            result += convertLessThanThousand(num / 10000000) + " Crore "
            num %= 10000000
        }

        // Handle lakhs (100,000 to 9,999,999)
        if (num >= 100000) {
            result += convertLessThanThousand(num / 100000) + " Lakh "
            num %= 100000
        }

        // Handle thousands (1,000 to 99,999)
        if (num >= 1000) {
            result += convertLessThanThousand(num / 1000) + " Hazaar "
            num %= 1000
        }

        // Handle remaining hundreds, tens and units
        if (num > 0) {
            result += convertLessThanThousand(num)
        }

        return result.trim()
    }

    // Function to convert numbers to Indian numbering format
    fun formatNumberToIndian(number: Long): String {
        val formatter = DecimalFormat("##,##,###")
        return formatter.format(number)
    }
}


