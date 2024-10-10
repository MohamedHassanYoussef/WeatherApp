package com.example.wetherapp.ui.alert.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetherapp.db.AlertPojo
import com.example.wetherapp.model.Reposatory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val reposatory: Reposatory) : ViewModel() {

    private val _alerts: MutableStateFlow<List<AlertPojo>> = MutableStateFlow(emptyList())
    val alerts: StateFlow<List<AlertPojo>> = _alerts

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        getAllAlerts()
    }

    private fun getAllAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                reposatory.getAllAlerts().collect { alertList ->
                    _alerts.value = alertList
                    Log.d("TAGonreseve", "getAllAlerts: ${_alerts.value.lastIndex}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun insertAlert(alert: AlertPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            reposatory.insertAlert(alert)
            Log.d("TAGonreseve", "getAllAlertsxcvxcxv: ${alert.time}")

        }
    }

    fun deleteAlert(alert: AlertPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            reposatory.deleteAlert(alert)
        }
    }

}
