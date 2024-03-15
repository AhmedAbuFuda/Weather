package com.example.weather.network

import com.example.weather.Constants
import com.example.weather.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast?")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String?,
        @Query("lang") lang: String?,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): WeatherResponse
}