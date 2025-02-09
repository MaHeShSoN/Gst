package com.goldinvoice0.billingsoftware.convertes


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IntListConverterPdf {
    @TypeConverter
    fun fromString(value: String): MutableList<Int> {
        val listType = object : TypeToken<MutableList<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: MutableList<Int>): String {
        return Gson().toJson(list)
    }
}