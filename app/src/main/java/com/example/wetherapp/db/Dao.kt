package com.example.wetherapp.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table")
data class PlaceFavPojo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var cityName: String,
    var latitude: Double,
    var longitude: Double
): Serializable