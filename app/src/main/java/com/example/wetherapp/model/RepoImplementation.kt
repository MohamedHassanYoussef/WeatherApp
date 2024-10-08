package com.example.wetherapp.model

import com.example.wetherapp.db.FavouriteDao
import com.example.wetherapp.db.FavouriteDaoImplementation
import com.example.wetherapp.db.PlaceFavPojo
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast
import com.example.wetherapp.network.WeatherNetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class RepoImplementation(private val remote: WeatherNetworkResponse, private val local: FavouriteDao) : Reposatory {

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

    companion object {
        private var instance: RepoImplementation? = null
        fun getInstance(remoteSource: WeatherNetworkResponse, local: FavouriteDaoImplementation): Reposatory {
            return instance ?: synchronized(this) {
                instance ?: RepoImplementation(remoteSource, local)
                    .also { instance = it }
            }
        }
    }
}
