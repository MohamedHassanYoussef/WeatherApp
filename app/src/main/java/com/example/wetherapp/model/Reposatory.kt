package com.example.wetherapp.model




import com.example.wetherapp.db.PlaceFavPojo
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.forecast.Forecast
import kotlinx.coroutines.flow.Flow

interface Reposatory {
    suspend fun getCurrentWeather(
        long:Double?,
        lat:Double?,
        language: String?,
        units:String?
    ): Flow<Current>

    suspend fun getForecastWeather(
        long:Double?,
        lat:Double?,
        language: String?,
        units:String?
    ): Flow<Forecast>


    fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>>


    suspend fun insertPlaceToFav(place: PlaceFavPojo)


    suspend fun deletePlaceFromFav(place: PlaceFavPojo)

}