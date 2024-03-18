package com.example.weather.Home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.APIState
import com.example.weather.model.CurrentDBState
import com.example.weather.model.WeatherRepository
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel( private val repo : WeatherRepository) : ViewModel() {
     var weatherApi: MutableStateFlow<APIState> = MutableStateFlow<APIState>(APIState.Loading)
     var weatherDB : MutableStateFlow<CurrentDBState> = MutableStateFlow<CurrentDBState>(CurrentDBState.Loading)

     fun getWeather(latitude: Double, longitude: Double, units: String?, lang: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherFromApi(latitude, longitude, units, lang).collect{
                weatherApi.value = APIState.Success(it)
            }
        }
    }

    fun getCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCurrentWeather()
                .catch {
                        e -> weatherDB.value = CurrentDBState.Failure(e)
                }?.collectLatest {
                        data -> weatherDB.value = CurrentDBState.Success(data)
                }
        }
    }

    fun insertCurrentWeather(weatherResponse: WeatherResponse){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertCurrentWeather(weatherResponse)
        }
    }

    fun deleteCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCurrentWeather()
        }
    }
}