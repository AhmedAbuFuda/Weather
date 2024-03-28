package com.example.weather

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.res.Resources
import org.intellij.lang.annotations.Language

object Constants {
    const val API_KEY = "e6c8bada64ea5356977e8966ec825784"
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val REQUEST_CODE = 10
    const val SETTING_SHARED = "Setting"
    const val LOCATION = "Location"
    const val LANGUAGE = "Language"
    const val TEMPERATURE = "Temperature"
    const val WIND_SPEED = "WindSpeed"
    const val GPS = "GPS"
    const val MAP = "Map"
    const val ENGLISH = "en"
    const val ARABIC = "ar"
    const val METER_SECOND = "meter/sec"
    const val MILE_HOUR = "mile/hour"
    const val CELSIUS = "metric"
    const val FAHRENHEIT = "imperial"
    const val KELVIN = "standard"
    const val LONGITUDE = "longitude"
    const val LATITUDE = "latitude"

    const val NOTIFICATION = "NOTIFICATION"
    const val ALARM = "ALARM"
    const val WEATHER_NOTIFICATION = "Weather Notification"

    var UNIT = " °C"
    var speed = " m/s"
}

fun getCurrentTime(dt : Long,language: String) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("EEE , d/M/yyyy", Locale(language))
    return dateFormat.format(data)
}

fun getHourTime(dt : Long,language: String) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("k a", Locale(language))
    return dateFormat.format(data)
}

fun getDay(dt : Long,language: String) : String{
    val data = Date(dt * 1000)
    val dateFormat = SimpleDateFormat("E, d/M", Locale(language))
    return dateFormat.format(data)
}

fun checkNetwork(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}

