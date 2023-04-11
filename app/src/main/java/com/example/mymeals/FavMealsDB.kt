package com.example.mymeals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymeals.adapters.ItemAdapter
import com.example.mymeals.database.MealItem
import com.example.mymeals.support.SpacingDeco
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FavMealsDB : AppCompatActivity() {
    private var mealArrayList = ArrayList<MealItem>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_meals_db)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.recycleViewMyMeals)

        // show all meals initially
        runBlocking {
            launch {
                val meals = Global.mealDao!!.getAll()
                for (m in meals) {
                    mealArrayList.add(m)
                }
            }
            showMeals(mealArrayList)
        }
    }

    private fun showMeals(arrayList: ArrayList<MealItem>) {
        val itemSpacingDeco = SpacingDeco(20)
        recyclerView.addItemDecoration(itemSpacingDeco)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = ItemAdapter(arrayList)
        recyclerView.adapter = adapter
    }
}
