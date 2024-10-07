package com.example.wetherapp.ui.fav.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherapp.databinding.FragmentDashboardBinding
import com.example.wetherapp.db.FavouriteDaoImplementation
import com.example.wetherapp.db.FavouriteDatabase
import com.example.wetherapp.model.RepoImplementation
import com.example.wetherapp.network.ImplementNetworkResponse
import com.example.wetherapp.network.RetrofitHelper
import com.example.wetherapp.ui.fav.viewmodel.FavoriteFactory
import com.example.wetherapp.ui.fav.viewmodel.FavoriteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
class FavoriteFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteFactory(
            RepoImplementation.getInstance(
                ImplementNetworkResponse.getInstance(RetrofitHelper.retrofit),
                FavouriteDaoImplementation(FavouriteDatabase.getInstance(requireContext()))
            )
        )
    }

    private lateinit var adapterFavorite: AdapterFavorite

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapterFavorite = AdapterFavorite(
            onDeleteClick = { place ->
                favoriteViewModel.deleteFromFav(place)
            },
            onItemClick = { place ->
                // التعامل مع النقر على عنصر مفضل هنا
                // على سبيل المثال، يمكنك الانتقال إلى تفاصيل المكان
                // startActivity(Intent(requireContext(), DetailActivity::class.java).apply {
                //     putExtra("place_id", place.id)
                // })
            }
        )
        binding.rvFavourite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavourite.adapter = adapterFavorite
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            favoriteViewModel.favouritePlaces.collectLatest { places ->
                adapterFavorite.submitList(places)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
