/**
 * This is the DAO (Data Access Object) for the MealItem class.
 * It contains the queries that can be performed on the database.
 * The queries are written in SQL.
 */
package com.example.mymeals.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealItemDao {
    @Query("SELECT * FROM MealItem")
    suspend fun getAll(): List<MealItem>

    @Query("SELECT * FROM MealItem WHERE LOWER(Meal) LIKE '%' || LOWER(:mealName) || '%' OR LOWER(Ingredients) LIKE '%' || LOWER(:mealName) || '%'")
    suspend fun getMeal(mealName: String): List<MealItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(vararg mealItem: MealItem)
}