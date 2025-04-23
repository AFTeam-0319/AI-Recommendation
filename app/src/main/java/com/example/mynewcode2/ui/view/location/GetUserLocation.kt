package com.example.mynewcode2.ui.view.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Composable
fun GetUserLocation(onLocationReceived: (Location?) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        onLocationReceived(location)
                    }
            }
        }
    )

    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    // 위치 권한 확인
    if (ActivityCompat.checkSelfPermission(
            context, locationPermission) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                onLocationReceived(location)
            }
    } else {
        // 권한 요청
        permissionLauncher.launch(locationPermission)
    }
}
