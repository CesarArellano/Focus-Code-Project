package com.cesararellano.focuscode

import android.content.Context
import android.content.SharedPreferences
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
    // Declarando variables.
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var map: GoogleMap
    private lateinit var placeLocationCoordinates: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Recibimos el extra con las coordenadas del mapa.
        val defaultCoors = "geo:-25.363,131.044"
        var placeLocation = intent.getStringExtra("PLACE_LOCATION") ?: defaultCoors

        // Validación si es diferente de http o geo.
        if( !placeLocation.contains("geo") ) {
            placeLocation = defaultCoors
        }

        // Obtenemos las coordenadas.
        placeLocationCoordinates = getLatLng(placeLocation)
        // Obtenemos una instancia para modificar los sharedPreferences, para ser usado en onStart.
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        settingsActionBar()
        createFragment()
    }

    // Inicializando la carga de anuncios en modo test en esta Activity.
    override fun onStart() {
        super.onStart()
        val premiumMode = sharedPreferences.getBoolean("premiumMode", false)
        if( !premiumMode ) initLoadAds()
    }


    // Configura el actionBar con un nuevo título y la posibilidad para poder de regresar al Main Activity.
    private fun settingsActionBar() {
        val actionBar = supportActionBar
        actionBar?.title ="Mapa"
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Crea el Fragment de Google Maps.
    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Inicializa el mapa con la coordenadas obtenidas en OnCreate.
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker( placeLocationCoordinates )
    }

    // Con esta función creamos el marcador del lugar escaneado y ponemos un título por defecto en el mismo marcador.
    private fun createMarker(coordinates: LatLng) {
        val marker = MarkerOptions().position(coordinates).title("Lugar escaneado")
        map.addMarker(marker)
        moveGoogleMapsCamera(coordinates)
    }

    // Función que convierte nuestras coordenadas de String a la clase LatLng de Google Maps.
    private fun getLatLng(scanCode:String): LatLng {
        val latLng = scanCode.substring(4).split(',')
        val lat = latLng[0].toDouble()
        val lng = latLng[1].toDouble()
        return LatLng(lat, lng)
    }

    // Esta función nos permite reubicar al usuario en las coordenadas del lugar.
    private fun moveGoogleMapsCamera(coordinates: LatLng) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f), // Zoom in floats
            800,
            null
        )
    }

    // Inicializando la carga de anuncios en modo test en esta Activity, el banner es distinto al de MainActivity.
    private fun initLoadAds() {
        val mapBanner = findViewById<AdView>(R.id.mapBanner)
        val adRequest = AdRequest.Builder().build()
        mapBanner.loadAd(adRequest)
    }

    // Habilita la opción de poder regresar al MainActivity.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Creación del menú para reubicar en las coordenadas del lugar escaneado.
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
}