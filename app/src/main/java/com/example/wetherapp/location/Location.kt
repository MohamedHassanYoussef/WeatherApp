package com.example.wetherapp.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

class Location private constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val context: Context
) : LocationInterface {

    private val sharedFlow: MutableSharedFlow<Coordinate> = MutableSharedFlow(replay = 1)
    private val MY_LOCATION_PERMISSION_ID = 5005

    companion object {
        private var instance: Location? = null

        fun getInstance(
            fusedLocationClient: FusedLocationProviderClient,
            context: Context
        ): Location {
            return instance ?: synchronized(this) {
                instance ?: Location(fusedLocationClient, context).also {
                    instance = it
                }
            }
        }
    }

    fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation() {
        if (checkLocationPermissions() && isLocationEnabled()) {
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.Builder(0).apply {
                    setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                }.build(),
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            sharedFlow.tryEmit(Coordinate(location.latitude, location.longitude))
                        }
                    }
                },
                Looper.getMainLooper()
            )
        }
    }

    override fun getCurrentLocation(): Flow<Coordinate> {
        fetchCurrentLocation()
        return sharedFlow
    }

    fun getCityAndAddress(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            "Geocoder failed: ${e.message}"
        }
    }
}

data class Coordinate(var latitude: Double, var longitude: Double)
