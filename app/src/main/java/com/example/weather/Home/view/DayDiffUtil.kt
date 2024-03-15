package com.example.weather.Home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weather.model.WeatherEntry

class DayDiffUtil: DiffUtil.ItemCallback<List<WeatherEntry>>() {
    override fun areItemsTheSame(oldItem: List<WeatherEntry>, newItem: List<WeatherEntry>): Boolean {
        return oldItem[0].dt == newItem[0].dt
    }

    override fun areContentsTheSame(oldItem: List<WeatherEntry>, newItem: List<WeatherEntry>): Boolean {
        return oldItem[0] == newItem[0]
    }
}