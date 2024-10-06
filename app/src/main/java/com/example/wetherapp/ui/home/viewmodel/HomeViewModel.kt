package com.example.wetherapp.ui.home.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.Reposatory
import com.example.wetherapp.model.forecast.Forecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val reposatory: Reposatory) : ViewModel() {
    private val _weatherData = MutableStateFlow<Current?>(null)
    val weatherData: StateFlow<Current?> = _weatherData
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _weatherForecast =MutableStateFlow<Forecast?>(null)
    val weatherForecast : StateFlow<Forecast?> = _weatherForecast

    fun getWeatherData(lat: Double, lon: Double, language: String, units: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reposatory.getCurrentWeather(lat, lon, language, units).collect { data ->
                    _weatherData.value = data
                }
            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getForecast(lat: Double, lon: Double, language: String, units: String){

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                reposatory.getForecastWeather(lat,lon,language,units).collect {data1->
                    _weatherForecast.value = data1
                }
            }catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }

        }
    }
}
