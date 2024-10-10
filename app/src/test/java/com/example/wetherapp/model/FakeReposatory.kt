package com.example.wetherapp.model

import com.example.wetherapp.db.AlertPojo
import com.example.wetherapp.db.PlaceFavPojo
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast
import kotlinx.coroutines.flow.Flow

class FakeReposatory():Reposatory {
    override suspend fun getCurrentWeather(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Flow<Current> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastWeather(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Flow<Forecast> {
        TODO("Not yet implemented")
    }

    override fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPlaceToFav(place: PlaceFavPojo) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaceFromFav(place: PlaceFavPojo) {
        TODO("Not yet implemented")
    }

    override fun getAllAlerts(): Flow<List<AlertPojo>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: AlertPojo) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        TODO("Not yet implemented")
    }
}