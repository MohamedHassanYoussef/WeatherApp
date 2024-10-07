package com.example.wetherapp.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Dao
@Entity(tableName = "table")
data class PlaceFavPojo(
    @PrimaryKey
    var id: Int ,

    var cityName: String,
    var latitude: Double,
    var longitude: Double
): Serializable