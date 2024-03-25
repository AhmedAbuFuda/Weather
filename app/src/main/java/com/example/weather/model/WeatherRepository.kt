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


    fun getFavoritePlace() : Flow<List<FavoritePlace>>
    suspend fun insertFavoritePlace(favoritePlace : FavoritePlace)
    suspend fun deleteFavoritePlace(id : Int)

    fun getAlertsWeather() : Flow<List<AlertWeather>>
    suspend fun insertAlert(alertWeather: AlertWeather)
    suspend fun deleteAlert(alertWeather: AlertWeather)
}