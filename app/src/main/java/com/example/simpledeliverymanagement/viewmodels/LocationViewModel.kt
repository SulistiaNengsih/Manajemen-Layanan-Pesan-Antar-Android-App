package com.example.simpledeliverymanagement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.location.Location

class LocationViewModel : ViewModel() {
    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> get() = _currentLocation

    fun updateLocation(location: Location) {
        _currentLocation.value = location
    }
}