package com.example.wetherapp.ui.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wetherapp.model.Reposatory


class AlertFactory(private val reposatory: Reposatory): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(reposatory) as T
        } else {
            throw java.lang.IllegalArgumentException("ViewModel Class not found")
        }
    }
}