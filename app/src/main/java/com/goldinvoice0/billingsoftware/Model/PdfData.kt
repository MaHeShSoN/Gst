package com.goldinvoice0.billingsoftware.Model



import androidx.room.TypeConverters
import com.goldinvoice0.billingsoftware.convertes.StringListConverterPdf

data class PdfData(
    var fileName: String = "",
    var name: String = "",
    var phone: String = "",
    var address: String = "",
    var date: String = "",
    @TypeConverters(StringListConverterPdf::class) var descriptionList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var grWtList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var ntWtList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var makingChargeList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var stoneValueList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var goldPriceList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var totalList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var pcsList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var karatList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var listOfWastage: MutableList<String> = mutableListOf(),
    var invoiceNumber: String = ""
)

