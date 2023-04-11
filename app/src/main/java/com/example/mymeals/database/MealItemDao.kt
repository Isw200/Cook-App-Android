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
import com.example.mymeals.database.MealItem

@Dao
interface MealItemDao {
    @Query("SELECT * FROM MealItem")
    suspend fun getAll(): List<MealItem>

    // returns the meals that have the save name and ingredient as the mealName parameter (e.g. "Chicken" and "Chicken") case insensitive and input is not complete
    @Query("SELECT * FROM MealItem WHERE LOWER(Meal) LIKE '%' || LOWER(:mealName) || '%' OR LOWER(Ingredients) LIKE '%' || LOWER(:mealName) || '%'")
    suspend fun getMeal(mealName: String): List<MealItem>

//    @Query("SELECT * FROM MealItem WHERE LOWER(Meal) LIKE '%' || LOWER(:keyword) || '%' OR LOWER(Ingredients) LIKE '%' || LOWER(:keyword) || '%'")
//    fun searchMeals(keyword: String): List<MealItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(vararg mealItem: MealItem)
}