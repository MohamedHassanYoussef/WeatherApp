package com.example.wetherapp.db

import kotlinx.coroutines.flow.Flow

class LocalDaoImplementation(private val localDataDatabase: LocalDatabase) : LocalDataDao{
    override fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>> {
        return localDataDatabase.getDao().getAllFavouritePlaces()
    }

    override suspend fun insertPlaceToFav(place: PlaceFavPojo) {
        return localDataDatabase.getDao().insertPlaceToFav(place)
    }

    override suspend fun deletePlaceFromFav(place: PlaceFavPojo) {
        return localDataDatabase.getDao().deletePlaceFromFav(place)
    }

    override fun getAllAlerts(): Flow<List<AlertPojo>> {
        return localDataDatabase.getDao().getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
        return localDataDatabase.getDao().insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        return localDataDatabase.getDao().deleteAlert(alert)
    }
}