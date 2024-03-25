package com.example.weather.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.APIState
import com.example.weather.model.FavoritePlace
import com.example.weather.model.PlaceDBState
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel( private val repo : WeatherRepository) : ViewModel() {
    var favoriteDB : MutableStateFlow<PlaceDBState> = MutableStateFlow<PlaceDBState>(PlaceDBState.Loading)
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

    fun insertFavoritePlace(favoritePlace: FavoritePlace){
        viewModelScope.launch(Dispatchers.IO){
            repo.insertFavoritePlace(favoritePlace)
        }
    }

    fun deleteFavoritePlace(id : Int){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteFavoritePlace(id)
        }
    }


}