package com.goldinvoice0.billingsoftware.Model

// Payment Data Class
//data class Payment(
//    val type: PaymentType,
//    val value: Double,
//    val weight: Double = 0.0,  // Used for GOLD and SILVER
//    val rate: Double = 0.0,    // Used for GOLD and SILVER
//    val date: Long
//)
data class Payment(
    val type: PaymentType,
    val value: Double,
    val weight: Double = 0.0,
    val rate: Double = 0.0,
    val date: Long
)