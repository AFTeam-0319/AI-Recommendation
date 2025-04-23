package com.example.mynewcode2.ui.view.location

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.map.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.example.mynewcode2.data.Place
import kotlin.math.round

@Composable
fun MapScreen(places: List<Place>) {
    AndroidView(factory = { context ->
        val mapView = MapView(context)
        mapView.onCreate(Bundle())
        mapView.getMapAsync { naverMap ->
            places.forEach { place ->
                try {
                    val lat = place.coordinate2.toDouble()
                    val lng = place.coordinate1.toDouble()
                    val marker = Marker().apply {
                        position = LatLng(lat, lng)
                        captionText = place.name
                        map = naverMap
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // 좌표 오류 시 무시
                }
            }
        }
        mapView
    })
}
