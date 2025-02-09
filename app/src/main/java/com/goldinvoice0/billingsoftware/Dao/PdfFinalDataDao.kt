package com.goldinvoice0.billingsoftware.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.goldinvoice0.billingsoftware.Model.PdfFinalData

@Dao
interface PdfFinalDataDao {
    @Insert
    suspend fun insert(PdfFinalData: PdfFinalData)

    @Query("SELECT * FROM pdf_data WHERE id = :id")
    suspend fun getPdfFinalData(id: Int): PdfFinalData

    @Query("SELECT * FROM pdf_data")
    suspend fun getAllPdfFinalDatas(): List<PdfFinalData>

    @Update
    suspend fun update(pdfFinalData: PdfFinalData)

    @Delete
    suspend fun delete(pdfFinalData: PdfFinalData)

   }