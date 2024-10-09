package com.example.wetherapp.ui.fav.view

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentDashboardBinding
import com.example.wetherapp.db.LocalDaoImplementation
import com.example.wetherapp.db.LocalDatabase
import com.example.wetherapp.db.PlaceFavPojo
import com.example.wetherapp.model.RepoImplementation
import com.example.wetherapp.network.ImplementNetworkResponse
import com.example.wetherapp.network.RetrofitHelper
import com.example.wetherapp.ui.fav.viewmodel.FavoriteFactory
import com.example.wetherapp.ui.fav.viewmodel.FavoriteViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class FavoriteFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteFactory(
            RepoImplementation.getInstance(
                ImplementNetworkResponse.getInstance(RetrofitHelper.retrofit),
                LocalDaoImplementation(LocalDatabase.getInstance(requireContext()))
            )
        )
    }

    private lateinit var adapterFavorite: AdapterFavorite

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupRadioButtonListener()

        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")
        Log.d("latitude", "onViewCreated2323443:$latitude ")

        if (latitude != null && longitude != null) {

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val cityName = if (addresses != null && addresses.isNotEmpty()) {
                    addresses[0].locality ?: "Unknown Location"
                } else {
                    "Unknown Location"
                }

                val place = PlaceFavPojo(id = 0, cityName = cityName, latitude = latitude, longitude = longitude)

                favoriteViewModel.insertToFav(place)

            } catch (e: Exception) {
                Log.e("GeocoderError", "Error retrieving location name", e)
            }
        }
    }

    private fun setupRecyclerView() {
        adapterFavorite = AdapterFavorite(
            onDeleteClick = { place -> showDeleteConfirmationDialog(place) },
            onItemClick = { place ->  }
        )
        binding.rvFavourite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavourite.adapter = adapterFavorite
    }

    private fun showDeleteConfirmationDialog(place: PlaceFavPojo) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this favorite place?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            favoriteViewModel.deleteFromFav(place)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            favoriteViewModel.favouritePlaces.collect { places ->
                adapterFavorite.submitList(places)
            }
        }
    }

    private fun setupRadioButtonListener() {
        binding.addFav.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.mapsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
