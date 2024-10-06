package com.example.wetherapp.network


import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast


interface WeatherNetworkResponse {
    suspend fun getCurrent(
        long:Double?,
        lat:Double?,
        language: String?,
        units:String?
    ): Current

    suspend fun getForecast(
        long:Double?,
        lat:Double?,
        language: String?,
        units:String?
    ): Forecast
}