package com.example.mymeals.support

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(list: ArrayList<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(string: String): ArrayList<String> {
        return ArrayList(string.split(","))
    }
}