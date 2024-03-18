package com.example.weather.db

import com.example.weather.model.FavoritePlace
import com.example.weather.model.FavoriteWeather
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
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