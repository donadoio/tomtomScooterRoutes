package io.donado.sfroutes

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionsHelper (context: Context) {
    private val myContext = context;

    fun areLocationPermissionsGranted() = ContextCompat.checkSelfPermission(
        myContext,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        myContext,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

}

