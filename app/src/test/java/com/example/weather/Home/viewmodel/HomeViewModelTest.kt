package com.example.weather.Home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.model.CurrentDBState
import com.example.weather.model.FakeWeatherRepository
import com.example.weather.model.WeatherResponse
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeViewModelTest{

    lateinit var repo : FakeWeatherRepository
    lateinit var viewModel: HomeViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        repo = FakeWeatherRepository()
        viewModel = HomeViewModel(repo)
    }

    @Test
    fun getWeather_returnMutableValueIsNotNull(){

        //When
        viewModel.getWeather(25.5498,31.4889,"standard","ar")

        //Then
        val result = viewModel.weatherApi
        assertThat(result,notNullValue())
    }

    @Test
    fun getCurrentWeather_returnListNotNull()= runBlocking {

        //When
        viewModel.getCurrentWeather()

        //Then
        val result = viewModel.weatherDB.first()
        var data = emptyList<WeatherResponse>()
        when(result){
            is CurrentDBState.Success ->
                data = result.data
            else ->{}

        }
        assertThat(data, `is`(notNullValue()))
    }

    @Test
    fun insertCurrentWeather_weatherResponse_returnUnit() {
        //Given
        val weatherResponse = WeatherResponse()
        //When
        val result = viewModel.insertCurrentWeather(weatherResponse)

        assertEquals(Unit,result)
    }

    @Test
    fun deleteCurrentWeather_returnUnit() {

        //When
        val result = viewModel.deleteCurrentWeather()

        //Then
        assertEquals(Unit,result)
    }
}