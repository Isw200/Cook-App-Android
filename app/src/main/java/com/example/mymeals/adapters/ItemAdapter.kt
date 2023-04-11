package com.example.mymeals.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymeals.database.MealItem
import com.example.mymeals.R
import com.squareup.picasso.Picasso


class ItemAdapter(private val meals : ArrayList<MealItem>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return meals.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = meals[position]

        //set image
        Picasso.get()
            .load(currentItem.MealThumb)
            .into(holder.mealImage)

        holder.mealName.text = currentItem.Meal
        holder.category.text = "Category: ${currentItem.Category}"
        holder.area.text = "Area: ${currentItem.Area}"
        if (currentItem.DrinkAlternate == null || currentItem.DrinkAlternate == "") {
            holder.drinkAlternate.text = "Drink Alternate: None"
        } else {
            holder.drinkAlternate.text = "Drink Alternate: ${currentItem.DrinkAlternate}"
        }
        if (currentItem.Tags == null || currentItem.Tags == "") {
            holder.tags.text = "Tags: None"
        } else {
            holder.tags.text = "Tags: ${currentItem.Tags}"
        }

        // recycle view scrolling
        holder.instructions.text = currentItem.Instructions

        //set ingredients
        var ingredients = ""
        var measures = ""
        for (i in 0 until currentItem.Ingredients.size) {
            ingredients += "${currentItem.Ingredients[i]}\n"
            if (currentItem.Measures[i] == ""){
                measures += "N/A"
            }
            else {
                measures += "${currentItem.Measures[i]}\n"
            }
        }
        holder.ingredients.text = ingredients
        holder.measures.text = measures

        holder.youtubeLink.text = "Youtube"
        holder.sourceLink.text = "Source"

        //set links to buttons
        holder.youtubeLink.setOnClickListener {
            val url = currentItem.Youtube
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            holder.itemView.context.startActivity(intent)
        }
        holder.sourceLink.setOnClickListener {
            val url = currentItem.Source
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            holder.itemView.context.startActivity(intent)
        }

        if (currentItem.dateModified == null || currentItem.dateModified == "") {
            holder.dateModified.text = "Updated"
        } else {
            holder.dateModified.text = "Date: ${currentItem.dateModified}"
        }
    }

    class ItemViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        val mealImage: ImageView = view.findViewById<ImageView>(R.id.mealImage)
        val mealName: TextView = view.findViewById<TextView>(R.id.mealName)
        val category: TextView = view.findViewById<TextView>(R.id.category)
        val area: TextView = view.findViewById<TextView>(R.id.area)
        val drinkAlternate: TextView = view.findViewById<TextView>(R.id.drinkAlternate)
        val tags: TextView = view.findViewById<TextView>(R.id.tags)
        val instructions: TextView = view.findViewById<TextView>(R.id.instructions)
        val ingredients: TextView = view.findViewById<TextView>(R.id.ingredientTextView)
        val measures: TextView = view.findViewById<TextView>(R.id.measureTextView)
        val youtubeLink: Button = view.findViewById<Button>(R.id.youtube)
        val sourceLink: Button = view.findViewById<Button>(R.id.source)
        val dateModified: TextView = view.findViewById<TextView>(R.id.dateModeified)
    }
}