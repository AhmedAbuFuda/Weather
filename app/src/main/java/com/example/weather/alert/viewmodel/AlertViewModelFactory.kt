package com.example.weather.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.favorite.viewmodel.FavoriteViewModel
import com.example.weather.model.WeatherRepository

class AlertViewModelFactory (private val rope : WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(rope) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}