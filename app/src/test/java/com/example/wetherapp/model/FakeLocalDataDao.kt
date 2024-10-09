package com.example.wetherapp.model

import com.example.wetherapp.db.LocalDataDao
import com.example.wetherapp.db.PlaceFavPojo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataDao(private var localData: MutableList<PlaceFavPojo> = mutableListOf() ): LocalDataDao {
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