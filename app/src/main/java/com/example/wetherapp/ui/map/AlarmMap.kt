package com.example.wetherapp.ui.map

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentAlarmMapBinding
import com.example.wetherapp.db.AlertPojo
import com.example.wetherapp.db.LocalDaoImplementation
import com.example.wetherapp.db.LocalDatabase
import com.example.wetherapp.location.Coordinate
import com.example.wetherapp.model.RepoImplementation
import com.example.wetherapp.network.ImplementNetworkResponse
import com.example.wetherapp.network.RetrofitHelper
import com.example.wetherapp.ui.alert.view.AlarmHelper
import com.example.wetherapp.ui.alert.viewmodel.AlertFactory
import com.example.wetherapp.ui.alert.viewmodel.AlertViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class AlarmMap : Fragment() {

    private lateinit var binding: FragmentAlarmMapBinding
    private var coordinate: Coordinate? = null
    private var marker: Marker? = null
    private var googleMap: GoogleMap? = null
    private var selectedDateTime: Calendar? = null

    private val alertViewModel: AlertViewModel by viewModels {
        AlertFactory(
            RepoImplementation.getInstance(
                ImplementNetworkResponse.getInstance(RetrofitHelper.retrofit),
                LocalDaoImplementation(LocalDatabase.getInstance(requireContext()))
            )
        )
    }

    private val callback = OnMapReadyCallback { googleMapInstance ->
        googleMap = googleMapInstance
        googleMap?.setOnMapClickListener { latLng ->
            coordinate = Coordinate(latLng.latitude, latLng.longitude)
            marker?.remove()

            marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Selected Location")
            )

            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    5f
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_alarm) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)



        binding.btnAlarmLocation.setOnClickListener {
            coordinate?.let { coord ->
                datePickerDialog(coord)
            } ?: run {
                Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        googleMap?.setOnCameraIdleListener {
            val currentZoom = googleMap?.cameraPosition?.zoom ?: 0f

            if (currentZoom > 5f) {
                googleMap?.moveCamera(CameraUpdateFactory.zoomTo(5f))
            }
        }
    }

    private fun datePickerDialog(location: Coordinate) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }
                timePickerDialog(selectedDate, location)
            },
            year, month, day
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    private fun timePickerDialog(selectedDate: Calendar, location: Coordinate) {
        val hour = selectedDate.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDate.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                selectedDate.apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                if (selectedDate.timeInMillis <= System.currentTimeMillis()) {
                    Toast.makeText(requireContext(), "Select a valid time", Toast.LENGTH_SHORT)
                        .show()
                    Snackbar.make(
                        requireView(),
                        "Please select a future time",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    // Save the selected date, time, and location in the bundle
                    selectedDateTime = selectedDate
                    saveAlarmLocation(location, selectedDateTime!!)
                }
            },
            hour, minute, false
        ).show()
    }

    private fun saveAlarmLocation(location: Coordinate, selectedDateTime: Calendar) {
        context?.let { safeContext ->
            val intent = Intent(safeContext, AlarmHelper::class.java).apply {
                putExtra("lat", location.latitude)
                putExtra("long", location.longitude)
                putExtra("cityName", "City Name")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                safeContext, selectedDateTime.timeInMillis.toInt(), intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager = safeContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                selectedDateTime.timeInMillis,
                pendingIntent
            )
            val cityname = getCityName(requireContext(), location.latitude, location.longitude)
            val bundle = Bundle().apply {
                putDouble("latitude", location.latitude)
                putDouble("longitude", location.longitude)
                putString("cityName", cityname)
                putLong("alarmTime", selectedDateTime.timeInMillis)
            }


            lifecycleScope.launch(Dispatchers.IO) {
                val result =
                    AlertPojo(
                        selectedDateTime.timeInMillis.toInt(),
                        cityname?: "  ",
                        selectedDateTime.time.toString(),
                        location.latitude?: 0.0,
                        location.longitude?: 0.0
                    )
                alertViewModel.insertAlert(result)

                Log.d("TAG2", "scheduleAlarm:$result ")
                Log.d("selected", "selected:${selectedDateTime.timeInMillis.toInt()} ")


            }

            findNavController().navigate(R.id.alert, bundle)

            Log.d("AlarmLocation", "saveAlarmLocation: $bundle")
        }
    }

    fun getCityName(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                return address.locality
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return " city"
    }
}
