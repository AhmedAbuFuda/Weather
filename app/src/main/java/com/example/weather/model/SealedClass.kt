package com.example.weather.model

import kotlinx.coroutines.flow.Flow

sealed class APIState {
    class Success(val data:WeatherResponse): APIState()
    class Failure(val msg:Throwable): APIState()
    object Loading: APIState()
}

sealed class CurrentDBState {
    class Success(val data: List<WeatherResponse>): CurrentDBState()
    class Failure(val msg:Throwable): CurrentDBState()
    object Loading: CurrentDBState()
}

sealed class FavoriteDBState {
    class Success(val data: List<FavoriteWeather>): FavoriteDBState()
    class Failure(val msg:Throwable): FavoriteDBState()
    object Loading: FavoriteDBState()
}

sealed class PlaceDBState {
    class Success(val data: List<FavoritePlace>): PlaceDBState()
    class Failure(val msg:Throwable): PlaceDBState()
    object Loading: PlaceDBState()
}
