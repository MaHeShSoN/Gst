package com.goldinvoice0.billingsoftware.Model

import java.util.UUID


// Payment.kt
data class PaymentRecived(

    //nec
    val id: String = UUID.randomUUID().toString(),
    val amount: Int,
    var date: String? = null,
    val type: RecivedPaymentType? = null,
    val paymentMethod: PaymentMethod? = null,
    val extraChargeType: ExtraChargeType? = null,
    val goldWeight: Double? = null,
    val goldRate: Double? = null,
    val silverWeight: Double? = null,
    val silverRate: Double? = null,
    val upiService: String? = null,
    val fromUpi: String? = null,
    val toUpi: String? = null,
    val bankName: String? = null,
    val fromName: String? = null,
    val toName: String? = null,
    val checkNumber: String? = null,
) {
    fun getDetailsText(): String = when (paymentMethod) {
        PaymentMethod.GOLD_EXCHANGE -> "${goldWeight}g @ ₹${goldRate}/g"
        PaymentMethod.SILVER_EXCHANGE -> "${silverWeight}g @ ₹${silverRate}/g"
        PaymentMethod.UPI -> "$upiService: $fromUpi → $toUpi"
        PaymentMethod.BANK_TRANSFER -> "$bankName: $fromName → $toName"
        PaymentMethod.CHECK, PaymentMethod.DEMAND_DRAFT ->
            "$bankName: $fromName → $toName (${type!!.name.lowercase().capitalize()} #$checkNumber)"

        else -> type!!.name.replace("_", " ").capitalize()
    }
}