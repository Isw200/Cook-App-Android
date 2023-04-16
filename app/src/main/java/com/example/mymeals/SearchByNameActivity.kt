package com.example.mymeals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymeals.adapters.ItemAdapter
import com.example.mymeals.adapters.MealNameItemAdapter
import com.example.mymeals.database.MealItem
import com.example.mymeals.support.SpacingDeco
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchByNameActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    var searchKeyword: String = ""
    var mealsArrayList: ArrayList<MealItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_name)
        supportActionBar?.hide()

        // get keyword from intent
        searchKeyword = intent.getStringExtra("keyword")!!
        search(searchKeyword)

        findViewById<EditText>(R.id.editTextSearchByName).setText(searchKeyword)
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
                        bf.close()
                        parseJsonData(stringBuilder)
                    }
                }
            }

            urlConnection.disconnect()

        } catch (e: Exception) {
            Toast.makeText(this, "No meals found with $keyword", Toast.LENGTH_SHORT).show()
        }
        showMeals(mealsArrayList)
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
            mealsArrayList.add(meal)
        }
    }

    /**
     * Show meal names in recyclerview
     * @param mealsArrayList: ArrayList<MealItem> containing meals
     */
    private fun showMeals(mealsArrayList: ArrayList<MealItem>) {
        val itemSpacingDeco = SpacingDeco(10)
        recyclerView = findViewById(R.id.searchByNameRecycleView)
        recyclerView.addItemDecoration(itemSpacingDeco)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = ItemAdapter(mealsArrayList)
        recyclerView.adapter = adapter
    }
}