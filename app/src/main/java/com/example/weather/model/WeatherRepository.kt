package com.example.weather.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherFromApi(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?)
    : Flow<WeatherResponse>

    fun getCurrentWeather() : Flow<List<WeatherResponse>>
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun deleteCurrentWeather()

    fun getFavoriteWeather() : Flow<List<FavoriteWeather>>
    suspend fun insertFavoriteWeather(favoriteWeather : FavoriteWeather)
    suspend fun deleteFavoriteWeather()

    fun getFavoritePlace() : Flow<List<FavoritePlace>>
    suspend fun insertFavoritePlace(favoritePlace : FavoritePlace)
    suspend fun deleteFavoritePlace(id : Int)
}