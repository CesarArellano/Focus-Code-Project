package com.cesararellano.focuscode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val placeLocation = intent.getStringExtra("PLACE_LOCATION")
        println("placeLocation: $placeLocation")
    }
}