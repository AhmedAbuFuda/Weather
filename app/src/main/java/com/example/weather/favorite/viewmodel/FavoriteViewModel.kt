package com.example.weather.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.APIState
import com.example.weather.model.FavoriteDBState
import com.example.weather.model.FavoriteWeather
import com.example.weather.model.PlaceDBState
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel( private val repo : WeatherRepository) : ViewModel() {
    var favoriteDB : MutableStateFlow<PlaceDBState> = MutableStateFlow<PlaceDBState>(PlaceDBState.Loading)
    var weatherDB : MutableStateFlow<FavoriteDBState> = MutableStateFlow<FavoriteDBState>(FavoriteDBState.Loading)
    var weatherApi: MutableStateFlow<APIState> = MutableStateFlow<APIState>(APIState.Loading)

    fun getWeather(latitude: Double, longitude: Double, units: String?, lang: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherFromApi(latitude, longitude, units, lang).collect{
                weatherApi.value = APIState.Success(it)
            }
        }
    }

    fun getFavoritePlace(){
        viewModelScope.launch(Dispatchers.IO){
            repo.getFavoritePlace()
                .catch {
                    e -> favoriteDB.value = PlaceDBState.Failure(e)
                }?.collectLatest {
                    data -> favoriteDB.value = PlaceDBState.Success(data)
                }
        }
    }

    fun deleteFavoritePlace(id : Int){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteFavoritePlace(id)
        }
    }

    fun getFavoriteWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFavoriteWeather()
                .catch {
                        e -> weatherDB.value = FavoriteDBState.Failure(e)
                }?.collectLatest {
                        data -> weatherDB.value = FavoriteDBState.Success(data)
                }
        }
    }

    fun insertFavoriteWeather(favoriteWeather: FavoriteWeather){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavoriteWeather(favoriteWeather)
        }
    }

    fun deleteFavoriteWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavoriteWeather()
        }
    }
}