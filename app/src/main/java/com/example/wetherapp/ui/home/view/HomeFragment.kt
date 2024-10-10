package com.example.wetherapp.ui.home.view

import WeatherAdapterHours
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.ConstantValue
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentHomeBinding
import com.example.wetherapp.db.LocalDaoImplementation
import com.example.wetherapp.db.LocalDatabase
import com.example.wetherapp.location.Location
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.RepoImplementation
import com.example.wetherapp.model.forecast.MainEnum
import com.example.wetherapp.model.forecast.extractDailyWeatherData
import com.example.wetherapp.network.ImplementNetworkResponse
import com.example.wetherapp.network.RetrofitHelper
import com.example.wetherapp.network.State
import com.example.wetherapp.ui.home.viewmodel.HomeFactory
import com.example.wetherapp.ui.home.viewmodel.HomeViewModel
import com.google.android.gms.location.LocationServices
import extractWeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
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

    private var isFromFavorite: Boolean = false
    lateinit var sharedPreferences: SharedPreferences
    var temp: String? = null
    private val homeViewModel: HomeViewModel by viewModels {
        HomeFactory(
            RepoImplementation.getInstance(
                ImplementNetworkResponse.getInstance(RetrofitHelper.retrofit),
                LocalDaoImplementation(LocalDatabase.getInstance(requireContext()))
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(
            ConstantValue.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        temp = sharedPreferences.getString(
            ConstantValue.UNITS_KEY,
            ConstantValue.SupportedUnits.Standard.toString()
        ) ?: ConstantValue.SupportedUnits.Standard.toString()

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

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        location = Location.getInstance(fusedLocationProviderClient, requireContext())

        checkAndRequestPermissions()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityName = arguments?.getString("cityName")
        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")

        if (cityName != null && latitude != null && longitude != null) {
            binding.tvCityName.text = cityName
            isFromFavorite = true

            if (temp == ConstantValue.SupportedUnits.Standard.toString()) {
                homeViewModel.getWeatherData(
                    lat = latitude,
                    lon = longitude,
                    language = "en",
                    units = "standard"
                )
                homeViewModel.getForecast(
                    lat = latitude,
                    lon = longitude,
                    language = "en",
                    units = "standard"
                )
            } else if (temp == ConstantValue.SupportedUnits.Imperial.toString()) {
                homeViewModel.getWeatherData(
                    lat = latitude,
                    lon = longitude,
                    language = "en",
                    units = "imperial"
                )
                homeViewModel.getForecast(
                    lat = latitude,
                    lon = longitude,
                    language = "en",
                    units = "imperial"
                )
            } else {
                homeViewModel.getWeatherData(
                    lat = latitude,
                    lon = longitude,
                    language = "en",
                    units = "metric"
                )
                homeViewModel.getForecast(
                    lat = latitude,
                    lon = longitude,
                    language = "en",
                    units = "metric"
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.weatherData.collectLatest { state ->
                    when (state) {
                        is State.Loading -> {

                        }

                        is State.Success -> {
                            val currentWeather = state.data
                            _binding?.let { binding ->
                                binding.tvDate.text = formatDate(date.format(Date()))
                                binding.tvDegree.text =
                                   currentWeather.main.temp.toString()
                                binding.tvStatus.text = currentWeather.weather[0].description

                                val weatherStatus = currentWeather.weather[0].main
                                val weatherIcon = when (weatherStatus) {
                                    MainEnum.Clear.toString() -> R.drawable.sun
                                    MainEnum.Clouds.toString() -> R.drawable.cloud
                                    MainEnum.Rain.toString() -> R.drawable.rain
                                    else -> R.drawable.sun
                                }
                                binding.imageStatus.setImageResource(weatherIcon)
                            }
                        }

                        is State.Error -> {
                            Toast.makeText(
                                context,
                                "There was an issue with the internet connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                homeViewModel.weatherForecast.collectLatest { state ->
                    when (state) {
                        is State.Success -> {
                            val forecast = state.data
                            val hours = extractWeatherData(forecast)
                            weatherAdapterHours.submitList(hours)

                            val daysList = extractDailyWeatherData(forecast.list)
                            weatherAdapterDays.submitList(daysList)
                        }

                        is State.Loading -> {

                        }

                        is State.Error -> {
                            Toast.makeText(
                                context,
                                "There was an issue with the internet connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            isFromFavorite = false
            checkAndRequestPermissions()
            fetchLocationAndWeather()
        }
    }

    private fun checkAndRequestPermissions() {
        if (checkLocationPermissions() && !isFromFavorite) {
           // fetchLocationAndWeather()
        } else {
            if (!isFromFavorite) {
                requestLocationPermissions()
            }
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
                if (!isFromFavorite) {
                    fetchLocationAndWeather()
                }
            } else {

            }
        }
    }


    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }

    private fun fetchLocationAndWeather() {
        if (isConnected(requireContext())) {
            viewLifecycleOwner.lifecycleScope.launch {
                location.getCurrentLocation().collectLatest { coordinate ->
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
                        when (temp) {
                            ConstantValue.SupportedUnits.Standard.toString() -> {
                                homeViewModel.getWeatherData(
                                    lat = coordinate.latitude,
                                    lon = coordinate.longitude,
                                    language = "en",
                                    units = "Standard"
                                )
                                homeViewModel.getForecast(
                                    lat = coordinate.latitude,
                                    lon = coordinate.longitude,
                                    language = "en",
                                    units = "Standard"
                                )
                            }
                            ConstantValue.SupportedUnits.Imperial.toString() -> {
                                homeViewModel.getWeatherData(
                                    lat = coordinate.latitude,
                                    lon = coordinate.longitude,
                                    language = "en",
                                    units = "imperial"
                                )
                                homeViewModel.getForecast(
                                    lat = coordinate.latitude,
                                    lon = coordinate.longitude,
                                    language = "en",
                                    units = "imperial"
                                )
                            }
                            else -> {
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
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.weatherData.collectLatest { state ->
                    when (state) {
                        is State.Loading -> {

                        }

                        is State.Success -> {
                            val currentWeather = state.data
                            _binding?.let { binding ->
                                binding.tvDate.text = formatDate(date.format(Date()))
                                binding.tvDegree.text = "${currentWeather.main.temp}°"
                                binding.tvStatus.text = currentWeather.weather[0].description

                                val weatherStatus = currentWeather.weather[0].main
                                val weatherIcon = when (weatherStatus) {
                                    MainEnum.Clear.toString() -> R.drawable.sun
                                    MainEnum.Clouds.toString() -> R.drawable.cloud
                                    MainEnum.Rain.toString() -> R.drawable.rain
                                    else -> R.drawable.sun
                                }
                                binding.imageStatus.setImageResource(weatherIcon)
                            }

                            saveWeatherDataToSharedPrefs(currentWeather)
                        }

                        is State.Error -> {
                            Toast.makeText(context, "There was an issue with the internet connection", Toast.LENGTH_SHORT).show()
                            loadWeatherDataFromSharedPrefs()
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                homeViewModel.weatherForecast.collectLatest { state ->
                    when (state) {
                        is State.Success -> {
                            val forecast = state.data
                            val hours = extractWeatherData(forecast)
                            weatherAdapterHours.submitList(hours)

                            val daysList = extractDailyWeatherData(forecast.list)
                            weatherAdapterDays.submitList(daysList)
                        }
                        is State.Loading -> {

                        }
                        is State.Error -> {
                            Toast.makeText(context, "There was an issue with the internet connection", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {

            loadWeatherDataFromSharedPrefs()
        }
    }

    private fun saveWeatherDataToSharedPrefs(currentWeather: Current) {
        val editor = sharedPreferences.edit()
        editor.putString("cityName", binding.tvCityName.text.toString())
        editor.putString("date", binding.tvDate.text.toString())
        editor.putString("degree", binding.tvDegree.text.toString())
        editor.putString("status", binding.tvStatus.text.toString())
        editor.apply()
    }

    private fun loadWeatherDataFromSharedPrefs() {
        val cityName = sharedPreferences.getString("cityName", "Unknown City")
        val date = sharedPreferences.getString("date", "")
        val degree = sharedPreferences.getString("degree", "")
        val status = sharedPreferences.getString("status", "")

        _binding?.let { binding ->
            binding.tvCityName.text = cityName
            binding.tvDate.text = date
            binding.tvDegree.text = degree
            binding.tvStatus.text = status
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
