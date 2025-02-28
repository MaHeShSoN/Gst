package com.goldinvoice0.billingsoftware.Model

data class ItemModel(
    val name: String,
    val grossWeight: Float,
    val netWeight: Float,
    val rateOfJewellery:Int,
    val stoneValue: Int,
    val finalMakingCharges: Int,
    val makingCharges: Int,
    val makingChargesType: String,
    val piece: Int,
    val totalValue: Long,
    val karat: String,
    val wastage: Float,
    var isExpanded: Boolean = false // Tracks if the item is expanded
)
