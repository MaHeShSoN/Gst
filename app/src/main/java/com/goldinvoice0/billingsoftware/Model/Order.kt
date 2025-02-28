package com.goldinvoice0.billingsoftware.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.goldinvoice0.billingsoftware.convertes.JewelryItemListConverter
import com.goldinvoice0.billingsoftware.convertes.PaymentListConverter
// Data Classes (as provided)
@Entity(tableName = "order_table_")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerNumber: String,
    val address: String,
    val deliveryDate: String,
    val deliveryStatus: DeliveryStatus,
    @TypeConverters(PaymentListConverter::class)
    val payments: List<PaymentTransaction>,
    @TypeConverters(JewelryItemListConverter::class)
    val jewelryItems: List<JewelryItem>,
    val orderNumber: String
)