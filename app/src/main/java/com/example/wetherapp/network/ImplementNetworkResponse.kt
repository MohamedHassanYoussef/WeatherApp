package com.example.wetherapp.network

import android.util.Log
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast


class ImplementNetworkResponse private constructor(private val weatherApiService: ApiServeces) : WeatherNetworkResponse {

    private val apiService: ApiServeces by lazy { RetrofitHelper.retrofit }

    companion object {
        private var instance: ImplementNetworkResponse? = null
        fun getInstance(weatherApiService: ApiServeces): ImplementNetworkResponse {
            return instance ?: synchronized(this) {
                instance ?: ImplementNetworkResponse(weatherApiService)
                    .also { instance = it }
            }
        }
    }

    override suspend fun getCurrent(
        longitude: Double?,
        latitude: Double?,
        language: String?,
        units: String?
    ): Current {
        return apiService.getAllWeather(longitude, latitude, language, units)
    }

     override suspend fun getForecast(
         long: Double?,
         lat: Double?,
         language: String?,
         units: String?
     ): Forecast {
         return apiService.getForcastWeather(long, lat, language, units)

     }

 }
