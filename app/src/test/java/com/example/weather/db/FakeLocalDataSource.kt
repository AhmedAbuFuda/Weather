package com.example.weather.db

import com.example.weather.model.AlertWeather
import com.example.weather.model.FavoritePlace
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource(
    private var _weatherResponse: MutableList<WeatherResponse> = mutableListOf(),
    private var _favoritePlace: MutableList<FavoritePlace> = mutableListOf()
) : WeatherLocalDataSource {
    override fun getCurrentWeather(): Flow<List<WeatherResponse>> = flow {
        emit(_weatherResponse)
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        _weatherResponse.add(weatherResponse)
    }

    override suspend fun deleteCurrentWeather() {
        _weatherResponse.clear()
    }

    override fun getFavoritePlace(): Flow<List<FavoritePlace>> = flow {
        emit(_favoritePlace)
    }

    override suspend fun insertFavoritePlace(favoritePlace: FavoritePlace) {
        _favoritePlace.add(favoritePlace)
    }

    override suspend fun deleteFavoritePlace(id: Int) {
        _favoritePlace.removeIf {
            it.id == id
        }
    }

    override fun getAlertsWeather(): Flow<List<AlertWeather>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alertWeather: AlertWeather) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alertWeather: AlertWeather) {
        TODO("Not yet implemented")
    }

}