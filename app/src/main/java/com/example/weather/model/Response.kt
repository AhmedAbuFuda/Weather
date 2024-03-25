package com.example.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentWeather")
data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherEntry>,
    @PrimaryKey
    val city: City
){
    constructor():this("",0,0, listOf(),City())
}

data class WeatherEntry(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?,
    val sys: Sys,
    val dt_txt: String
){
    constructor():this(0, Main(), listOf(), Clouds(), Wind(),0,0.0, Rain(), Sys(),"")
}

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
){
    constructor():this(0.0,0.0,0.0,0.0,0,0,0,0,0.0)
}

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
){
    constructor():this(0,"","","")
}

data class Clouds(
    val all: Int
){
    constructor():this(0)
}

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
){
    constructor():this(0.0,1,0.0)
}

data class Rain(
    val `3h`: Double
){
    constructor():this(0.0)
}

data class Sys(
    val pod: String
){
    constructor():this("")
}

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
){
    constructor():this(0,"",Coord(),"",0,0,0,0)
}

data class Coord(
    val lat: Double,
    val lon: Double
){
    constructor():this(0.0,0.0)
}

