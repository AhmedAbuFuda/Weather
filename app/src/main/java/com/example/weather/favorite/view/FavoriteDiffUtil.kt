package com.example.weather.favorite.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weather.model.FavoritePlace
import com.example.weather.model.FavoriteWeather
import com.example.weather.model.WeatherEntry

class FavoriteDiffUtil: DiffUtil.ItemCallback<FavoritePlace>() {
    override fun areItemsTheSame(oldItem: FavoritePlace, newItem: FavoritePlace): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavoritePlace, newItem: FavoritePlace): Boolean {
        return oldItem == newItem
    }
}