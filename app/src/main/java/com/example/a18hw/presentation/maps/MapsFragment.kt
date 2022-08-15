package com.example.a18hw.presentation.maps

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.a18hw.R
import com.example.a18hw.databinding.FragmentMapsBinding
import com.example.a18hw.entity.State
import com.example.a18hw.utils.LauncherUtils
import com.example.a18hw.utils.LauncherUtils.getLauncher
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapsFragmentViewModel by viewModels()
    private val launcher = getLauncher { viewModel.startLocation() }

    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        with(mMap.uiSettings) {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
        }
        initMapUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycle.addObserver(viewModel)
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLauncherUpdates()
        viewModel.checkMapsFragmentPermissions()
    }

    private fun initLauncherUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.permissions.collect {
                    when (it) {
                        State.PermissionsNotGranted ->
                        {
                            launcher.launch(LauncherUtils.MapsFragmentPermissions.REQUIRED_PERMISSIONS)
                        }
                        State.AllPermissionsGranted -> {
                            initMap()
                            viewModel.startLocation()
                        }
                    }
                }
            }
        }
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun initMapUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAndNearestSights.collect {
                    val latitude = it.first?.latitude ?: 55
                    val longitude = it.first?.longitude ?: 55
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(
                                latitude.toDouble(),
                                longitude.toDouble()
                            )
                        )
                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(latitude.toDouble(), longitude.toDouble()), 18f
                    ))
                    it.second?.forEach {
                        mMap.addMarker(
                            MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .position(LatLng(it.point.latitude, it.point.longitude))
                                .title(it.name)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycle.removeObserver(viewModel)
        _binding = null
        super.onDestroy()
    }
}
