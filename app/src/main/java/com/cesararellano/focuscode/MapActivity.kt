package com.cesararellano.focuscode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var placeLocation: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        placeLocation = intent.getStringExtra("PLACE_LOCATION") ?: "geo:-25.363,131.044"
        println(placeLocation)
        createFragment( )

    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker( getLatLng(placeLocation) )
    }

    private fun createMarker(coordinates: LatLng) {
        val marker = MarkerOptions().position(coordinates).title("Lugar escaneado")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f), // Zoom in floats
            1000,
            null
        )
    }

    private fun getLatLng(scanCode:String): LatLng {
        val latLng = scanCode.substring(4).split(',')
        val lat = latLng[0].toDouble()
        val lng = latLng[1].toDouble()
        return LatLng(lat, lng)
    }
}