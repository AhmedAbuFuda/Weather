package com.example.weather.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID


@Entity(tableName = "alertWeather")
data class AlertWeather(
    @PrimaryKey @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),
    val fromDate: String, val toDate: String,
    val toTime: String, val fromTime: String,
    val alertType: String,
    val lon: Double,
    val lat: Double,
    ) : Serializable{
    constructor():this("1","","","","","",0.0,0.0)
}