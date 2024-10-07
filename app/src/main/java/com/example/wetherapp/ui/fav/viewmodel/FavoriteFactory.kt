package com.example.wetherapp.ui.fav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wetherapp.model.Reposatory


class FavoriteFactory(private val reposatory: Reposatory): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
                FavoriteViewModel(reposatory) as T
            } else {
                throw java.lang.IllegalArgumentException("ViewModel Class not found")
            }
        }
    }
