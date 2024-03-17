package com.example.weather.Home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.APIState
import com.example.weather.model.WeatherRepository
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel( private val repo : WeatherRepository) : ViewModel() {
     var weather: MutableStateFlow<APIState> = MutableStateFlow<APIState>(APIState.Loading)

     fun getWeather(
        latitude: Double,
        longitude: Double,
        units: String?,
        lang: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherFromApi(latitude, longitude, units, lang).collect{
                weather.value = APIState.Success(it)
            }
        }
    }
}