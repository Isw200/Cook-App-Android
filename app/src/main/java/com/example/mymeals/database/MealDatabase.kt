package com.example.mymeals.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mymeals.support.Converters

@Database(entities = [MealItem::class], version = 2)
@TypeConverters(Converters::class)

abstract class MealDatabase: RoomDatabase() {
    abstract fun mealItemDao(): MealItemDao
}