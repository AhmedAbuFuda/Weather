package com.example.weather.model

import com.example.weather.db.WeatherLocalDataSource
import com.example.weather.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRepositoryImp private constructor(
    private var weatherRemoteDataSource : WeatherRemoteDataSource,
    private var weatherLocalDataSource : WeatherLocalDataSource)
    : WeatherRepository {

    companion object{
        private var instance : WeatherRepositoryImp? = null
        fun getInstance(
            weatherRemoteDataSource : WeatherRemoteDataSource,
            weatherLocalDataSource : WeatherLocalDataSource)
                : WeatherRepositoryImp {
            return instance?: synchronized(this){
                val temp = WeatherRepositoryImp(weatherRemoteDataSource,weatherLocalDataSource)
                instance = temp
                temp
            }
        }
    }
    override suspend fun getWeatherFromApi(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?
    ): Flow<WeatherResponse> {
        return flowOf(weatherRemoteDataSource.getWeatherOverNetwork(latitude,longitude,units,lang))
    }

    override fun getCurrentWeather(): Flow<List<WeatherResponse>> {
        return weatherLocalDataSource.getCurrentWeather()
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        return weatherLocalDataSource.insertCurrentWeather(weatherResponse)
    }

    override suspend fun deleteCurrentWeather() {
       return weatherLocalDataSource.deleteCurrentWeather()
    }


    override fun getFavoritePlace(): Flow<List<FavoritePlace>> {
        return weatherLocalDataSource.getFavoritePlace()
    }

    override suspend fun insertFavoritePlace(favoritePlace: FavoritePlace) {
        return weatherLocalDataSource.insertFavoritePlace(favoritePlace)
    }

    override suspend fun deleteFavoritePlace(id : Int) {
        return weatherLocalDataSource.deleteFavoritePlace(id)
    }

    override fun getAlertsWeather(): Flow<List<AlertWeather>> {
        return weatherLocalDataSource.getAlertsWeather()
    }

    override suspend fun insertAlert(alertWeather: AlertWeather) {
        return weatherLocalDataSource.insertAlert(alertWeather)
    }

    override suspend fun deleteAlert(alertWeather: AlertWeather) {
        return weatherLocalDataSource.deleteAlert(alertWeather)
    }
}