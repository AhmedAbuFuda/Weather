package com.example.weather.db

import android.content.Context
import com.example.weather.model.FavoritePlace
import com.example.weather.model.FavoriteWeather
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImp(context : Context) : WeatherLocalDataSource {

    private val dao : WeatherDao by lazy {
        val db : WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getWeather()
    }
    override fun getCurrentWeather(): Flow<List<WeatherResponse>> {
        return dao.getCurrentWeather()
    }
    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        dao.insertCurrentWeather(weatherResponse)
    }
    override suspend fun deleteCurrentWeather() {
        dao.deleteCurrentWeather()
    }

    override fun getFavoriteWeather(): Flow<List<FavoriteWeather>> {
        return dao.getFavoriteWeather()
    }

    override suspend fun insertFavoriteWeather(favoriteWeather: FavoriteWeather) {
        dao.insertFavoriteWeather(favoriteWeather)
    }

    override suspend fun deleteFavoriteWeather() {
        dao.deleteFavoriteWeather()
    }

    override fun getFavoritePlace(): Flow<List<FavoritePlace>> {
        return dao.getFavoritePlace()
    }

    override suspend fun insertFavoritePlace(favoritePlace: FavoritePlace) {
        dao.insertFavoritePlace(favoritePlace)
    }

    override suspend fun deleteFavoritePlace(id : Int) {
        dao.deleteFavoritePlace(id)
    }
}