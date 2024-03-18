package com.example.weather.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favoriteWeather",indices = [Index(value = ["city"], unique = true)])
data class FavoriteWeather(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherEntry>,
    val city: City
){
constructor(): this(0,"",0,0, listOf(),City())
}

@Entity(tableName = "favoritePlace")
data class FavoritePlace(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val latitude : Double,
    val longitude : Double,
    val address : String
)
