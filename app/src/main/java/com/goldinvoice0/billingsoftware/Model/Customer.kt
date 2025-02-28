package com.goldinvoice0.billingsoftware.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "customer_table")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val address2: String = "",
    val number: String = "",
    val customerId: String = "",
    val imageUrl: String = ""
) : Parcelable