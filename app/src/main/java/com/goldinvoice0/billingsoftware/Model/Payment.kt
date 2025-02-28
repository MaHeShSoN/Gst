package com.goldinvoice0.billingsoftware.Model


data class Payment(
    val type: PaymentMethod,
    val value: Double,
    val weight: Double = 0.0,
    val rate: Double = 0.0,
    val date: Long
)