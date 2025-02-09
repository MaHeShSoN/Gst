package com.goldinvoice0.billingsoftware.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "invoice_numbers")
data class InvoiceNumber(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val number: Long,
    val createdAt: Long = System.currentTimeMillis()
)