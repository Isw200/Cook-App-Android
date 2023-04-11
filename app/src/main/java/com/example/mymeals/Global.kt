package com.example.mymeals

import android.widget.Button
import android.widget.ImageButton
import com.example.mymeals.database.MealDatabase
import com.example.mymeals.database.MealItem
import com.example.mymeals.database.MealItemDao
import com.google.gson.Gson
import com.google.gson.JsonObject

class Global {
    companion object {
        var db: MealDatabase? = null
        var mealDao : MealItemDao? = null

        fun convertJsonToMeal(json: String): MealItem {
            val gson = Gson()
            val jsonObject = gson.fromJson(json, JsonObject::class.java)

            val ingredients = ArrayList<String>()
            val measures = ArrayList<String>()

            for (i in 1..20) {
                val ingredientKey = "Ingredient$i"
                val measureKey = "Measure$i"

                var ingredient = jsonObject[ingredientKey]?.asString
                val measure = jsonObject[measureKey]?.asString

                if (ingredient != null && measure != null && ingredient.isNotBlank() && measure.isNotBlank()) {
                    val ingredientNoComma = ingredient.replace(",", "-")
                    ingredients.add(ingredientNoComma)
                    measures.add(measure)
                }
            }

            val idMeal = jsonObject["idMeal"].asInt
            val mealName = jsonObject["Meal"].asString
            val drinkAlternateElement = jsonObject.get("DrinkAlternate")
            val drinkAlternate =
                if (drinkAlternateElement.isJsonNull) "" else drinkAlternateElement.asString

            val category = jsonObject["Category"].asString
            val area = jsonObject["Area"].asString
            val instructions = jsonObject["Instructions"].asString
            val mealThumbElement = jsonObject.get("MealThumb")
            val mealThumb =
                if (mealThumbElement.isJsonNull || mealThumbElement == null) "null" else mealThumbElement.asString
            val tagsElement = jsonObject.get("Tags")
            val tags =
                if (tagsElement.isJsonNull || tagsElement == null) "null" else tagsElement.asString
            val youtubeElement = jsonObject.get("Youtube")
            val youtube =
                if (youtubeElement.isJsonNull || youtubeElement == null) "null" else youtubeElement.asString
            val sourceElement = jsonObject.get("Source")
            val source = if (sourceElement == null) "null" else sourceElement.asString
            val imageSourceElement = jsonObject.get("ImageSource")
            val imageSource =
                if (imageSourceElement == null || imageSourceElement.isJsonNull) "null" else imageSourceElement.asString
            val creativeCommonsConfirmedElement = jsonObject.get("CreativeCommonsConfirmed")
            val creativeCommonsConfirmed =
                if (creativeCommonsConfirmedElement == null || creativeCommonsConfirmedElement.isJsonNull) "null" else creativeCommonsConfirmedElement.asString
            val dateModifiedElement = jsonObject.get("dateModified")
            val dateModified =
                if (dateModifiedElement == null || dateModifiedElement.isJsonNull) "null" else dateModifiedElement.asString


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

        fun convertJsonToMealId(json: String): Int {
            val gson = Gson()
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            return jsonObject["idMeal"].asInt
        }

        fun convertJsonToMealFromAPI(json: String): MealItem {
            val gson = Gson()
            val jsonObject = gson.fromJson(json, JsonObject::class.java)

            val ingredients = ArrayList<String>()
            val measures = ArrayList<String>()

            for (i in 1..20) {
                val ingredientKey = "strIngredient$i"
                val measureKey = "strMeasure$i"

                val ingredientElem = jsonObject.get(ingredientKey)
                var ingredient = if (ingredientElem.isJsonNull || ingredientElem == null) "" else ingredientElem.asString

                val measureElem = jsonObject.get(measureKey)
                val measure = if (measureElem.isJsonNull || measureElem == null) "" else measureElem.asString

                if (ingredient != null && measure != null && ingredient.isNotBlank() && measure.isNotBlank()) {
                    // remove commas from ingredients
                    val ingredientNoComma = ingredient.replace(",", "-")
                    ingredients.add(ingredientNoComma)
                    measures.add(measure)
                }
            }

            val idMeal = jsonObject["idMeal"].asInt
            val mealName = jsonObject["strMeal"].asString
            val drinkAlternateElement = jsonObject.get("strDrinkAlternate")
            val drinkAlternate =
                if (drinkAlternateElement.isJsonNull) "" else drinkAlternateElement.asString

            val category = jsonObject["strCategory"].asString
            val area = jsonObject["strArea"].asString
            val instructions = jsonObject["strInstructions"].asString
            val mealThumbElement = jsonObject.get("strMealThumb")
            val mealThumb =
                if (mealThumbElement.isJsonNull || mealThumbElement == null) "null" else mealThumbElement.asString
            val tagsElement = jsonObject.get("strTags")
            val tags =
                if (tagsElement.isJsonNull || tagsElement == null) "null" else tagsElement.asString
            val youtubeElement = jsonObject.get("strYoutube")
            val youtube =
                if (youtubeElement.isJsonNull || youtubeElement == null) "null" else youtubeElement.asString
            val sourceElement = jsonObject.get("strSource")
            val source =
                if (sourceElement.isJsonNull || sourceElement == null) "null" else sourceElement.asString
            val imageSourceElement = jsonObject.get("strImageSource")
            val imageSource =
                if (imageSourceElement == null || imageSourceElement.isJsonNull) "null" else imageSourceElement.asString
            val creativeCommonsConfirmedElement = jsonObject.get("strCreativeCommonsConfirmed")
            val creativeCommonsConfirmed =
                if (creativeCommonsConfirmedElement == null || creativeCommonsConfirmedElement.isJsonNull) "null" else creativeCommonsConfirmedElement.asString
            val dateModifiedElement = jsonObject.get("dateModified")
            val dateModified =
                if (dateModifiedElement == null || dateModifiedElement.isJsonNull) "null" else dateModifiedElement.asString


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