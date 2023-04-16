/**
 * This class is used to store global variables
 * that are used throughout the app
 */

package com.example.mymeals

import android.widget.Button
import android.widget.ImageButton
import com.example.mymeals.database.MealDatabase
import com.example.mymeals.database.MealItem
import com.example.mymeals.database.MealItemDao
import org.json.JSONObject

class Global {
    companion object {
        var db: MealDatabase? = null
        var mealDao : MealItemDao? = null

        /**
         * This function is used to convert a json string to a MealItem object
         * specifically for when the json string is from the local database
         * @param json the json string to be converted
         */

        fun convertJsonToMeal(json: String): MealItem {
            val jsonObject = JSONObject(json)

            val ingredients = ArrayList<String>()
            val measures = ArrayList<String>()

            for (i in 1..20) {
                val ingredientKey = "Ingredient$i"
                val measureKey = "Measure$i"

                val ingredient = jsonObject.optString(ingredientKey, null.toString())
                val measure = jsonObject.optString(measureKey, null.toString())

                if (ingredient.isNotBlank() && measure.isNotBlank()) {
                    val ingredientNoComma = ingredient.replace(",", "-")
                    ingredients.add(ingredientNoComma)
                    measures.add(measure)
                }
            }

            val idMeal = jsonObject.getInt("idMeal")
            val mealName = jsonObject.getString("Meal")
            val drinkAlternateElement = jsonObject.opt("DrinkAlternate")
            val drinkAlternate = if (drinkAlternateElement == null || drinkAlternateElement == JSONObject.NULL) "" else drinkAlternateElement.toString()
            val category = jsonObject.getString("Category")
            val area = jsonObject.getString("Area")
            val instructions = jsonObject.getString("Instructions")
            val mealThumbElement = jsonObject.opt("MealThumb")
            val mealThumb = if (mealThumbElement == null || mealThumbElement == JSONObject.NULL) "null" else mealThumbElement.toString()
            val tagsElement = jsonObject.opt("Tags")
            val tags = if (tagsElement == null || tagsElement == JSONObject.NULL) "null" else tagsElement.toString()
            val youtubeElement = jsonObject.opt("Youtube")
            val youtube = if (youtubeElement == null || youtubeElement == JSONObject.NULL) "null" else youtubeElement.toString()
            val sourceElement = jsonObject.opt("Source")
            val source = if (sourceElement == null || sourceElement == JSONObject.NULL) "null" else sourceElement.toString()
            val imageSourceElement = jsonObject.opt("ImageSource")
            val imageSource = if (imageSourceElement == null || imageSourceElement == JSONObject.NULL) "null" else imageSourceElement.toString()
            val creativeCommonsConfirmedElement = jsonObject.opt("CreativeCommonsConfirmed")
            val creativeCommonsConfirmed = if (creativeCommonsConfirmedElement == null || creativeCommonsConfirmedElement == JSONObject.NULL) "null" else creativeCommonsConfirmedElement.toString()
            val dateModifiedElement = jsonObject.opt("dateModified")
            val dateModified = if (dateModifiedElement == null || dateModifiedElement == JSONObject.NULL) "null" else dateModifiedElement.toString()

            return MealItem(
                idMeal,
                mealName,
                drinkAlternate,
                category,
                area,
                instructions,
                mealThumb,
                tags,
                youtube,
                ingredients,
                measures,
                source,
                imageSource,
                creativeCommonsConfirmed,
                dateModified
            )
        }


        /**
         * This function is used to convert a json string to a MealItem object
         * specifically for when the json string is from the API
         * @param json the json string to be converted
         */
        fun convertJsonToMealFromAPI(json: String): MealItem {
            val jsonObject = JSONObject(json)

            val ingredients = ArrayList<String>()
            val measures = ArrayList<String>()

            for (i in 1..20) {
                val ingredientKey = "strIngredient$i"
                val measureKey = "strMeasure$i"

                val ingredient = jsonObject.optString(ingredientKey, "")
                val measure = jsonObject.optString(measureKey, "")

                if (ingredient.isNotEmpty() && measure.isNotEmpty()) {
                    // remove commas from ingredients
                    val ingredientNoComma = ingredient.replace(",", "-")
                    ingredients.add(ingredientNoComma)
                    measures.add(measure)
                }
            }

            val idMeal = jsonObject.getInt("idMeal")
            val mealName = jsonObject.getString("strMeal")
            val drinkAlternate = jsonObject.optString("strDrinkAlternate", "")
            val category = jsonObject.getString("strCategory")
            val area = jsonObject.getString("strArea")
            val instructions = jsonObject.getString("strInstructions")
            val mealThumb = jsonObject.optString("strMealThumb", "null")
            val tags = jsonObject.optString("strTags", "null")
            val youtube = jsonObject.optString("strYoutube", "null")
            val source = jsonObject.optString("strSource", "null")
            val imageSource = jsonObject.optString("strImageSource", "null")
            val creativeCommonsConfirmed = jsonObject.optString("strCreativeCommonsConfirmed", "null")
            val dateModified = jsonObject.optString("dateModified", "null")

            return MealItem(
                idMeal,
                mealName,
                drinkAlternate,
                category,
                area,
                instructions,
                mealThumb,
                tags,
                youtube,
                ingredients,
                measures,
                source,
                imageSource,
                creativeCommonsConfirmed,
                dateModified
            )
        }


        /**
         * This function perform a button click animation
         */
        fun buttonClickAnimation(button: Button) {
            button.animate().apply {
                scaleX(0.9f)
                scaleY(0.9f)
                duration = 100
                withEndAction {
                    button.animate().apply {
                        scaleX(1f)
                        scaleY(1f)
                        duration = 100
                    }.start()
                }
            }.start()
        }

        /**
         * This function perform a button click animation for image buttons
         */
        fun imgButtonClickAnimation(button: ImageButton) {
            button.animate().apply {
                scaleX(0.9f)
                scaleY(0.9f)
                duration = 100
                withEndAction {
                    button.animate().apply {
                        scaleX(1f)
                        scaleY(1f)
                        duration = 100
                    }.start()
                }
            }.start()
        }
    }
}