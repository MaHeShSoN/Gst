package com.goldinvoice0.billingsoftware.convertes

import androidx.room.TypeConverter
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken


class JewelryItemListConverter {
    @TypeConverter
    fun toJewelryItems(json: String): List<JewelryItem> {
        val gson = com.google.gson.Gson()
        val type = object : TypeToken<List<JewelryItem>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toJson(jewelryItems: List<JewelryItem>): String {
        val gson = com.google.gson.Gson()
        return gson.toJson(jewelryItems)
    }
}