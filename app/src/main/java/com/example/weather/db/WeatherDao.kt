package com.example.weather.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.model.AlertWeather
import com.example.weather.model.FavoritePlace
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weatherResponse : WeatherResponse)
    @Query("DELETE FROM currentWeather")
    suspend fun deleteCurrentWeather()
    @Query("SELECT * FROM currentWeather")
    fun getCurrentWeather(): Flow<List<WeatherResponse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePlace(favoritePlace : FavoritePlace)
    @Query("DELETE FROM favoritePlace WHERE id = :id")
    suspend fun deleteFavoritePlace(id : Int)
    @Query("SELECT * FROM favoritePlace")
    fun getFavoritePlace(): Flow<List<FavoritePlace>>


    @Query("SELECT * FROM alertWeather")
    fun getAlertWeather(): Flow<List<AlertWeather>>
    @Query("SELECT * FROM alertWeather WHERE id = :id")
    suspend fun getAlertById(id : String) : AlertWeather
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlert(alertWeather: AlertWeather)
    @Delete
    suspend fun deleteAlert(alertWeather: AlertWeather)

}