package com.example.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather.model.FavoritePlace
import com.example.weather.model.WeatherResponse
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {
    private lateinit var database : WeatherDataBase
    private lateinit var dao: WeatherDao

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).build()

        dao = database.getWeather()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insertCurrentWeather_returnCurrentWeather()= runBlockingTest {
        //Given
        val weatherResponse = WeatherResponse()
        dao.insertCurrentWeather(weatherResponse)

        //When
        val result = dao.getCurrentWeather()

        //Then
        assertThat(result,not(nullValue()))
        assertThat(result.first().size,`is`(1))
    }

    @Test
    fun deleteCurrentWeather_returnSizeZero()= runBlockingTest {
        //Given
        val weatherResponse = WeatherResponse()
        dao.insertCurrentWeather(weatherResponse)

        //When
        dao.deleteCurrentWeather()
        val result = dao.getCurrentWeather()

        //Then
        assertEquals(0,result.first().size)
    }

    @Test
    fun getCurrentWeather_returnTrueOrFalse()= runBlockingTest {
        //Given
        val weatherResponse = WeatherResponse()
        dao.insertCurrentWeather(weatherResponse)

        //When
        val result = dao.getCurrentWeather()

        //Then
        assertEquals(true,result.first().contains(result.first()[0]))
    }

    @Test
    fun insertFavoritePlace_returnFavoritePlace()= runBlockingTest {
        //Given
        val favoritePlace = FavoritePlace()
        dao.insertFavoritePlace(favoritePlace)

        //When
        val result = dao.getFavoritePlace()
        //Then
        assertThat(result,not(nullValue()))
    }


    @Test
    fun deleteFavoritePlace_returnSizeZero()= runBlockingTest {
        //Given
        val favoritePlace = FavoritePlace()
        dao.insertFavoritePlace(favoritePlace)

        //When
        dao.deleteFavoritePlace(favoritePlace.id)
        val result = dao.getFavoritePlace()

        //Then
        assertEquals(0,result.first().size)
    }

    @Test
    fun getFavoritePlace_returnTrueOrFalse()= runBlockingTest {
        //Given
        val favoritePlace = FavoritePlace()
        dao.insertFavoritePlace(favoritePlace)

        //When
        val result = dao.getFavoritePlace()

        //Then
        assertEquals(true,result.first().contains(result.first()[0]))
    }

}