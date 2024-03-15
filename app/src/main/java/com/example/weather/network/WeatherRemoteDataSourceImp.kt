package com.example.weather.network

import com.example.weather.model.WeatherResponse

class WeatherRemoteDataSourceImp private constructor() : WeatherRemoteDataSource {

    private val weatherService : WeatherService by lazy {
        RetrofitHelper.retrofitInstance.create(WeatherService::class.java)
    }
    override suspend fun getWeatherOverNetwork(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?,
    ): WeatherResponse {
        val response = weatherService.getWeather(latitude,longitude,units,lang)
        return response
    }

    companion object{
        private var instance : WeatherRemoteDataSourceImp? = null
        fun getInstance(): WeatherRemoteDataSourceImp{
            return instance?: synchronized(this){
                val temp = WeatherRemoteDataSourceImp()
                instance = temp
                temp
            }
        }
    }
}