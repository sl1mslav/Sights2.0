package com.example.a18hw.presentation.maps

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a18hw.data.Repository
import com.example.a18hw.data.services.CloseLocationDto
import com.example.a18hw.entity.State
import com.example.a18hw.entity.UserLocationData
import com.example.a18hw.utils.LauncherUtils
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.create
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsFragmentViewModel @Inject constructor(
    private val app: Application,
    private val repository: Repository
) : ViewModel(), DefaultLifecycleObserver {
    private lateinit var fusedClient: FusedLocationProviderClient

    private val _permissions = MutableStateFlow<State>(State.PermissionsNotGranted)
    val permissions = _permissions.asStateFlow()

    private val _userLocation = MutableStateFlow<UserLocationData?>(null)
    private val _nearestSightsToUser = MutableStateFlow<List<CloseLocationDto>?>(null)

    val userAndNearestSights =
        _userLocation.combine(_nearestSightsToUser) { userLoc, nearestSights ->
            Pair(userLoc, nearestSights)
        }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val longitude = (p0.lastLocation?.longitude ?: 55).toDouble()
            val latitude = (p0.lastLocation?.latitude ?: 55).toDouble()
            setUserLocation(longitude, latitude)
            getNearestLocationsFromRepo(latitude, longitude)
        }
    }

    fun setUserLocation(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            _userLocation.value = UserLocationData(longitude, latitude)
        }
    }

    fun getNearestLocationsFromRepo(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _nearestSightsToUser.value = repository.getCloseLocationsFromAPI(latitude, longitude)
        }
    }

    fun checkMapsFragmentPermissions() {
        LauncherUtils.checkPermissions(
            app.applicationContext,
            LauncherUtils.MapsFragmentPermissions.REQUIRED_PERMISSIONS,
            onGranted = { _permissions.value = State.AllPermissionsGranted },
            onNotGranted = { _permissions.value = State.PermissionsNotGranted }
        )
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        fusedClient = LocationServices.getFusedLocationProviderClient(app.applicationContext)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        fusedClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    fun startLocation() {
        checkMapsFragmentPermissions()
        val request = create().setInterval(1000).setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}