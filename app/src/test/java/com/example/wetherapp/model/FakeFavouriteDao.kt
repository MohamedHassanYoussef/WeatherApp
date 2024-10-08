package com.example.wetherapp.model

import com.example.wetherapp.db.FavouriteDao
import com.example.wetherapp.db.PlaceFavPojo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFavouriteDao(private var localData: MutableList<PlaceFavPojo> = mutableListOf() ): FavouriteDao {
    override fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>> {
        return  flowOf(localData)
    }

    override suspend fun insertPlaceToFav(place: PlaceFavPojo) {
         (localData.add(place))

    }
    override suspend fun deletePlaceFromFav(place: PlaceFavPojo) {
        (localData.remove(place))
    }
}