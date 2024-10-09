package com.example.wetherapp.model

import com.example.wetherapp.db.AlertPojo
import com.example.wetherapp.db.LocalDataDao
import com.example.wetherapp.db.LocalDaoImplementation
import com.example.wetherapp.db.PlaceFavPojo
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast
import com.example.wetherapp.network.WeatherNetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class RepoImplementation(private val remote: WeatherNetworkResponse, private val local: LocalDataDao) : Reposatory {

    override suspend fun getCurrentWeather(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Flow<Current> {
        return flowOf(remote.getCurrent(long, lat, language, units))
    }

    override suspend fun getForecastWeather(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Flow<Forecast> {
        return flowOf(remote.getForecast(long, lat, language, units))
    }

    override fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>> {
        return local.getAllFavouritePlaces()
    }

    override suspend fun insertPlaceToFav(place: PlaceFavPojo) {
        local.insertPlaceToFav(place)
    }

    override suspend fun deletePlaceFromFav(place: PlaceFavPojo) {
        local.deletePlaceFromFav(place)
    }

    override fun getAllAlerts(): Flow<List<AlertPojo>> {
        return   local.getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
        return local.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        return local.deleteAlert(alert)
    }

    companion object {
        private var instance: RepoImplementation? = null
        fun getInstance(remoteSource: WeatherNetworkResponse, local: LocalDaoImplementation): Reposatory {
            return instance ?: synchronized(this) {
                instance ?: RepoImplementation(remoteSource, local)
                    .also { instance = it }
            }
        }
    }
}
