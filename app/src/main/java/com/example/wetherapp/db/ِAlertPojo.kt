package com.example.wetherapp.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "tableAlerm")
data class AlertPojo(
    @PrimaryKey
    var id: Int,
    var cityName: String,
    var time :String,
    var latitude: Double,
    var longitude: Double
):Serializable
