package com.goldinvoice0.billingsoftware.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.goldinvoice0.billingsoftware.convertes.IntListConverterPdf
import com.goldinvoice0.billingsoftware.convertes.StringListConverterPdf

@Entity(tableName = "pdf_data")
data class PdfFinalData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var fileName: String = "",
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var date: String = "",
    val totalAmount: Int = 0,
    @TypeConverters(StringListConverterPdf::class) var descriptionList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var grWtList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var ntWtList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var makingChargeList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var stoneValueList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var goldPriceList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var totalList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var pcsList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) var karatList: MutableList<String> = mutableListOf(),
    @TypeConverters(StringListConverterPdf::class) val receivedList: MutableList<String> = mutableListOf(),
    @TypeConverters(IntListConverterPdf::class) val listOfWastage: MutableList<String> = mutableListOf(),
    @TypeConverters(IntListConverterPdf::class) val amountTextList: MutableList<String> = mutableListOf(),
    val invoiceNumber : String = ""
    )