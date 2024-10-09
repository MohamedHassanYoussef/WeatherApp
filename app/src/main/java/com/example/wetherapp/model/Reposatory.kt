package com.example.wetherapp.model


import androidx.room.Delete
import com.example.wetherapp.db.AlertPojo
import com.example.wetherapp.db.PlaceFavPojo
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast
import kotlinx.coroutines.flow.Flow

interface Reposatory {

    // remote
    suspend fun getCurrentWeather(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Flow<Current>

    suspend fun getForecastWeather(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Flow<Forecast>

    //fav
    fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>>
    suspend fun insertPlaceToFav(place: PlaceFavPojo)
    suspend fun deletePlaceFromFav(place: PlaceFavPojo)

    // alert
    fun getAllAlerts(): Flow<List<AlertPojo>>
    suspend fun insertAlert(alert: AlertPojo)
    suspend fun deleteAlert(alert: AlertPojo)

}