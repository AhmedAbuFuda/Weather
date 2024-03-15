package com.example.weather.Home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.WeatherRepository
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel( private val repo : WeatherRepository) : ViewModel() {
    private var _weather: MutableLiveData<WeatherResponse> = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> = _weather

     fun getWeather(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _weather.postValue(repo.getWeatherFromApi(latitude, longitude, units, lang))
        }
    }
}