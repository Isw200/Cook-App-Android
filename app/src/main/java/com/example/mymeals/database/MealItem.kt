package com.example.mymeals.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mymeals.support.Converters

@Entity
data class MealItem (
    @PrimaryKey val idMeal: Int,
    val Meal: String,
    val DrinkAlternate: String?,
    val Category: String?,
    val Area: String?,
    val Instructions: String?,
    val MealThumb: String?,
    val Tags: String?,
    val Youtube: String?,
    @TypeConverters(Converters::class) val Ingredients: ArrayList<String>,
    @TypeConverters(Converters::class) val Measures: ArrayList<String>,
    val Source: String?,
    val ImageSource: String?,
    val CreativeCommonsConfirmed: String?,
    val dateModified: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idMeal)
        parcel.writeString(Meal)
        parcel.writeString(DrinkAlternate)
        parcel.writeString(Category)
        parcel.writeString(Area)
        parcel.writeString(Instructions)
        parcel.writeString(MealThumb)
        parcel.writeString(Tags)
        parcel.writeString(Youtube)
        parcel.writeString(Source)
        parcel.writeString(ImageSource)
        parcel.writeString(CreativeCommonsConfirmed)
        parcel.writeString(dateModified)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MealItem> {
        override fun createFromParcel(parcel: Parcel): MealItem {
            return MealItem(parcel)
        }

        override fun newArray(size: Int): Array<MealItem?> {
            return arrayOfNulls(size)
        }
    }
}

