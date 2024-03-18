package com.example.weather.favorite.view

import com.example.weather.model.FavoritePlace
import com.example.weather.model.FavoriteWeather

interface FavoriteClickListener {
    fun onClick(favoritePlace: FavoritePlace)
    fun onClickDelete(id : Int)
}