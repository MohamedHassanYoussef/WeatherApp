package com.example.wetherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface LocalDataDao {

    @Query("SELECT * FROM 'table'")
    fun getAllFavouritePlaces(): Flow<List<PlaceFavPojo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceToFav(place: PlaceFavPojo)

    @Delete
    suspend fun deletePlaceFromFav(place: PlaceFavPojo)



    @Query("SELECT * FROM tableAlerm")
    fun getAllAlerts(): Flow<List<AlertPojo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertPojo)

    @Delete
    suspend fun deleteAlert(alert: AlertPojo)
}