package com.cesararellano.focuscode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var placeLocationCoordinates: LatLng

    override fun onStart() {
        super.onStart()
        initLoadAds()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        var placeLocation: String = intent.getStringExtra("PLACE_LOCATION") ?: "geo:-25.363,131.044"

        if( !placeLocation.contains("geo") ) {
            placeLocation = "geo:-25.363,131.044"
        }

        placeLocationCoordinates = getLatLng(placeLocation)
        settingsActionBar()
        createFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when( item.itemId ) {
            R.id.placeLocationItem -> {
                moveGoogleMapsCamera(placeLocationCoordinates)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun settingsActionBar() {
        val actionBar = supportActionBar
        actionBar?.title ="Mapa"
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker( placeLocationCoordinates )
    }

    private fun createMarker(coordinates: LatLng) {
        val marker = MarkerOptions().position(coordinates).title("Lugar escaneado")
        map.addMarker(marker)
        moveGoogleMapsCamera(coordinates)
    }

    private fun getLatLng(scanCode:String): LatLng {
        val latLng = scanCode.substring(4).split(',')
        val lat = latLng[0].toDouble()
        val lng = latLng[1].toDouble()
        return LatLng(lat, lng)
    }

    private fun moveGoogleMapsCamera(coordinates: LatLng) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f), // Zoom in floats
            800,
            null
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initLoadAds() {
        val mapBanner = findViewById<AdView>(R.id.mapBanner)
        val adRequest = AdRequest.Builder().build()
        mapBanner.loadAd(adRequest)
    }
}