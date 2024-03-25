package com.example.weather.alert.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weather.model.AlertWeather

class AlertDiffUtil: DiffUtil.ItemCallback<AlertWeather>() {
    override fun areItemsTheSame(oldItem: AlertWeather, newItem: AlertWeather): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AlertWeather, newItem: AlertWeather): Boolean {
        return oldItem == newItem
    }
}