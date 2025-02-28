package com.goldinvoice0.billingsoftware.convertes

import androidx.room.TypeConverter
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.google.gson.reflect.TypeToken


class RecivedPaymentListConverter {
    @TypeConverter
    fun toItems(json: String): List<PaymentRecived> {
        // Use Gson or Moshi to convert JSON string to List<Payment>
        val gson = com.google.gson.Gson()
        val type = object : TypeToken<List<PaymentRecived>>() {}.type
        return gson.fromJson(json, type)
    }


    @TypeConverter
    fun toJson(payments: List<PaymentRecived>): String {
        val gson = com.google.gson.Gson()
        return gson.toJson(payments)
    }
}
