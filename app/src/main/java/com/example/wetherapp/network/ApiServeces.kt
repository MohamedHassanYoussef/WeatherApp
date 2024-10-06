package com.example.wetherapp.network


import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast
import com.example.wetherapp.network.RetrofitHelper.api_key
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServeces {
    @GET("forecast")
    suspend fun getForcastWeather(
        @Query("lat") lat:Double?,
        @Query("lon") lon:Double?,
        @Query("units")units:String?,
        @Query("lang")language: String?,
        @Query("appid") appid:String= api_key
    ): Forecast
    @GET("weather")
    suspend fun getAllWeather(
        @Query("lat") lat:Double?,
        @Query("lon") lon:Double?,
        @Query("units")units:String?,
        @Query("lang")language: String?,
        @Query("appid") appid:String=api_key

    ): Current

}