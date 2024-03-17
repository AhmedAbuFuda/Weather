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
}