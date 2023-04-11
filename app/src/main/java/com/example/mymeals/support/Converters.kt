/**
 * This class is used to convert the ArrayList<String> to a String and vice versa
 * This is needed because Room does not support ArrayList<String> as a type
 * This class is used in the MealItem.kt file
 */

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