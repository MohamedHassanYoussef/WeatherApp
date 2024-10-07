package com.example.wetherapp.db

import kotlinx.coroutines.flow.Flow

class FavouriteDaoImplementation(private val favouriteDatabase: FavouriteDatabase) : FavouriteDao {
    override fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>> {
        return favouriteDatabase.getDao().getAllFavouritePlaces()
    }

    override suspend fun insertPlaceToFav(place: PlaceFavPojo) {
        return favouriteDatabase.getDao().insertPlaceToFav(place)
    }

    override suspend fun deletePlaceFromFav(place: PlaceFavPojo) {
        return favouriteDatabase.getDao().deletePlaceFromFav(place)
    }
}