package com.example.weather.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.APIState
import com.example.weather.model.FavoritePlace

import com.example.weather.model.WeatherRepository
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MapViewModel( private val repo : WeatherRepository) : ViewModel() {

    var weatherApi: MutableStateFlow<APIState> = MutableStateFlow<APIState>(APIState.Loading)
    fun getWeather(latitude: Double, longitude: Double, units: String?, lang: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherFromApi(latitude, longitude, units, lang).collect{
                weatherApi.value = APIState.Success(it)
            }
        }
    }

    fun insertFavoritePlace(favoritePlace: FavoritePlace){
        viewModelScope.launch(Dispatchers.IO){
            repo.insertFavoritePlace(favoritePlace)
        }
    }


}