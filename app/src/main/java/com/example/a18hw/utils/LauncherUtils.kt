package com.example.a18hw.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

object LauncherUtils {

    fun Fragment.getLauncher(actionOnSuccess: () -> Unit): ActivityResultLauncher<Array<String>> {
        return this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.values.all { it }) {
                actionOnSuccess()
            } else {
                Toast.makeText(requireContext(), "permissions were not granted", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            }
        }
    }

    object CameraFragmentPermissions {
        val REQUIRED_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    object MapsFragmentPermissions {
        val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun checkPermissions(
        appContext: Context,
        permissions: Array<String>,
        onGranted: () -> Unit,
        onNotGranted: () -> Unit
    ) {
        if (permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    appContext,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            onGranted()
        } else onNotGranted()
    }
}