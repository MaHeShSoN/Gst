package com.goldinvoice0.billingsoftware.convertes

import androidx.room.TypeConverter
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.google.gson.reflect.TypeToken

class ItemListConverter {
    @TypeConverter
    fun toItems(json: String): List<ItemModel> {
        // Use Gson or Moshi to convert JSON string to List<Payment>
        val gson = com.google.gson.Gson()
        val type = object : TypeToken<List<ItemModel>>() {}.type
        return gson.fromJson(json, type)
    }


    @TypeConverter
    fun toJson(items: List<ItemModel>): String {
        val gson = com.google.gson.Gson()
        return gson.toJson(items)
    }
}