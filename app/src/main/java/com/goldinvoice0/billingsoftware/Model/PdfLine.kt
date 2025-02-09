package com.goldinvoice0.billingsoftware.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.TypeConverters
import com.goldinvoice0.billingsoftware.convertes.IntListConverterPdf
import com.goldinvoice0.billingsoftware.convertes.StringListConverterPdf

@Entity(tableName = "pdf_lines")
data class PdfLine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val pdfData: PdfData = PdfData(),
    @TypeConverters(StringListConverterPdf::class) val receivedList: MutableList<String> = mutableListOf(""),
    val totalAmount: Int = 0,
    @TypeConverters(IntListConverterPdf::class) val addedList: MutableList<Int> = mutableListOf(),
    @TypeConverters(IntListConverterPdf::class) val subList: MutableList<Int> = mutableListOf(),
    val fileName: String = ""
)