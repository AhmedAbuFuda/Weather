package com.example.weather.db

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weather.model.FavoritePlace
import com.example.weather.model.WeatherResponse
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceImpTest{
    private lateinit var database : WeatherDataBase
    private lateinit var weatherLocalDataSource: WeatherLocalDataSourceImp

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).build()
        val testingContext : Application = ApplicationProvider.getApplicationContext()
        weatherLocalDataSource = WeatherLocalDataSourceImp(testingContext)
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insertCurrentWeather_returnCurrentWeather()= runTest {
        //Given
        val weatherResponse = WeatherResponse()
        weatherLocalDataSource.insertCurrentWeather(weatherResponse)

        //When
        val result = weatherLocalDataSource.getCurrentWeather()

        //Then
        assertThat(result,not(nullValue()))
    }

    @Test
    fun deleteCurrentWeather_returnSizeZero()= runTest {
        //Given
        val weatherResponse = WeatherResponse()
        weatherLocalDataSource.insertCurrentWeather(weatherResponse)

        //When
        weatherLocalDataSource.deleteCurrentWeather()
        val result = weatherLocalDataSource.getCurrentWeather()

        //Then
        TestCase.assertEquals(0, result.first().size)
    }

    @Test
    fun getCurrentWeather_returnTrueOrFalse()= runTest {
        //Given
        val weatherResponse = WeatherResponse()
        weatherLocalDataSource.insertCurrentWeather(weatherResponse)

        //When
        val result = weatherLocalDataSource.getCurrentWeather()

        //Then
        TestCase.assertEquals(true, result.first().contains(result.first()[0]))
    }

    @Test
    fun insertFavoritePlace_returnFavoritePlace()= runTest {
        //Given
        val favoritePlace = FavoritePlace()
        weatherLocalDataSource.insertFavoritePlace(favoritePlace)

        //When
        val result = weatherLocalDataSource.getFavoritePlace()
        //Then
        assertThat(result,not(nullValue()))
    }


    @Test
    fun deleteFavoritePlace_returnSizeZero()= runTest {
        //Given
        val favoritePlace = FavoritePlace()
        weatherLocalDataSource.insertFavoritePlace(favoritePlace)

        //When
        weatherLocalDataSource.deleteFavoritePlace(favoritePlace.id)
        val result = weatherLocalDataSource.getFavoritePlace()

        //Then
        TestCase.assertEquals(0, result.first().size)
    }

    @Test
    fun getFavoritePlace_returnTrueOrFalse()= runTest {
        //Given
        val favoritePlace = FavoritePlace()
        weatherLocalDataSource.insertFavoritePlace(favoritePlace)

        //When
        val result = weatherLocalDataSource.getFavoritePlace()

        //Then
        TestCase.assertEquals(true, result.first().contains(result.first()[0]))
    }

}