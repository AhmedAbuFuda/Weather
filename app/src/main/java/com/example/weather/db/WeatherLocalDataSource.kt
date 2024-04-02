package com.example.weather.db

import com.example.weather.model.AlertWeather
import com.example.weather.model.FavoritePlace
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getCurrentWeather() : Flow<List<WeatherResponse>>
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun deleteCurrentWeather()

    fun getFavoritePlace() : Flow<List<FavoritePlace>>
    suspend fun insertFavoritePlace(favoritePlace : FavoritePlace)
    suspend fun deleteFavoritePlace(id : Int)

    fun getAlertsWeather() : Flow<List<AlertWeather>>
    suspend fun getAlertById(id : String) : AlertWeather
    suspend fun insertAlert(alertWeather: AlertWeather)
    suspend fun deleteAlert(alertWeather: AlertWeather)

}