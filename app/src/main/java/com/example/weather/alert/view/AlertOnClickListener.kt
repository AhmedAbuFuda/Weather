package com.example.weather.alert.view

import com.example.weather.model.AlertWeather

interface AlertOnClickListener {
    fun onClickDelete(alertWeather: AlertWeather)
}