package com.example.wetherapp.ui.map

import android.app.AlertDialog
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentMapsSettingBinding
import com.example.wetherapp.db.PlaceFavPojo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapsSetting : Fragment() {

    private lateinit var binding: FragmentMapsSettingBinding
    private var marker: Marker? = null
    private var googleMap: GoogleMap? = null
    private var place: PlaceFavPojo? = null
    private val callback = OnMapReadyCallback { googleMapInstance ->
        googleMap = googleMapInstance
        googleMap?.setOnMapClickListener { latLng ->
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            val cityName = if (addresses != null && addresses.isNotEmpty()) {
                addresses[0].locality ?: "Unknown Location"
            } else {
                "Unknown Location"
            }
            place = PlaceFavPojo(id, cityName, latLng.latitude, latLng.longitude)
            marker?.remove()
            marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(cityName)
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
        binding = FragmentMapsSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapSetting) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.btnSaveLocation.setOnClickListener {
            place?.let { placeData ->

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Save Location")
                    .setMessage("Are you sure you want to save this location?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val bundle = Bundle().apply {
                            putDouble("latitude", placeData.latitude)
                            putDouble("longitude", placeData.longitude)
                            putString(
                                "cityName",
                                placeData.cityName
                            )
                        }
                        findNavController().navigate(R.id.navigation_home, bundle)
                        Log.d("bundlesetting", "onViewCreated:$bundle ")
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

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
}
