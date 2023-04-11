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
    var mealNameArrayList: ArrayList<String> = ArrayList()
    var searchKeyword: String = ""
    var isSearching: Boolean = false
    private lateinit var recyclerView: RecyclerView

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("mealNameArrayList", mealNameArrayList)
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
            mealNameArrayList = savedInstanceState.getStringArrayList("mealNameArrayList")!!
            searchKeyword = savedInstanceState.getString("searchKeyword")!!
            isSearching = savedInstanceState.getBoolean("isSearching")

            findViewById<EditText>(R.id.editTextTextSearchMain).text.append(searchKeyword)
            if (isSearching) {
                if (searchKeyword != "") {
                    search(searchKeyword)
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
        findViewById<ImageButton>(R.id.searchMain).setOnClickListener {
            Global.imgButtonClickAnimation(it as ImageButton)
            mealNameArrayList.clear()
            isSearching = true
            val keyword = findViewById<EditText>(R.id.editTextTextSearchMain).text.toString()
            searchKeyword = keyword
            if (keyword == "") {
                Toast.makeText(this, "Please enter a keyword", Toast.LENGTH_SHORT).show()
                showNames(mealNameArrayList)
                return@setOnClickListener
            } else {
                search(keyword)
            }
        }
    }

    /**
     * Search for meals from API
     * @param keyword: String keyword to search for meal names with keyword in it (case insensitive)
     */
    private fun search(keyword: String) {
        try {
            // add by meal name
            val stringBuilder = StringBuilder()
            val urlString = "https://www.themealdb.com/api/json/v1/1/search.php?s=${keyword}"
            val url = URL(urlString)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

            runBlocking {
                launch {
                    withContext(Dispatchers.IO) {
                        val bf = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        var line: String? = bf.readLine()
                        while (line != null) {
                            stringBuilder.append(line + "\n")
                            line = bf.readLine()
                        }
                        parseJsonData(stringBuilder)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "No meals found with $keyword", Toast.LENGTH_SHORT).show()
        }
        showNames(mealNameArrayList)
    }

    /**
     * Parse JSON data from API and add each mealName to mealNameArrayList
     * @param stringBuilder: StringBuilder containing JSON data
     */
    private fun parseJsonData(stringBuilder: StringBuilder) {
        val json = JSONObject(stringBuilder.toString())

        val jsonArray = json.getJSONArray("meals")

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val meal = Global.convertJsonToMealFromAPI(jsonObject.toString())
            mealNameArrayList.add(meal.Meal)
        }
    }

    /**
     * Show meal names in recyclerview
     * @param mealNameArrayList: ArrayList<String> containing meal names
     */
    private fun showNames(mealNameArrayList: ArrayList<String>) {
        val itemSpacingDeco = SpacingDeco(0)
        recyclerView = findViewById(R.id.mainAcSearchResults)
        recyclerView.addItemDecoration(itemSpacingDeco)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = MealNameItemAdapter(mealNameArrayList)
        recyclerView.adapter = adapter
    }

    /**
     * on stop, save search keyword
     */
    override fun onStop() {
        super.onStop()
        val keyword = findViewById<EditText>(R.id.editTextTextSearchMain).text.toString()
        searchKeyword = keyword
    }
}