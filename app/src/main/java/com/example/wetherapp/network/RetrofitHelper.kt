package com.example.wetherapp.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper{

   val base_url = "https://api.openweathermap.org/data/2.5/"
    val api_key ="c6b1bb45d7073e1efdf5421a71afb4c6"

    private val retrofit1 = Retrofit.Builder()
        .baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofit = retrofit1.create(ApiServeces::class.java)
}