package com.example.mymeals

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymeals.adapters.ItemAdapter
import com.example.mymeals.database.MealItem
import com.example.mymeals.support.SpacingDeco
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchFromInternetActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var retrieveButton: Button
    private lateinit var saveMealsToDBBtn: Button
    private var keyword = ""

    private lateinit var newRecyclerView: RecyclerView
    private var mealIdArrayList = ArrayList<String>()
    private var mealArrayList = ArrayList<MealItem>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("keyword", keyword)
        outState.putParcelableArrayList("mealArrayList", mealArrayList)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_from_internet)
        supportActionBar?.hide()

        if (savedInstanceState != null) {
            keyword = savedInstanceState.getString("keyword", "")
            mealArrayList = savedInstanceState.getParcelableArrayList<MealItem>("mealArrayList") as ArrayList<MealItem>
            showMeals(mealArrayList)

            searchEditText = findViewById(R.id.editTextTextSearchMain)
            searchEditText.setText(keyword)
        }

        searchEditText = findViewById(R.id.editTextTextSearchMain)
        retrieveButton = findViewById(R.id.retrieveMealsBtn)
        saveMealsToDBBtn = findViewById(R.id.saveMealsToDB)

        retrieveButton.setOnClickListener {
            //reset ArrayLists
            mealArrayList.clear()
            mealIdArrayList.clear()

            Global.buttonClickAnimation(it as Button)
            keyword = searchEditText.text.toString()

            if (keyword.isEmpty()) {
                Toast.makeText(this, "All Meals", Toast.LENGTH_SHORT).show()
                searchByName(keyword)
            } else{
                search(keyword)
            }
        }

        saveMealsToDBBtn.setOnClickListener {
            Global.buttonClickAnimation(it as Button)
            if (mealArrayList.isEmpty()) {
                Toast.makeText(this, "No meals to save", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                for (meal in mealArrayList) {
                    runBlocking {
                        launch {
                            Global.mealDao!!.insertMeal(meal)
                        }
                    }
                }
                Toast.makeText(this, "Meals saved to database", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * This function will get the keyword that contains the meal name.
     * Search result will be received from the API.
     */
    private fun searchByName(keyWord: String){
        runBlocking {
            launch {
                try {
                    withContext(Dispatchers.IO){
                        // add by meal id
                        val stringBuilder = StringBuilder()
                        val urlString = "https://www.themealdb.com/api/json/v1/1/search.php?s=${keyWord}"
                        val url = URL(urlString)
                        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                        val bf = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        var line: String? = bf.readLine()
                        while (line != null) {
                            stringBuilder.append(line + "\n")
                            line = bf.readLine()
                        }
                        parseJsonData(stringBuilder)
                    }

                }
                catch (e: Exception) {
                    Toast.makeText(this@SearchFromInternetActivity, "No meals found", Toast.LENGTH_SHORT).show()
                    Log.d("Error_SearchFromInternetActivity", e.toString())
                }
            }
        }
        showMeals(mealArrayList)
    }

    /**
     * This function will get the keyword that contains the ingredients
     * and search for the meal id that contains the ingredients.
     * Search result will be received from the API.
     * It contains ID of each meal that contains the ingredients.
     * @param keyWord: String keyword that contains the ingredients
     */
    private fun search(keyWord: String){
        try {
            val stringBuilder = StringBuilder()
            val urlString = "https://www.themealdb.com/api/json/v1/1/filter.php?i=${keyWord}"
            val url = URL(urlString)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

            runBlocking {
                launch {
                    withContext(Dispatchers.IO){
                        val bf = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        var line: String? = bf.readLine()
                        while (line != null) {
                            stringBuilder.append(line + "\n")
                            line = bf.readLine()
                        }
                        parseJsonDataMealID(stringBuilder)
                    }
                }
            }

            // retrieve meals by meal id
            retrieveMeals(mealIdArrayList)
            showMeals(mealArrayList)

        }
        catch (e: Exception) {
            Toast.makeText(this, "No meals found", Toast.LENGTH_SHORT).show()
            Log.d("Error_SearchFromInternetActivity", e.toString())
        }
    }

    /**
     * This function will get all the meal details from the API
     * which contains the meal ids that are stored in the mealIdArrayList.
     */
    private fun retrieveMeals(mealIds: ArrayList<String>){
        runBlocking {
            launch {
                for (id in mealIds) {
                    try {
                        withContext(Dispatchers.IO){
                            // add by meal id
                            val stringBuilder = StringBuilder()
                            val urlString = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=${id}"
                            val url = URL(urlString)
                            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                            val bf = BufferedReader(InputStreamReader(urlConnection.inputStream))
                            var line: String? = bf.readLine()
                            while (line != null) {
                                stringBuilder.append(line + "\n")
                                line = bf.readLine()
                            }
                            parseJsonData(stringBuilder)
                        }

                    }
                    catch (e: Exception) {
                        Log.d("Error_SearchFromInternetActivity", e.toString())
                    }
                }
            }
        }
    }

    /**
     * This function will parse the json data and store the meal details
     * into the mealArrayList.
     * @param stringBuilder: StringBuilder that contains the json data
     */
    private fun parseJsonData(stringBuilder: StringBuilder) {
        val json = JSONObject(stringBuilder.toString())

        val jsonArray = json.getJSONArray("meals")

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val meal = Global.convertJsonToMealFromAPI(jsonObject.toString())
            mealArrayList.add(meal)
        }
    }

    /**
     * This function will parse the json data and store the meal id
     * into the mealIdArrayList.
     * @param stringBuilder: StringBuilder that contains the json data
     */
    private fun parseJsonDataMealID(stringBuilder: StringBuilder) {
        val json = JSONObject(stringBuilder.toString())

        val jsonArray = json.getJSONArray("meals")

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val meal = Global.convertJsonToMealId(jsonObject.toString())
            mealIdArrayList.add(meal.toString())
        }
    }

    /**
     * This function will display the meal details in the recycler view.
     * @param mealArrayList: ArrayList<MealItem> that contains the meal details
     */
    private fun showMeals(mealArrayList: ArrayList<MealItem>){
        val itemSpacingDeco = SpacingDeco(20)
        newRecyclerView = findViewById(R.id.recycleView)
        newRecyclerView.addItemDecoration(itemSpacingDeco)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        val adapter = ItemAdapter(mealArrayList)
        newRecyclerView.adapter = adapter
    }
}
