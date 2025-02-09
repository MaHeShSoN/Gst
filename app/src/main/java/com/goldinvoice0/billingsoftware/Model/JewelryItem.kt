package com.goldinvoice0.billingsoftware.Model

data class JewelryItem(
    val type: JewelryType,
    val name: String,
    val weight: Double,
    val goldRate: Double,
    val size: Double? = null,  // Only for rings
    val notes: String? = null,
    val status: DeliveryStatus = DeliveryStatus.PENDING
)
