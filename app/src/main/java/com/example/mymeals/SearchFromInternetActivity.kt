package com.example.mymeals

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
            mealArrayList =
                savedInstanceState.getParcelableArrayList<MealItem>("mealArrayList") as ArrayList<MealItem>
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

            // Get the input method manager
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            val view = currentFocus ?: View(this)

            imm.hideSoftInputFromWindow(view.windowToken, 0)


            Global.buttonClickAnimation(it as Button)
            keyword = searchEditText.text.toString()

            if (keyword.isEmpty()) {
                Toast.makeText(this, "Enter an Ingredient", Toast.LENGTH_SHORT).show()
            } else {
                searchByIng(keyword)
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
     * This function will get the keyword that contains the ingredients
     * and search for the meal id that contains the ingredients.
     * Search result will be received from the API.
     * It contains ID of each meal that contains the ingredients.
     * @param keyWord: String keyword that contains the ingredients
     */
    private fun searchByIng(keyWord: String) {
        try {
            val urlString = "https://www.themealdb.com/api/json/v1/1/filter.php?i=${keyWord}"

            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    URL(urlString).readText()
                }
                parseJsonDataMealID(result)

                // retrieve meals by meal id
                retrieveMeals(mealIdArrayList)

                withContext(Dispatchers.Main) {
                    showMeals(mealArrayList)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "No meals found", Toast.LENGTH_SHORT).show()
            Log.d("Error_SearchFromInternetActivity", e.toString())
        }
    }


    /**
     * This function will get all the meal details from the API
     * which contains the meal ids that are stored in the mealIdArrayList.
     */
    private fun retrieveMeals(mealIds: ArrayList<String>) {
        runBlocking {
            launch {
                for (id in mealIds) {
                    try {
                        val urlString =
                            "https://www.themealdb.com/api/json/v1/1/lookup.php?i=${id}"

                        val result = withContext(Dispatchers.IO) {
                            URL(urlString).readText()
                        }
                        parseJsonDataMeal(result)

                    } catch (e: Exception) {
                        Log.d("Error_SearchFromInternetActivity", e.toString())
                    }
                }
            }
        }
    }

    /**
     * This function will parse the json data and store the meal details
     * into the mealArrayList.
     * @param jsonString: String that contains the json data
     */
    private fun parseJsonDataMeal(jsonString: String) {
        val json = JSONObject(jsonString)

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
     * @param jsonString: String that contains the json data
     */
    private fun parseJsonDataMealID(jsonString: String) {
        val jsonObj = JSONObject(jsonString)
        val mealsArray = jsonObj.getJSONArray("meals")

        for (i in 0 until mealsArray.length()) {
            val mealObj = mealsArray.getJSONObject(i)
            val mealId = mealObj.getString("idMeal")
            mealIdArrayList.add(mealId)
        }
    }

    /**
     * This function will display the meal details in the recycler view.
     * @param mealArrayList: ArrayList<MealItem> that contains the meal details
     */
    private fun showMeals(mealArrayList: ArrayList<MealItem>) {
        val itemSpacingDeco = SpacingDeco(20)
        newRecyclerView = findViewById(R.id.recycleView)
        newRecyclerView.addItemDecoration(itemSpacingDeco)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        val adapter = ItemAdapter(mealArrayList)
        newRecyclerView.adapter = adapter
    }
}
