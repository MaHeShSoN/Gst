package com.goldinvoice0.billingsoftware

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Color
import android.util.Log
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction
//import com.goldinvoice0.billingsoftware.Model.Order
//import com.goldinvoice0.billingsoftware.Model.OrderItem
import com.goldinvoice0.billingsoftware.Model.Shop
import com.tom_roush.harmony.awt.AWTColor
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class PdfGenerationClassOrder() {
    private lateinit var selectedAWTColor: AWTColor
    private lateinit var font: PDType0Font

    fun createPdfFromList(
        context: Context,
        order: Order,
        shop: Shop?,
        orderNumber: String,
        fileName: String
    ): File {

        val listOfPayment: List<PaymentTransaction> = order.payments
        val listOfPayments = mutableListOf<String>()
        val listOfPaymentText = mutableListOf<String>()
        val ss = listOfPayment.map {
            listOfPaymentText.add(it.type.toString())
            listOfPayments.add(it.amount.toString())
        }

        val l0 =
            "Term And Condition"
        val l1 =
            "1.Design Changes: Once the design is finalized and approved, no changes will be accepted."
        val l2 =
            "2.Weight Variations: The final weight of the jewelry may vary slightly due to the crafting process, and any adjustments will be reflected in the final billing."
        val l3 =
            "3.Delivery Timeline: Delivery is subject to availability and production timelines, and any delays will be communicated to the customer."
        val l4 =
            "4.Inspection: Customers are required to inspect the jewelry upon delivery; once accepted,no further complaints regarding design or weight will be entertained. "


        //Get the shared preferences colors
        val colorInt = getSelectedColorFromSharedPreferences(context = context)
        val rgbColor = convertColorIntToRgb(colorInt)
        val red = rgbColor.first
        val green = rgbColor.second
        val blue = rgbColor.third
        selectedAWTColor = AWTColor(red, green, blue)

        val document = PDDocument()
        val page = PDPage(PDRectangle.A4)
        document.addPage(page)
        val contentStream = PDPageContentStream(document, page)

        val assetManager: AssetManager = context.assets
        var inputStream: InputStream? = null
        inputStream = assetManager.open("notosans.ttf")

        // Load the Noto Sans font for both English and Hindi
        font = PDType0Font.load(
            document,
            inputStream
        )

        val pageWidth = page.mediaBox.width
        val pageHeight = page.mediaBox.height


        //Heading
        printStringInCenter(
            contentStream,
            "ADVANCED RECEIPT",
            24f,
            pageWidth,
            pageHeight,
            selectedAWTColor
        )

        //16dp
        val orderToText = "Order To"
        val orderFromText = "Order From"

        //14dp
        val customerName = order.customerName
        val mobilNumber = order.customerNumber
        val address = order.address
        //14f
        val shopName = shop!!.shopName
        val shopAddress = shop.address1
        val shopAddres2 = shop.address2
        val dateForPurched = "Delivery Date"
        val date = order.deliveryDate
        val gstText = "GSTIN :-"
        val gstNumber = shop.gstNumber
        val list = mutableListOf<String>(customerName, mobilNumber, address)
        val list2 = mutableListOf<String>(shopName, shopAddress, shopAddres2)

        val headingXLine = 16 + 14 * 3
        val rectY = pageHeight - 157.87f
        //upperRect
        drawRectangle(
            contentStream,
            20f,
            rectY,
            pageWidth - 40f,
            headingXLine.toFloat() + 10f,
            AWTColor.BLACK
        )
        val textY = rectY + 52.61f


        //Upper Rect Text
        drawColoredRect(contentStream, 20.3f, textY - 4f, pageWidth - 41f, 19f, selectedAWTColor)
        drawLine(contentStream, 20f, textY - 4, pageWidth - 20f, textY - 4, 1f, AWTColor.BLACK)

        //LeftSide
        drawTextForColorRect(contentStream, orderFromText, 26f, textY, 14f, AWTColor.black)
        val listY = textY - 16f
        printList(contentStream, list, 25f, listY)

        //RightSide
        drawTextForColorRect(
            contentStream,
            orderToText,
            pageWidth - 168.817f,
            textY,
            14f,
            AWTColor.BLACK
        )
        printList(contentStream, list2, pageWidth - 168.817f, listY)

        //forDate
        val dateY = rectY - 18f
        drawRectangle(contentStream, 20f, dateY, pageWidth - 40f, 18f, AWTColor.BLACK)
        drawText(contentStream, dateForPurched, 26f, rectY - 13f, 12f, AWTColor.BLACK)
        drawText(contentStream, date, pageWidth - 168.817f, rectY - 13f, 12f, AWTColor.BLACK)

        //ForGstNo and Gold Rate
        val gstY = dateY - 18f
        drawRectangle(contentStream, 20f, gstY, pageWidth - 40f, 18f, AWTColor.BLACK)
        drawText(contentStream, gstText, 26f, dateY - 13f, 12f, AWTColor.BLACK)
        drawText(contentStream, gstNumber, 80f, dateY - 13f, 12f, AWTColor.BLACK)

        drawText(
            contentStream,
            "Order No. : $orderNumber",
            pageWidth - 168.817f,
            dateY - 13f,
            12f,
            AWTColor.BLACK
        )

        //ForMainListTital
        val mainListY = gstY - 18f
        drawColoredRect(contentStream, 20.3f, mainListY, pageWidth - 41f, 18f, selectedAWTColor)


        drawRectangle(contentStream, 20f, mainListY, pageWidth - 40.1f, 18f, AWTColor.BLACK)
        drawText(contentStream, "S/N", 26f, gstY - 13f, 10f, AWTColor.BLACK)
        drawText(contentStream, "DESCR ", 63f, gstY - 13f, 10f, AWTColor.BLACK)
        drawText(contentStream, "Wt", 173f, gstY - 13f, 10f, AWTColor.BLACK)
        drawText(contentStream, "Rate", 250f, gstY - 13f, 10f, AWTColor.BLACK)
//        drawText(contentStream, "Image", 401f, gstY - 13f, 10f, AWTColor.BLACK)


        //ForMainList
        val listOfSN = mutableListOf<String>()
        for (i in 1..order.jewelryItems.size) {
            listOfSN.add(i.toString())
        }

        val mainListItem = mainListY - 18f
        val lastMainListItemY =
            mainListY - (order.jewelryItems.size - 1).toFloat() * 15f - 13f - 6f

        printListHorizontal(
            order.jewelryItems.toMutableList(),
            contentStream,
            mainListY - 13f,
            document
        )

        drawRectangle(
            contentStream,
            20f,
            lastMainListItemY,
            pageWidth - 40f,
            (order.jewelryItems.size).toFloat() * 15f + 3.5f,
            AWTColor.BLACK
        )


        // Draw the advancePayment and advanceGold text with aligned width
        val advanceTextX = 63f
        val advanceTextY = lastMainListItemY - 15f

        printList(
            contentStream,
            listOfPaymentText,
            advanceTextX,
            advanceTextY,
        )
        printList(
            contentStream,
            listOfPayments,
            advanceTextX+80f,
            advanceTextY,
        )
//        drawText(
//            contentStream,
//            "Advance Payment: $advancePayment",
//            advanceTextX,
//            advanceTextY - 15f,
//            12f,
//            AWTColor.BLACK
//        )

        // Draw rectangle enclosing advancePayment and advanceGold text with aligned width
        val advanceRectY = advanceTextY - 18.9f
        drawRectangle(contentStream, 20f, advanceRectY, pageWidth - 40f, 30f + 3.5f, AWTColor.BLACK)

        //draw the lines from l1 to l7

        val listOfLines = listOf(l1, l2, l3, l4)

        drawLines(
            contentStream,
            pageWidth,
            advanceRectY,
            pageHeight,
            listOfLines,
            l0
        )

//        drawRectangle(
//            contentStream,
//            termsX - 10f,
//            termsY - termsHeight - 10f,
//            pageWidth - 80f,
//            termsHeight + 20f,
//            AWTColor.BLACK
//        )


        contentStream.close()
        // Save the PDF document
        val file = savePdfToFile(document, fileName, context)
        document.close()
        return file

    }














    private fun drawLines(
        contentStream: PDPageContentStream,
        pageWidth: Float,
        advanceRectY: Float,
        pageHeight: Float,
        lines: List<String>,
        l0: String
    ) {
        val termsX = 30f
        val bottomMargin = 50f
        val fontSize = 6f
        val titleFontSize = 8f
        val lineSpacing = 10f  // Reduced spacing for smaller text

        // Calculate rectangle dimensions based on content
        val numberOfLines = lines.size + 1  // +1 for title
        val rectHeight =
            (numberOfLines * lineSpacing) + 20f  // Adjusted height for content + padding
        val rectBottom = bottomMargin + 10f
        val rectTop = rectBottom + rectHeight

        // Draw rectangle around terms and conditions
        drawRectangle(
            contentStream,
            20f,
            rectBottom,
            pageWidth - 40f,
            rectHeight,
            AWTColor.BLACK
        )

        // Calculate text positions based on rectangle position
        val startY = rectTop - 12f  // Adjusted for smaller text

        // Draw the title (l0)
        drawText(
            contentStream,
            l0,
            termsX,
            startY,
            titleFontSize,
            AWTColor.BLACK
        )

        // Draw all terms and conditions lines
        var currentY = startY - lineSpacing - 4f  // Adjusted spacing after title

        for (line in lines) {
            drawText(
                contentStream,
                line,
                termsX,
                currentY,
                fontSize,
                AWTColor.BLACK
            )
            currentY -= lineSpacing
        }
    }


    fun printListHorizontal(
        itemList: MutableList<JewelryItem>,
        contentStream: PDPageContentStream,
        yStart: Float,
        document: PDDocument
    ) {
        var yPosition = yStart
        val lineHeight = 15f // Space for text rows
        val pageHeight = PDRectangle.A4.height
        val marginBottom = 50f
        var serialNumber = 1 // Start S/N at 1

        for (orderItem in itemList) {
            // Check if yPosition is too low, create a new page if needed
            if (yPosition - lineHeight < marginBottom) {
                contentStream.close() // Close current content stream
                val newPage = PDPage(PDRectangle.A4)
                document.addPage(newPage)
                val newContentStream = PDPageContentStream(document, newPage)
                yPosition = pageHeight - 50f // Reset yPosition for the new page
            }

            // Draw S/N
            drawText(contentStream, serialNumber.toString(), 26f, yPosition, 12f, AWTColor.BLACK)

            // Draw item details
            drawText(contentStream, orderItem.name, 63f, yPosition, 12f, AWTColor.BLACK)
            drawText(
                contentStream,
                orderItem.weight.toString(),
                173f,
                yPosition,
                12f,
                AWTColor.BLACK
            )
            drawText(
                contentStream,
                orderItem.goldRate.toString(),
                250f,
                yPosition,
                12f,
                AWTColor.BLACK
            )

            // Increment S/N and adjust for spacing after each item
            serialNumber++
            yPosition -= lineHeight
        }
    }


    private fun savePdfToFile(pdfDocument: PDDocument, fileName: String, context: Context): File {
        val cacheDirectory = context.cacheDir
        val file = File(cacheDirectory, "$fileName.pdf")
//        val directory = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
//            "Pdf"
//        )
//        if (!directory.exists()) {
//            directory.mkdirs()
//        }
//        val file = File(directory, "$fileName.pdf")
        try {
            // Get your app's private external storage directory
            // Get your app's private external storage directory


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
//
//    private fun drawImageFromUriString(
//        contentStream: PDPageContentStream,
//        imageUri: String,
//        yPosition: Float,
//        context: Context,
//        document: PDDocument
//    ): Float {
//        try {
//            val uri = Uri.parse(imageUri)
//            val inputStream = context.contentResolver.openInputStream(uri)
//                ?: throw Exception("Unable to open input stream for URI: $imageUri")
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            inputStream.close()
//
//            val pdImage = LosslessFactory.createFromImage(document, bitmap)
//            val imageWidth = 25f
//            val imageHeight = pdImage.height * (imageWidth / pdImage.width)
//            val adjustedYPosition = yPosition - imageHeight
//
//            // Check for page bounds
//            if (adjustedYPosition < 50f) {
//                throw Exception("Image cannot fit on the current page")
//            }
//
//            contentStream.drawImage(pdImage, 401f, adjustedYPosition, imageWidth, imageHeight)
//            return adjustedYPosition - 10f // Adjust for spacing
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(context, "Failed to draw image: ${e.message}", Toast.LENGTH_SHORT).show()
//            return yPosition
//        }
//    }


    fun drawColoredRect(
        contentStream: PDPageContentStream,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        backgroundColor: AWTColor
    ) {
        contentStream.setNonStrokingColor(backgroundColor)
        contentStream.setLineWidth(0.5f)
        contentStream.addRect(x, y, width, height)
        contentStream.fill()
    }

    fun drawTextForColorRect(
        contentStream: PDPageContentStream,
        text: String,
        x: Float,
        y: Float,
        fontSize: Float,
        color: AWTColor
    ) {
        contentStream.setFont(font, fontSize)
        contentStream.setNonStrokingColor(color)
        contentStream.setLineWidth(0.5f)
        contentStream.beginText()
        contentStream.newLineAtOffset(x, y)
        contentStream.showText(text)
        contentStream.endText()
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

    fun printList(
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

    private fun getSelectedColorFromSharedPreferences(context: Context): Int {
        val sharedPreferences =
            context.getSharedPreferences("colorPreferences", Context.MODE_PRIVATE)
        val colorString = sharedPreferences.getString("selectedColor", "-1")
        return colorString?.toInt() ?: 0
    }

    private fun convertColorIntToRgb(colorInt: Int): Triple<Int, Int, Int> {
        val red = Color.red(colorInt)
        val green = Color.green(colorInt)
        val blue = Color.blue(colorInt)
        return Triple(red, green, blue)
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
        val font = font
        val textWidth = font.getStringWidth(string) / 1000 * fontSize
        val textX = centerX - (textWidth / 2)

        drawTextForCenter(contentStream, string, textX, pageHight - 53f, fontSize, color)

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
}