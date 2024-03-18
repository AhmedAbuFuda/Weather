package com.example.weather.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.Home.viewmodel.HomeViewModel
import com.example.weather.model.WeatherRepository

class MapViewModelFactory (private val rope : WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(rope) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}