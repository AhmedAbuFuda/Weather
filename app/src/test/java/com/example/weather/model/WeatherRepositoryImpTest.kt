package com.example.weather.model

import com.example.weather.db.FakeLocalDataSource
import com.example.weather.db.WeatherLocalDataSource
import com.example.weather.network.FakeRemoteDataSource
import com.example.weather.network.WeatherRemoteDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WeatherRepositoryImpTest{

    val weatherResponse = WeatherResponse()

    val currentWeather = listOf(
        weatherResponse
    )

    val favoritePlace1 = FavoritePlace(1,29.848,31.1999,"Fuwwa")
    val favoritePlace2 = FavoritePlace(2,45.984,20.8452,"Alex")
    val favoritePlace3 = FavoritePlace(3,10.158,12.5485,"Cairo")
    val favoritePlace4 = FavoritePlace(4,70.564,80.6666,"Tanta")
    val favoritePlace5 = FavoritePlace(5,30.654,41.0518,"Port Said")

    val favoriteList = listOf(
        favoritePlace1,favoritePlace2,favoritePlace3,favoritePlace4,favoritePlace5
    )

    lateinit var fakeRemoteDataSource: WeatherRemoteDataSource
    lateinit var fakeLocalDataSource: WeatherLocalDataSource
    lateinit var fakeRepository: WeatherRepository

    @Before
    fun setup(){
        fakeRemoteDataSource = FakeRemoteDataSource(weatherResponse)
        fakeLocalDataSource = FakeLocalDataSource(currentWeather.toMutableList(),favoriteList.toMutableList())
        fakeRepository = FakeWeatherRepository(currentWeather.toMutableList(),favoriteList.toMutableList())
    }


    @Test
    fun getWeatherFromApi_returnWeatherResponse() = runTest{
        // Given
        val weatherResponse = WeatherResponse()
        // When
        fakeRepository.getWeatherFromApi(13.584,14.8489,"metric","en").collect{
            val weather =it
            // Then
            assertThat(weather, IsEqual(weatherResponse))
        }

    }

    @Test
    fun insertCurrentWeather_returnCurrentWeather()= runTest {
        // Given
        val weatherResponse = WeatherResponse()

        // When
        fakeRepository.deleteCurrentWeather()
        fakeRepository.insertCurrentWeather(weatherResponse)

        //Then
        val result = fakeRepository.getCurrentWeather()
        assertThat(result.first()[0],`is`(weatherResponse))
    }

    @Test
    fun getCurrentWeather_returnSizeOfList()= runTest {
        // Given

        //When
        fakeRepository.getCurrentWeather().collect{
            val weather = it.size
            // Then
            assertEquals(1, weather)
        }
    }
    @Test
    fun deleteCurrentWeather_returnSizeZero()= runTest{
        // Given

        //When
        fakeRepository.deleteCurrentWeather()

        //Then
        fakeRepository.getCurrentWeather().collect{
            val weather = it.size
            // Then
            assertEquals(0, weather)
        }
    }

    @Test
    fun getFavoritePlace_returnSizeOfList()= runTest{
        // Given

        //When
        fakeRepository.getFavoritePlace().collect{
            val favorite = it.size
            // Then
            assertEquals(5, favorite)
        }
    }

    @Test
    fun insertFavoritePlace_returnPlace() = runTest {
        //Give
        val favoritePlace = FavoritePlace(6,29.848,31.1999,"Fuwwa")

        //When
        fakeRepository.insertFavoritePlace(favoritePlace)
        fakeRepository.getFavoritePlace().collect{
            val result = it[5]

            //Then
            assertThat(result, `is`(favoritePlace))
            assertThat(result.id, `is`(favoritePlace.id))
        }

    }

    @Test
    fun deleteFavoritePlace() = runTest{
        //Given
        val favoritePlace = FavoritePlace()
        fakeRepository.insertFavoritePlace(favoritePlace)

        //When
        fakeRepository.deleteFavoritePlace(favoritePlace.id)
        fakeRepository.getFavoritePlace().collect{
            val result = it.size

            //Then
            assertThat(result,`is`(4))
        }

    }
}