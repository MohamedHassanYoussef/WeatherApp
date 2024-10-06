package com.example.wetherapp.location

import kotlinx.coroutines.flow.Flow


interface LocationInterface {
    fun getCurrentLocation(): Flow<Coordinate>
}