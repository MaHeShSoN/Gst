package com.goldinvoice0.billingsoftware.Model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "shop_table")
data class Shop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var shopName: String = "",
    var shopOwner: String = "",
    var address1: String = "",
    var address2: String = "",
    var gstNumber: String = ""
)