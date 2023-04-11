package com.example.mymeals.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymeals.R

class MealNameItemAdapter(private val meals : ArrayList<String>) : RecyclerView.Adapter<MealNameItemAdapter.ItemViewHolderName>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolderName {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.name_item, parent, false)
        return ItemViewHolderName(itemView)
    }

    override fun getItemCount(): Int {
        return meals.size
    }

    override fun onBindViewHolder(holder: ItemViewHolderName, position: Int) {
        val currentItem = meals[position]
        holder.mealName.text = currentItem
    }

    class ItemViewHolderName(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealNameView)
    }
}