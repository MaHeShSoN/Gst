//package come.Gst.pdf.PdfGenerationClasses
//
//
//import android.content.Context
//import android.content.res.AssetManager
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.net.Uri
//import android.util.Log
//import com.goldinvoice0.billingsoftware.Model.PdfData
//import com.goldinvoice0.billingsoftware.Model.Shop
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.common.BitMatrix
//import com.google.zxing.qrcode.QRCodeWriter
//import com.tom_roush.harmony.awt.AWTColor
//import com.tom_roush.pdfbox.pdmodel.PDDocument
//import com.tom_roush.pdfbox.pdmodel.PDPage
//import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
//import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
//import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
//import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
//import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
//import java.io.ByteArrayOutputStream
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.InputStream
//
//
//class PdfGeneratorclass2(context: Context) {
//    lateinit var last_total_value: String
//    lateinit var last_y_line: String
//    lateinit var selectedAWTColor: AWTColor
//    lateinit var font: PDType0Font
//
//    fun createPdfFromView(
//        pdfData: PdfData,
//        context: Context,
//        list1212: MutableList<String>,
//        totalAmount1: Int,
//        shop: Shop?,
//        invoiceNumber: String
//    ): File {
//        val colorInt = getSelectedColorFromSharedPreferences(context = context)
//        val rgbColor = convertColorIntToRgb(colorInt)
//        val red = rgbColor.first
//        val green = rgbColor.second
//        val blue = rgbColor.third
//        selectedAWTColor = AWTColor(red, green, blue)
//
//        val document = PDDocument()
//        val page = PDPage(PDRectangle.A4)
//        document.addPage(page)
//        val contentStream = PDPageContentStream(document, page)
//
//        val assetManager: AssetManager = context.assets
//        var inputStream: InputStream? = null
//        inputStream = assetManager.open("notosans.ttf")
//
//        // Load the Noto Sans font for both English and Hindi
//        font = PDType0Font.load(
//            document,
//            inputStream
//        )
//
//        val pageWidth = page.mediaBox.width
//        val pageHeight = page.mediaBox.height
//        //Heading
//        printStringInCenter(
//            contentStream,
//            "INVOICE",
//            24f,
//            pageWidth,
//            pageHeight,
//            selectedAWTColor
//        )
//
//
//        //16dp
//        val billedtoText = "Billed To"
//        val billedFromText = "Billed From"
//        //14dp
//        val customerName = pdfData.name
//        val mobilNumber = pdfData.phone
//        val address = pdfData.address
//        //14f
//        val shopName = shop!!.shopName
//        val shopAddress = shop.address1
//        val shopAddres2 = shop.address2
//        val dateForPurched = "Date Of Purchased Jewellery"
//        val date = pdfData.date
//        val gstText = "GSTIN :-"
//        val gstNumber = shop.gstNumber
//        val list = mutableListOf<String>(customerName, mobilNumber, address)
//        val list2 = mutableListOf<String>(shopName, shopAddress, shopAddres2)
//        val blueAWTColor = AWTColor(13, 128, 237)
//        val headingXLine = 16 + 14 * 3
//        val rectY = pageHeight - 157.87f
//        //upperRect
//        drawRectangle(
//            contentStream,
//            20f,
//            rectY,
//            pageWidth - 40f,
//            headingXLine.toFloat() + 10f,
//            AWTColor.BLACK
//        )
//        val textY = rectY + 52.61f
//
//
//        //Upper Rect Text
//        drawColoredRect(contentStream, 20.3f, textY - 4f, pageWidth - 41f, 19f, selectedAWTColor)
//        drawLine(contentStream, 20f, textY - 4, pageWidth - 20f, textY - 4, 1f, AWTColor.BLACK)
//
//        //LeftSide
//        drawTextForColorRect(contentStream, billedtoText, 26f, textY, 14f, AWTColor.black)
//        val listY = textY - 16f
//        printList(contentStream, list, 25f, listY)
//
//        //RightSide
//        drawTextForColorRect(
//            contentStream,
//            billedFromText,
//            pageWidth - 168.817f,
//            textY,
//            14f,
//            AWTColor.BLACK
//        )
//        printList(contentStream, list2, pageWidth - 168.817f, listY)
//
//        //forDate
//        val dateY = rectY - 18f
//        drawRectangle(contentStream, 20f, dateY, pageWidth - 40f, 18f, AWTColor.BLACK)
//        drawText(contentStream, dateForPurched, 26f, rectY - 13f, 12f, AWTColor.BLACK)
//        drawText(contentStream, date, pageWidth - 168.817f, rectY - 13f, 12f, AWTColor.BLACK)
//
//        //ForGstNo and Gold Rate
//        val gstY = dateY - 18f
//        drawRectangle(contentStream, 20f, gstY, pageWidth - 40f, 18f, AWTColor.BLACK)
//        drawText(contentStream, gstText, 26f, dateY - 13f, 12f, AWTColor.BLACK)
//        drawText(contentStream, gstNumber, 80f, dateY - 13f, 12f, AWTColor.BLACK)
//
//        drawText(
//            contentStream,
//            "Invoice No. : $invoiceNumber",
//            pageWidth - 168.817f,
//            dateY - 13f,
//            12f,
//            AWTColor.BLACK
//        )
//
//
//
//        //ForMainListTital
//        val mainListY = gstY - 18f
//        drawColoredRect(contentStream, 20.3f, mainListY, pageWidth - 41f, 18f, selectedAWTColor)
//
//
//        drawRectangle(contentStream, 20f, mainListY, pageWidth - 40.1f, 18f, AWTColor.BLACK)
//        drawText(contentStream, "S/N", 26f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "DESCR ", 63f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "Gr.Wt.", 122f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "Net.Wt", 173f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "PCS", 224f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "Labour", 250f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "Stone Val.", 304f, gstY - 13f, 10f, AWTColor.BLACK) //329 x
//        drawText(contentStream, "Purity", 357f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "Rate", 401f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "Net Total", 476f, gstY - 13f, 10f, AWTColor.BLACK)
//
//        //ForMainList
//        val listOfSN = mutableListOf<String>()
//        for (i in 1..pdfData.descriptionList.size) {
//            listOfSN.add(i.toString())
//        }
//
//        val mainListItem = mainListY - 18f
//        val lastMainListItemY =
//            mainListY - (pdfData.descriptionList.size - 1).toFloat() * 15f - 13f - 6f
//
//        printList(contentStream, listOfSN, 26f, mainListY - 13f)
//        printList(contentStream, pdfData.descriptionList, 47f, mainListY - 13f)
//        printList(contentStream, pdfData.grWtList, 122f, mainListY - 13f)
//        printList(contentStream, pdfData.ntWtList, 173f, mainListY - 13f)
//        printList(contentStream, pdfData.pcsList, 224f, mainListY - 13f)
//        printList(contentStream, pdfData.makingChargeList, 250f, mainListY - 13f) //255 x
//        printList(contentStream, pdfData.stoneValueList, 304f, mainListY - 13f)
//        printList(contentStream, pdfData.karatList, 357f, mainListY - 13f)
//        printList(contentStream, pdfData.goldPriceList, 401f, mainListY - 13f)
//        printList(contentStream, pdfData.totalList, 476f, mainListY - 13f)
//
//        drawRectangle(
//            contentStream,
//            20f,
//            lastMainListItemY,
//            pageWidth - 40f,
//            (pdfData.descriptionList.size).toFloat() * 15f + 3.5f,
//            AWTColor.BLACK
//        )
//
//        //ForTotal
//        var totalNetValue = 0f
//        for (i in pdfData.ntWtList) {
//            totalNetValue += i.toFloat()
//        }
//        var totalPcs = 0f
//        for (i in pdfData.pcsList) {
//            totalPcs += i.toFloat()
//        }
//        var totalLabout = 0f
//        for (i in pdfData.makingChargeList) {
//            totalLabout += i.toFloat()
//        }
//        var totalStoneValue = 0f
//        for (i in pdfData.stoneValueList) {
//            totalStoneValue += i.toFloat()
//        }
//        var totalAmount = 0L
//        for (i in pdfData.totalList) {
//            totalAmount += i.toLong()
//        }
//
//        val formattedValue_0001 = String.format("%.2f", totalNetValue)
//
//        val forTotalY = lastMainListItemY - 18f
//
//        drawRectangle(contentStream, 20f, forTotalY, pageWidth - 40f, 18f, AWTColor.BLACK)
//        drawText(contentStream, "Net Total", 63f, lastMainListItemY - 13f, 10f, AWTColor.BLACK)
//        drawText(
//            contentStream,
//            formattedValue_0001,
//            173f,
//            lastMainListItemY - 13f,
//            10f,
//            AWTColor.BLACK
//        )
//        drawText(
//            contentStream,
//            totalPcs.toString(),
//            224f,
//            lastMainListItemY - 13f,
//            10f,
//            AWTColor.BLACK
//        )
//        drawText(
//            contentStream,
//            totalLabout.toString(),
//            250f,
//            lastMainListItemY - 13f,
//            10f,
//            AWTColor.BLACK
//        )
//        drawText(
//            contentStream,
//            totalStoneValue.toString(),
//            304f,
//            lastMainListItemY - 13f,
//            10f,
//            AWTColor.BLACK
//        )
//        drawText(
//            contentStream,
//            totalAmount.toString(),
//            476f,
//            lastMainListItemY - 13f,
//            10f,
//            AWTColor.BLACK
//        )
//
//        //drow box and then draw list items
//        var recycviedAmountY = forTotalY - 18f
//        val lastRAY = forTotalY - (list1212.size).toFloat() * 15f
//
//
//
//        printList(contentStream, list1212, 450f, forTotalY - 13f)
//
//        var s = ""
//        if (totalAmount1 > 0) {
//            s = ("Due $totalAmount1")
//        } else if (totalAmount1 == 0) {
//            s = ("Cleared $totalAmount1")
//        }
//
//        var receviedPayementY = lastRAY - 13f
//        drawText(contentStream, s, 450f, lastRAY - 13f, 12f, AWTColor.BLACK)
//
//
//
//        drawRectangle(
//            contentStream,
//            20f,
//            receviedPayementY - 3.5f,
//            pageWidth - 40f,
//            (list1212.size).toFloat() * 15f + 13f + 3.5f,
//            AWTColor.BLACK
//        )
//
//        val thankYouText = "Thank you for shopping with $shopName"
//        drawText(
//            contentStream,
//            thankYouText,
//            20f,
//            20.4f,
//            12f,
//            AWTColor.BLACK
//        )
//
//        drawImage(context, contentStream, document)
//
////        drawLine(contentStream, 446.25f, 40.3f, 574.97f, 40.3f, .5f, AWTColor.BLACK)
//
//        drawText(contentStream, "Signature", 487.25f, 20.3f, 12f, AWTColor.BLACK)
//
//
//
//
//
//
//        last_y_line = forTotalY.toString()
//        last_total_value = totalAmount.toString()
//
//        drawImage2(context, contentStream, document)
//
//
//        //QR code scanner
//        val qrCodeBitmap = generateUpiQrCode("9828441285@ybl")
//        insertQRCodeImage(document, page, qrCodeBitmap, 20f, 30f)
//
//        contentStream.close()
//        // Save the PDF document
//        val file = savePdfToFile(document, pdfData.fileName, context)
//        document.close()
//        return file
//
//
//    }
//
//    private fun drawImage(
//        context: Context,
//        contentStream: PDPageContentStream,
//        pdfDocument: PDDocument
//    ) {
//        val sharedPreferences =
//            context.getSharedPreferences("image_path", Context.MODE_PRIVATE)
//        val fileUrl = sharedPreferences.getString("image_path", "0")
//        Log.d("Tag", fileUrl.toString())
//        drawImageToPDF(contentStream, fileUrl!!, pdfDocument)
//
//    }
//
//
//    @Throws(IOException::class)
//    private fun drawImageToPDF(
//        contentStream: PDPageContentStream,
//        imagePath: String,
//        pdfDocument: PDDocument
//    ) {
//        try {
//            val imageFile = File(imagePath)
//            if (!imageFile.exists()) {
//                Log.e("PDF Drawing", "Image file does not exist: $imagePath")
//                return
//            }
//
//            // Load the image using PDFBox's PDImageXObject
//            val pdImage = PDImageXObject.createFromFileByContent(imageFile, pdfDocument)
//
//            // Set the position and size to draw the image
//            val x = 20.3f // Adjust the X-coordinate as needed
//            val y = 40.3f // Adjust the Y-coordinate as needed
//            val width = 75f // Adjust the width as needed
//            val height = 75f // Adjust the height as needed
//
//            // Draw the image onto the PDF
//            contentStream.drawImage(pdImage, x, y, width, height)
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Log.e("PDF Drawing", "Error while drawing image: ${e.message}")
//        }
//    }
//
//    fun printStringInCenter(
//        contentStream: PDPageContentStream,
//        string: String,
//        fontSize: Float,
//        pageWidth: Float,
//        pageHight: Float,
//        color: AWTColor
//    ) {
//        val centerX = pageWidth / 2
//        val font = font
//        val textWidth = font.getStringWidth(string) / 1000 * fontSize
//        val textX = centerX - (textWidth / 2)
//
//        drawTextForCenter(contentStream, string, textX, pageHight - 53f, fontSize, color)
//
//    }
//
//    fun calculateWidth(string: String, fontSize: Float, pageWidth: Float): Float {
//        val centerX = pageWidth / 2
//        val textWidth = font.getStringWidth(string) / 1000 * fontSize
//        val textX = centerX - (textWidth / 2)
//        return textWidth
//    }
//
//    fun drawColoredRect(
//        contentStream: PDPageContentStream,
//        x: Float,
//        y: Float,
//        width: Float,
//        height: Float,
//        backgroundColor: AWTColor
//    ) {
//        contentStream.setNonStrokingColor(backgroundColor)
//        contentStream.setLineWidth(0.5f)
//        contentStream.addRect(x, y, width, height)
//        contentStream.fill()
//    }
//
//    fun printListWithRectangle(
//        contentStream: PDPageContentStream,
//        list: MutableList<String>,
//        xPosition: Float,
//        yPosition: Float,
//        rectWidth: Float,
//        rectHeight: Float
//    ) {
//        val lineHeight = 20f // Height of each line
//        var yPosition = yPosition
//        for (text in list) {
//            // Draw a rectangle around the text
//            drawRectangle(
//                contentStream,
//                xPosition,
//                yPosition - lineHeight + 15f,
//                rectWidth,
//                rectHeight,
//                AWTColor.LIGHT_GRAY
//            )
//            // Draw the text within the rectangle
//            drawText(contentStream, text, xPosition, yPosition, 12f, AWTColor.BLACK)
//            yPosition -= lineHeight
//        }
//    }
//
//    fun printList(
//        contentStream: PDPageContentStream,
//        list: MutableList<String>,
//        xPosition: Float,
//        yPosition: Float
//    ) {
//        val lineHeight = 15f // Height of each line
//        var yPosition = yPosition// Starting y-coordinate
//        for (text in list) {
//            drawText(contentStream, text, xPosition, yPosition, 12f, AWTColor.BLACK)
//            yPosition -= lineHeight
//        }
//    }
//
//
//    fun drawRectangle(
//        contentStream: PDPageContentStream,
//        x: Float,
//        y: Float,
//        width: Float,
//        height: Float,
//        color: AWTColor
//    ) {
//        contentStream.setStrokingColor(color)
//        contentStream.setLineWidth(0.5f)
//        contentStream.addRect(x, y, width, height)
//
//        contentStream.stroke()
//    }
//
//
//    fun drawTextForCenter(
//        contentStream: PDPageContentStream,
//        text: String,
//        x: Float,
//        y: Float,
//        fontSize: Float,
//        color: AWTColor
//    ) {
//        contentStream.setFont(font, fontSize)
//        contentStream.setNonStrokingColor(color)
//        contentStream.beginText()
//        contentStream.newLineAtOffset(x, y)
//        contentStream.showText(text)
//        contentStream.endText()
//    }
//
//
//    fun drawText(
//        contentStream: PDPageContentStream,
//        text: String,
//        x: Float,
//        y: Float,
//        fontSize: Float,
//        color: AWTColor
//    ) {
//
//        contentStream.setFont(font, fontSize)
//        contentStream.setNonStrokingColor(color)
//        contentStream.beginText()
//        contentStream.newLineAtOffset(x, y)
//        contentStream.showText(text)
//        contentStream.endText()
//    }
//
//    fun drawTextForColorRect(
//        contentStream: PDPageContentStream,
//        text: String,
//        x: Float,
//        y: Float,
//        fontSize: Float,
//        color: AWTColor
//    ) {
//        contentStream.setFont(font, fontSize)
//        contentStream.setNonStrokingColor(color)
//        contentStream.setLineWidth(0.5f)
//        contentStream.beginText()
//        contentStream.newLineAtOffset(x, y)
//        contentStream.showText(text)
//        contentStream.endText()
//    }
//
//    fun drawLine(
//        contentStream: PDPageContentStream,
//        x1: Float,
//        y1: Float,
//        x2: Float,
//        y2: Float,
//        lineWidth: Float,
//        color: AWTColor
//    ) {
//        contentStream.setStrokingColor(color)
//        contentStream.setLineWidth(lineWidth)
//        contentStream.moveTo(x1, y1)
//        contentStream.lineTo(x2, y2)
//        contentStream.stroke()
//    }
//
//    fun drawTextWithBackground(
//        contentStream: PDPageContentStream,
//        text: String,
//        x: Float,
//        y: Float,
//        fontSize: Float,
//        bgColor: AWTColor
//    ) {
//        // Draw a background rectangle
//        contentStream.setNonStrokingColor(bgColor)
//        contentStream.addRect(x - 5, y - fontSize - 2, text.length * 6f, fontSize + 5)
//        contentStream.fill()
//
//        // Draw text over the background
//        contentStream.setFont(font, fontSize)
//        contentStream.setNonStrokingColor(AWTColor.BLACK)
//        contentStream.beginText()
//        contentStream.newLineAtOffset(x, y)
//        contentStream.showText(text)
//        contentStream.endText()
//    }
//
//    private fun savePdfToFile(pdfDocument: PDDocument, fileName: String, context: Context): File {
//        val cacheDirectory = context.cacheDir
//        val file = File(cacheDirectory, "$fileName.pdf")
////        val directory = File(
////            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
////            "Pdf"
////        )
////        if (!directory.exists()) {
////            directory.mkdirs()
////        }
////        val file = File(directory, "$fileName.pdf")
//        try {
//            // Get your app's private external storage directory
//            // Get your app's private external storage directory
//
//
//            val fileOutputStream = FileOutputStream(file)
//            pdfDocument.save(fileOutputStream)
//            fileOutputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Log.d("Print", "${e.message}")
//        } finally {
//            pdfDocument.close()
//
//        }
//        return file
//
//    }
//
//    private fun getSelectedColorFromSharedPreferences(context: Context): Int {
//        val sharedPreferences =
//            context.getSharedPreferences("colorPreferences", Context.MODE_PRIVATE)
//        val colorString = sharedPreferences.getString("selectedColor", "-650051840")
//        return colorString?.toInt() ?: 0
//    }
//
//    private fun convertColorIntToRgb(colorInt: Int): Triple<Int, Int, Int> {
//        val red = Color.red(colorInt)
//        val green = Color.green(colorInt)
//        val blue = Color.blue(colorInt)
//        return Triple(red, green, blue)
//    }
//
//    fun drawImage2(context: Context, contentStream: PDPageContentStream, document: PDDocument) {
//        try {
//            // Assuming the signature image is stored in the app's internal storage
//            val signaturePath = File(context.filesDir, "signature.png").absolutePath
//            val imageFile = File(signaturePath)
//
//            if (imageFile.exists()) {
//                // Load the image into the PDF
//                val image = PDImageXObject.createFromFile(imageFile.absolutePath, document)
//
//                // Define the position and size of the image
//                val x = 470.25f // X position (left)
//                val y = 35.3f   // Y position (top)
//                val width = 100f  // Width of the image
//                val height = 80f   // Height of the image
//
//                // Draw the image on the page
//                contentStream.drawImage(image, x, y, width, height)
//            } else {
//                Log.e("PdfGenerator", "Signature image not found at path: $signaturePath")
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun generateUpiQrCode(upiId: String): Bitmap {
//        // Create UPI payment string
//        val upiPaymentString =
//            "upi://pay?pa=${Uri.encode(upiId)}&cu=INR"
//
//
//        // Generate QR code
//        val writer = QRCodeWriter()
//        val bitMatrix: BitMatrix = writer.encode(
//            upiPaymentString,
//            BarcodeFormat.QR_CODE,
//            512,
//            512
//        )
//
//        // Convert to Bitmap
//        val width = bitMatrix.width
//        val height = bitMatrix.height
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//
//        for (x in 0 until width) {
//            for (y in 0 until height) {
//                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
//            }
//        }
//
//        return bitmap
//    }
//
//    private fun insertQRCodeImage(
//        document: PDDocument,
//        page: PDPage,
//        qrCodeBitmap: Bitmap,
//        x: Float,
//        y: Float
//    ) {
//        // Convert Android Bitmap to ByteArray
//        val stream = ByteArrayOutputStream()
//        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//        val imageBytes = stream.toByteArray()
//
//        // Create PDImageXObject from the byte array
//        val qrCodeImage = LosslessFactory.createFromImage(document, qrCodeBitmap)
//
//        // Create a content stream to draw the image
//        PDPageContentStream(
//            document,
//            page,
//            PDPageContentStream.AppendMode.APPEND,
//            true,
//            true
//        ).use { contentStream ->
//            // Draw the image at the specified position
//            contentStream.drawImage(
//                qrCodeImage,
//                x,  // x position
//                y,  // y position
//                100f,  // width
//                100f   // height
//            )
//        }
//    }
//
//
//}