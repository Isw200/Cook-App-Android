package com.example.mymeals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymeals.adapters.ItemAdapter
import com.example.mymeals.database.MealItem
import com.example.mymeals.support.SpacingDeco
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SearchForMeals : AppCompatActivity() {
    lateinit var searchEditText: EditText
    lateinit var searchButton: Button
    var mealArrayList = ArrayList<MealItem>()

    private lateinit var recyclerView: RecyclerView

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("mealArrayList", mealArrayList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_meals)
        supportActionBar?.hide()

        searchEditText = findViewById(R.id.editTextTextSearchFromDB)
        searchButton = findViewById(R.id.retrieveMealsFromDBBtn)
        recyclerView = findViewById(R.id.recycleViewMyMeals)


        // show all meals initially
        val allMealsArrayList = ArrayList<MealItem>()
        runBlocking {
            launch {
                val meals = Global.mealDao!!.getAll()
                for (m in meals) {
                    allMealsArrayList.add(m)
                }
            }
            showMeals(allMealsArrayList)
        }

        if (savedInstanceState != null) {
            mealArrayList =
                savedInstanceState.getParcelableArrayList<MealItem>("mealArrayList") as ArrayList<MealItem>
            showMeals(mealArrayList)
        }

        // search for meals
        searchButton.setOnClickListener {
            Global.buttonClickAnimation(it as Button)

            var keyword = searchEditText.text.toString()
            search(keyword)
        }
    }

    /**
     * Search for meals in the database
     */
    private fun search(keyword: String) {
        mealArrayList.clear()
        runBlocking {
            launch {
                val meals = Global.mealDao!!.getMeal(keyword)
                for (m in meals) {
                    mealArrayList.add(m)
                }
            }
        }
        showMeals(mealArrayList)
    }

    /**
     * Show meals in the recycler view
     */
    private fun showMeals(arrayList: ArrayList<MealItem>) {
        val itemSpacingDeco = SpacingDeco(20)
        recyclerView.addItemDecoration(itemSpacingDeco)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = ItemAdapter(arrayList)
        recyclerView.adapter = adapter
    }
}