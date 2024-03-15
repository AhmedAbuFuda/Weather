package com.example.weather.network

import com.example.weather.model.WeatherResponse
import retrofit2.http.Query

interface WeatherRemoteDataSource {
    suspend fun getWeatherOverNetwork(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?
    ): WeatherResponse
}