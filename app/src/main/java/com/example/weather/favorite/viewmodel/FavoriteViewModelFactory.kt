package com.example.weather.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.Home.viewmodel.HomeViewModel
import com.example.weather.model.WeatherRepository

class FavoriteViewModelFactory (private val rope : WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(rope) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}