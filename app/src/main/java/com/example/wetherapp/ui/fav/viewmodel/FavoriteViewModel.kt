package com.example.wetherapp.ui.fav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetherapp.db.PlaceFavPojo
import com.example.wetherapp.model.Reposatory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val reposatory: Reposatory) : ViewModel() {

    private val _favouritePlaces: MutableStateFlow<List<PlaceFavPojo>> =
        MutableStateFlow(emptyList())
    val favouritePlaces: StateFlow<List<PlaceFavPojo>> = _favouritePlaces

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getFavoritePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                reposatory.getAllFavouritePlaces().collect {
                    _favouritePlaces.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteFromFav(place: PlaceFavPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            reposatory.deletePlaceFromFav(place)
        }
    }

    fun insertToFav(place: PlaceFavPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            reposatory.insertPlaceToFav(place)
        }
    }
}
