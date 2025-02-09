package com.goldinvoice0.billingsoftware.Model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "customer_table")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val number: String = ""
)
