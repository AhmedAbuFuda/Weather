package com.example.weather.network

import com.example.weather.model.WeatherResponse

class FakeRemoteDataSource(private var weatherResponse: WeatherResponse): WeatherRemoteDataSource{
    override suspend fun getWeatherOverNetwork(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?
    ): WeatherResponse {
        return weatherResponse
    }
}