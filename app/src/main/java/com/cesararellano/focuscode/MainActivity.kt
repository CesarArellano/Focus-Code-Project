package com.cesararellano.focuscode

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencia a la barra de navegación inferior para poder navegar entre los distintos fragments.
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Instanciamos el controller de la barra de navegación para controlar las vistas.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Esta opción cambia de manera dinámica los labels del action bar.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.historyFragment, R.id.scannerFragment, R.id.generatorFragment, R.id.settingsFragment)
        )

        // Finalizando configuración.
        setupActionBarWithNavController( navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)

        // Obtenemos instancia de los sharedPreferences
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

    }

    // En este punto del ciclo de vida se inicializan los anuncios y se obtienen las preferencis del usuario.
    override fun onStart() {
        super.onStart()
        val userTheme = sharedPreferences.getInt("userTheme", 0)
        val premiumMode = sharedPreferences.getBoolean("premiumMode", false)

        AppCompatDelegate.setDefaultNightMode(userTheme)

        if( !premiumMode ) initLoadAds()
    }

    // Inicializando la carga de anuncios en modo test en esta Activity.
    private fun initLoadAds() {
        val mainBanner = findViewById<AdView>(R.id.mainBanner)
        val adRequest = AdRequest.Builder().build()
        mainBanner.loadAd(adRequest)
    }

}