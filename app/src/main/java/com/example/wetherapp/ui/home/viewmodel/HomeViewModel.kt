package com.example.wetherapp.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.Reposatory
import com.example.wetherapp.model.forecast.Forecast
import com.example.wetherapp.network.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val reposatory: Reposatory) : ViewModel() {

    // Updated to use the State sealed class
    private val _weatherData = MutableStateFlow<State<Current>>(State.Loading)
    val weatherData: StateFlow<State<Current>> = _weatherData

    private val _weatherForecast = MutableStateFlow<State<Forecast>>(State.Loading)
    val weatherForecast: StateFlow<State<Forecast>> = _weatherForecast

    // Function to get current weather data
    fun getWeatherData(lat: Double, lon: Double, language: String, units: String) {
        viewModelScope.launch {
            _weatherData.value = State.Loading
            try {
                reposatory.getCurrentWeather(lat, lon, language, units).collect { data ->
                    _weatherData.value = State.Success(data)
                    Log.d("data", "getWeatherData: $data")
                }
            } catch (e: Exception) {
                _weatherData.value = State.Error(e)
                e.printStackTrace()
            }
        }
    }

    // Function to get weather forecast data
    fun getForecast(lat: Double, lon: Double, language: String, units: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherForecast.value = State.Loading
            try {
                reposatory.getForecastWeather(lat, lon, language, units).collect { data1 ->
                    _weatherForecast.value = State.Success(data1)
                }
            } catch (e: Exception) {
                _weatherForecast.value = State.Error(e)
                e.printStackTrace()
            }
        }
    }
}
