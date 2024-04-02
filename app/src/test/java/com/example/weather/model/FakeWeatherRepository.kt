package com.example.weather.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRepository(
    private var _currentWeatherResponse: MutableList<WeatherResponse> = mutableListOf(),
    private var _favoritePlace: MutableList<FavoritePlace> = mutableListOf()
) : WeatherRepository {

    private var _weatherResponse: WeatherResponse = WeatherResponse()
    override suspend fun getWeatherFromApi(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?
    ): Flow<WeatherResponse> = flow{
        emit(_weatherResponse)
    }

    override fun getCurrentWeather(): Flow<List<WeatherResponse>> = flow{
        emit(_currentWeatherResponse)
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        _currentWeatherResponse.add(weatherResponse)
    }

    override suspend fun deleteCurrentWeather() {
        _currentWeatherResponse.clear()
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

    override suspend fun getAlertById(id: String): AlertWeather {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alertWeather: AlertWeather) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alertWeather: AlertWeather) {
        TODO("Not yet implemented")
    }
}