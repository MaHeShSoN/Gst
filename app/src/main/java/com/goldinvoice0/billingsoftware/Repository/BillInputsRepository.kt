package com.goldinvoice0.billingsoftware.Repository

import androidx.lifecycle.LiveData
import com.goldinvoice0.billingsoftware.Dao.BillsInputDao
import com.goldinvoice0.billingsoftware.Model.BillInputs


class BillInputsRepository(private val billInputDao: BillsInputDao) {
    suspend fun insertBillInputs(billInput: BillInputs) {
        billInputDao.insertBillInput(billInput)
    }

    fun getAllBillInput(): LiveData<List<BillInputs>> {
        return billInputDao.getAllBillInputs()
    }

    suspend fun getBillInputById(billInputsId: Int): BillInputs? {
        return billInputDao.getBillInputsById(billInputsId)
    }

    suspend fun updateBillInput(billInputs: BillInputs) {
        billInputDao.updateBillInput(billInputs)
    }

    suspend fun deleteOrder(billInputs: BillInputs) {
        billInputDao.deleteBillInputs(billInputs)
    }

    suspend fun deleteBillInputById(billId: Int) {
        billInputDao.deleteBillById(billId)
    }
}