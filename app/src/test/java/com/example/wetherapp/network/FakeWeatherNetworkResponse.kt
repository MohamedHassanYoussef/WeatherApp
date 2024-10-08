package com.example.wetherapp.network

import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast

class FakeWeatherNetworkResponse(private var remoteCurrent: Current,private var remoteForecast: Forecast): WeatherNetworkResponse {
    override suspend fun getCurrent(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Current {
       return remoteCurrent
    }

    override suspend fun getForecast(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Forecast {
       return remoteForecast
    }
}