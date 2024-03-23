package com.example.weather.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.model.CurrentDBState
import com.example.weather.model.FakeWeatherRepository
import com.example.weather.model.FavoritePlace
import com.example.weather.model.PlaceDBState
import com.example.weather.model.WeatherResponse
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest{

    lateinit var repo : FakeWeatherRepository
    lateinit var viewModel: FavoriteViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        repo = FakeWeatherRepository()
        viewModel = FavoriteViewModel(repo)
    }

    @Test
    fun getFavoritePlace_returnListNotNull()= runBlocking {

        //When
        viewModel.getFavoritePlace()

        //Then
        val result = viewModel.favoriteDB.first()
        var data = emptyList<FavoritePlace>()
        when(result){
            is PlaceDBState.Success ->
                data = result.data
            else ->{}

        }
        assertThat(data, `is`(notNullValue()))
    }

    @Test
    fun deleteFavoritePlace_returnUnit(){
        //Given
        val favoritePlace = FavoritePlace(id = 2,41.158,35.564,"fuwwa")
        viewModel.insertFavoritePlace(favoritePlace)

        //When
        val result = viewModel.deleteFavoritePlace(favoritePlace.id)

        assertEquals(Unit, result)
    }
}