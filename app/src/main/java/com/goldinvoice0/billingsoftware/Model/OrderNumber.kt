package com.goldinvoice0.billingsoftware.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_numbers")
data class OrderNumber(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val number: Long,
    val createdAt: Long = System.currentTimeMillis()
)