package com.example.wetherapp.ui.home.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wetherapp.model.FakeReposatory
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    private lateinit var fakeRepo: FakeReposatory
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup() {
        fakeRepo = FakeReposatory()
        homeViewModel = HomeViewModel(reposatory = fakeRepo)
    }


    @Test
    fun getCurrentWeather_success() = runBlockingTest {
        // Given
        val latitude = 0.0
        val longitude = 0.0
        val language = "en"


        // When
        val result = homeViewModel.getWeatherData(latitude, longitude, language, "metrec")

        // Then
        assertThat(result, not(nullValue()))
    }

    @Test
    fun getForecastWeather_success() = runBlocking {

        val latitude = 0.0
        val longitude = 0.0
        val language = "en"

        val result = homeViewModel.getForecast(latitude, longitude, language, "metrec")

        assertThat(result, not(nullValue()))

    }


}