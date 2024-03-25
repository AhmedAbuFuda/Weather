package com.example.weather.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.AlertDBState
import com.example.weather.model.AlertWeather
import com.example.weather.model.FavoritePlace
import com.example.weather.model.PlaceDBState
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel ( private val repo : WeatherRepository) : ViewModel(){

    var alertDB : MutableStateFlow<AlertDBState> = MutableStateFlow<AlertDBState>(AlertDBState.Loading)

    fun getAlertsWeather(){
        viewModelScope.launch(Dispatchers.IO){
            repo.getAlertsWeather()
                .catch {
                        e -> alertDB.value = AlertDBState.Failure(e)
                }?.collectLatest {
                        data -> alertDB.value = AlertDBState.Success(data)
                }
        }
    }

    fun insertAlert(alertWeather: AlertWeather){
        viewModelScope.launch(Dispatchers.IO){
            repo.insertAlert(alertWeather)
        }
    }

    fun deleteAlert(alertWeather: AlertWeather){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteAlert(alertWeather)
            getAlertsWeather()
        }
    }
}