package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.goldinvoice0.billingsoftware.DadatBase.PdfFinalDataDatabase
import com.goldinvoice0.billingsoftware.Model.PdfFinalData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PdfFinalDataViewModel(application: Application) : AndroidViewModel(application) {
    private val database: PdfFinalDataDatabase = Room.databaseBuilder(
        application,
        PdfFinalDataDatabase::class.java, "database-name"
    ).build()

    fun insertPdfFinalData(PdfFinalData: PdfFinalData) {
        viewModelScope.launch(Dispatchers.IO) {
            database.PdfFinalDataDao().insert(PdfFinalData)
        }
    }

    fun getAllPdfFinalData(callback: (List<PdfFinalData>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val PdfFinalDataList = database.PdfFinalDataDao().getAllPdfFinalDatas()
            withContext(Dispatchers.Main) {
                callback(PdfFinalDataList)
            }
        }
    }

    fun updatePdfFinalData(pdfFinalData: PdfFinalData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val PdfFinalDataList = database.PdfFinalDataDao().update(pdfFinalData)
            } catch (e: Exception) {
                // Handle the exception appropriately (e.g., log it or show an error message)
                e.printStackTrace()
            }
        }
    }

    fun deletePdfFinalData(pdfFinalData: PdfFinalData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val PdfFinalDataList = database.PdfFinalDataDao().delete(pdfFinalData)
            } catch (e: Exception) {
                // Handle the exception appropriately (e.g., log it or show an error message)
                e.printStackTrace()
            }
        }
    }


}