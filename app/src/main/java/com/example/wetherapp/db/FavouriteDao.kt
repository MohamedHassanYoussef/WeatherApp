package com.example.wetherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface FavouriteDao {

    @Query("SELECT * FROM 'table'")
    fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceToFav(place: PlaceFavPojo)

    @Delete
    suspend fun deletePlaceFromFav(place: PlaceFavPojo)
}