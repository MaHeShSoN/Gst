package com.goldinvoice0.billingsoftware.convertes

import androidx.room.TypeConverter
import com.goldinvoice0.billingsoftware.Model.Payment
import com.google.gson.reflect.TypeToken

class PaymentListConverter {
    @TypeConverter
    fun toPayments(json: String): List<Payment> {
        // Use Gson or Moshi to convert JSON string to List<Payment>
        val gson = com.google.gson.Gson()
        val type = object : TypeToken<List<Payment>>() {}.type
        return gson.fromJson(json, type)
    }


    @TypeConverter
    fun toJson(payments: List<Payment>): String {
        val gson = com.google.gson.Gson()
        return gson.toJson(payments)
    }
}