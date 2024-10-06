package com.example.wetherapp.ui.home.view

import WeatherAdapterHours
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentHomeBinding
import com.example.wetherapp.location.Location
import com.example.wetherapp.model.RepoImplementation
import com.example.wetherapp.model.forecast.MainEnum
import com.example.wetherapp.model.forecast.extractDailyWeatherData
import com.example.wetherapp.model.forecast.extractWeatherData
import com.example.wetherapp.network.ImplementNetworkResponse
import com.example.wetherapp.network.RetrofitHelper
import com.example.wetherapp.ui.home.viewmodel.HomeFactory
import com.example.wetherapp.ui.home.viewmodel.HomeViewModel
import com.google.android.gms.location.LocationServices


import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var location: Location
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    lateinit var weatherAdapterHours: WeatherAdapterHours
    private lateinit var weatherAdapterDays: WeatherAdapterDays
    private val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        location = Location.getInstance(fusedLocationProviderClient, requireContext())
        checkAndRequestPermissions()

        weatherAdapterHours = WeatherAdapterHours()
        binding.recyclerViewHours.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = weatherAdapterHours
        }
        weatherAdapterDays = WeatherAdapterDays()
        binding.recyclerviewDays.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weatherAdapterDays
        }

        return binding.root
    }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeFactory(
            RepoImplementation.getInstance(
                ImplementNetworkResponse.getInstance(RetrofitHelper.retrofit)
            )
        )
    }


    private fun checkAndRequestPermissions() {
        if (checkLocationPermissions()) {
            fetchLocationAndWeather()
        } else {
            requestLocationPermissions()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                fetchLocationAndWeather()
            } else {

            }
        }
    }

    private fun fetchLocationAndWeather() {
        viewLifecycleOwner.lifecycleScope.launch {
            location.getCurrentLocation().collect { coordinate ->
                _binding?.let { binding ->
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)

                    val cityName = if (!addresses.isNullOrEmpty()) {
                        addresses[0].locality ?: addresses[0].subAdminArea ?: "Unknown City"
                    } else {
                        "Unknown City"
                    }

                    binding.tvCityName.text = cityName

                    // Fetch weather data using coordinates
                    homeViewModel.getWeatherData(
                        lat = coordinate.latitude,
                        lon = coordinate.longitude,
                        language = "en",
                        units = "metric"
                    )
                    homeViewModel.getForecast(
                        lat = coordinate.latitude,
                        lon = coordinate.longitude,
                        language = "en",
                        units = "metric"
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.weatherData.collect { currentWeather ->
                _binding?.let { binding ->
                    currentWeather?.let {
                        binding.tvDate.text = formatDate(date.format(Date()))
                        binding.tvDegree.text = "${it.main.temp}°C"
                        binding.tvStatus.text = it.weather[0].description
                        val weatherStatus = currentWeather.weather[0].main
                        val weatherIcon = when (weatherStatus) {
                            MainEnum.Clear.toString() -> R.drawable.sun
                            MainEnum.Clouds.toString() -> R.drawable.cloud
                            MainEnum.Rain.toString() -> R.drawable.rain
                            else -> {
                                R.drawable.sun
                            }
                        }
                        binding.imageStatus.setImageResource(weatherIcon)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.isLoading.collect { isLoading ->
                _binding?.let { binding ->
                    if (isLoading) {

                    } else {

                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.weatherForecast.collect { forecast ->
                forecast?.let {
                    val hours = extractWeatherData(it)
                    weatherAdapterHours.submitList(hours)
                    Log.d("TAGh", "fetchLocationAndWeather:$hours ")

                    val daysList = extractDailyWeatherData(it.list)
                    Log.d("TAG", "fetchLocationAndWeather:${it.list} ")
                    weatherAdapterDays.submitList(daysList)
                    Log.d("TAG1", "fetchLocationAndWeather:$daysList ")
                }
            }
        }

    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
