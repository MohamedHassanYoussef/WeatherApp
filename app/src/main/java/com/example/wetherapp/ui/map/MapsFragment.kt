package com.example.wetherapp.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wetherapp.MainActivity
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentMapsBinding
import com.example.wetherapp.location.Coordinate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import android.app.AlertDialog
import com.google.android.gms.maps.GoogleMap

class MapsFragment : Fragment() {

    private lateinit var binding: FragmentMapsBinding
    private var coordinate: Coordinate? = null
    private var marker: Marker? = null
    private var googleMap: GoogleMap? = null

    override fun onStart() {
        super.onStart()
        val homeActivity = requireActivity() as MainActivity
        homeActivity.binding.navView.visibility = View.GONE
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.btnSaveLocation.setOnClickListener {
            coordinate?.let { coord ->

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Save Location")
                    .setMessage("Are you sure you want to save this location?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val bundle = Bundle().apply {
                            putDouble("latitude", coord.latitude)
                            putDouble("longitude", coord.longitude)
                        }
                        findNavController().navigate(R.id.favourit, bundle)
                        Log.d("bundle", "onViewCreated:$bundle ")
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

    override fun onDestroy() {
        super.onDestroy()
        val homeActivity = requireActivity() as MainActivity
        homeActivity.binding.navView.visibility = View.VISIBLE
    }
}
