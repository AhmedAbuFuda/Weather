package com.example.weather.Home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weather.model.WeatherEntry
import com.example.weather.model.WeatherResponse

class WeatherDiffUtil : DiffUtil.ItemCallback<WeatherEntry>() {
    override fun areItemsTheSame(oldItem: WeatherEntry, newItem: WeatherEntry): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: WeatherEntry, newItem: WeatherEntry): Boolean {
        return oldItem == newItem
    }
}