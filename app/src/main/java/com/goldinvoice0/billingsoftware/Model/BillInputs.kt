package com.goldinvoice0.billingsoftware.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.goldinvoice0.billingsoftware.convertes.ItemListConverter
import com.goldinvoice0.billingsoftware.convertes.RecivedPaymentListConverter


@Entity(tableName = "billInputs_table_")
data class BillInputs(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerNumber: String,
    val customerAddress: String,
    val date: String,
    val invoiceNumber: String,
    @TypeConverters(RecivedPaymentListConverter::class)
    val paymentList: List<PaymentRecived>,
    @TypeConverters(ItemListConverter::class)
    val itemList: List<ItemModel>,
    val totalAmount: Int,
    val receviedAmount: Int,
    val dueAmount: Int,
    val status: String
)
