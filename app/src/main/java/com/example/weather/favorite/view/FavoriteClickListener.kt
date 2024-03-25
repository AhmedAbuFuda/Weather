package com.example.weather.favorite.view

import com.example.weather.model.FavoritePlace

interface FavoriteClickListener {
    fun onClick(favoritePlace: FavoritePlace)
    fun onClickDelete(id : Int)
}