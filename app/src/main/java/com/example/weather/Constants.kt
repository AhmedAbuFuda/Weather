package com.example.weather

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Constants {
    const val API_KEY = "e6c8bada64ea5356977e8966ec825784"
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val REQUEST_CODE = 5005
}

fun getCurrentTime(dt : Long) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("EEE , d/M/yyyy", Locale.US)
    return dateFormat.format(data)
}

fun getHourTime(dt : Long) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("k a", Locale.US)
    return dateFormat.format(data)
}

fun getDay(dt : Long) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("E, d/M", Locale.US)
    return dateFormat.format(data)
}

