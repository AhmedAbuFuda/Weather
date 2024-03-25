package com.example.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.Converters
import com.example.weather.model.AlertWeather
import com.example.weather.model.FavoritePlace
import com.example.weather.model.WeatherResponse

@Database(entities = [WeatherResponse::class, AlertWeather::class, FavoritePlace::class], version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getWeather() : WeatherDao
    companion object{
        private var INSTANCE : WeatherDataBase? = null

        fun getInstance (context: Context) : WeatherDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, WeatherDataBase::class.java,"WeatherDatabase")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}