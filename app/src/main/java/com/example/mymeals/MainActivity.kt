package com.example.mymeals

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import androidx.room.Room
import com.example.mymeals.adapters.MealNameItemAdapter
import com.example.mymeals.Global.Companion.mealDao
import com.example.mymeals.database.MealDatabase
import com.example.mymeals.database.MealItem
import com.example.mymeals.support.SpacingDeco
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    var mealsArrayList: ArrayList<MealItem> = ArrayList()

    var searchKeyword: String = ""
    var isSearching: Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchKeyword", searchKeyword)
        outState.putBoolean("isSearching", isSearching)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Retrieve data after system configuration change
        if (savedInstanceState != null) {
            searchKeyword = savedInstanceState.getString("searchKeyword")!!
            isSearching = savedInstanceState.getBoolean("isSearching")

            findViewById<EditText>(R.id.editTextSearchByNameMain).text.append(searchKeyword)
            if (isSearching) {
                if (searchKeyword != "") {
                    val intent = Intent(this, SearchByNameActivity::class.java)
                    intent.putExtra("keyword", searchKeyword)
                    startActivity(intent)
                }
            }
        }

        // create database
        Global.db = Room.databaseBuilder(this, MealDatabase::class.java, "meal-db").build()
        mealDao = Global.db!!.mealItemDao()

        // get initial meals from JSON and add to database
        val inputStream = resources.openRawResource(R.raw.initial_meals)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val mealJson = jsonArray.getJSONObject(i).toString()
            val meal = Global.convertJsonToMeal(mealJson)
            runBlocking {
                launch {
                    mealDao!!.insertMeal(meal)
                }
            }
        }

        // get all meals and add to arraylist
        runBlocking {
            launch {
                val meals = mealDao!!.getAll()
                for (m in meals) {
                    mealsArrayList.add(m)
                }
            }
        }

        // All buttons and their actions in main activity
        findViewById<Button>(R.id.add_meals_to_db_btn).setOnClickListener {
            val intent = Intent(this, FavMealsDB::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.search_by_ing_btn).setOnClickListener {
            Global.buttonClickAnimation(it as Button)
            var intent = Intent(this, SearchFromInternetActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.search_meals_btn).setOnClickListener {
            Global.buttonClickAnimation(it as Button)
            var intent = Intent(this, SearchForMeals::class.java)
            startActivity(intent)
        }

        // search for Meal names
        findViewById<ImageButton>(R.id.searchByNameBtnMain).setOnClickListener {
            Global.imgButtonClickAnimation(it as ImageButton)
            isSearching = true
            val keyword = findViewById<EditText>(R.id.editTextSearchByNameMain).text.toString()
            searchKeyword = keyword
            if (keyword == "") {
                Toast.makeText(this, "Please enter a keyword", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, SearchByNameActivity::class.java)
                intent.putExtra("keyword", keyword)
                startActivity(intent)
            }
        }
    }

    /**
     * on stop, save search keyword
     */
    override fun onStop() {
        super.onStop()
        val keyword = findViewById<EditText>(R.id.editTextSearchByNameMain).text.toString()
        searchKeyword = keyword
    }
}