package com.goldinvoice0.billingsoftware.DadatBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goldinvoice0.billingsoftware.Dao.PdfFinalDataDao
import com.goldinvoice0.billingsoftware.Model.PdfFinalData
import com.goldinvoice0.billingsoftware.convertes.IntListConverterPdf
import com.goldinvoice0.billingsoftware.convertes.StringListConverterPdf

@Database(entities = [PdfFinalData::class], version = 1)
@TypeConverters(StringListConverterPdf::class, IntListConverterPdf::class)
abstract class PdfFinalDataDatabase : RoomDatabase() {
    abstract fun PdfFinalDataDao(): PdfFinalDataDao
}