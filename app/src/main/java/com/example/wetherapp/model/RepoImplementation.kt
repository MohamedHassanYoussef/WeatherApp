package com.example.wetherapp.model


import android.util.Log
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast
import com.example.wetherapp.network.WeatherNetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class RepoImplementation(private val remote: WeatherNetworkResponse): Reposatory {
    override suspend fun getCurrentWeather(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Flow<Current> {
        Log.d("repocurrent", "current Weather langtudue: $long")
        return flowOf(remote.getCurrent(long, lat, language, units))
    }

    override suspend fun getForecastWeather(
        long: Double?,
        lat: Double?,
        language: String?,
        units: String?
    ): Flow<Forecast> {
        Log.d("repoForecast", "Forecast Weather langtudue: $long")
        Log.d("repo1", "Forecast Weather Data: $lat")
        Log.d("repo1", "Forecast Weather Data: ${remote.getForecast(long, lat, language, units)}")

        return flowOf(remote.getForecast(long, lat, language, units))
    }

    companion object {
        private var instance: RepoImplementation? = null
        fun getInstance(remoteSource: WeatherNetworkResponse): Reposatory {
            return instance ?: synchronized(this) {
                instance ?: RepoImplementation(remoteSource)
                    .also { instance = it }
            }
        }
    }

}