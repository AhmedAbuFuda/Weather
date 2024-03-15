package com.example.weather.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherFromApi(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?)
    : WeatherResponse
}