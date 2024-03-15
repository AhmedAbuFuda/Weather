package com.example.weather.Home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.WeatherRepository

class HomeViewModelFactory (private val rope : WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(rope) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}