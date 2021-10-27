package com.cesararellano.focuscode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurando barra de navegación inferior para poder navegar entre los distintos fragments.
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Instanciamos el controller de la barra de navegación para controlar las vistas.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Esta opción cambia de manera dinámica los labels del action bar.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.historyFragment, R.id.scannerFragment, R.id.generatorFragment, R.id.settingsFragment)
        )

        // Finalizando configuración.
        setupActionBarWithNavController( navController, appBarConfiguration )
        bottomNavigationView.setupWithNavController(navController)
    }

    // En este punto del ciclo de vida se inicializan los anuncios.
    override fun onStart() {
        super.onStart()
        initLoadAds()
    }

    // Inicializando la carga de anuncios en modo test en esta Activity.
    private fun initLoadAds() {
        val mainBanner = findViewById<AdView>(R.id.mainBanner)
        val adRequest = AdRequest.Builder().build()
        mainBanner.loadAd(adRequest)
    }

    // TODO: Mover lógica del menú sólo al HistoryFragment.
    // Inflando opción del menú.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_fragment_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Estableciendo acciones del botón.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when( item.itemId ) {
            R.id.deleteHistoryButton -> { // Acción que hará el botón de deleteHistory
                val database = AppDatabase.getDatabase(applicationContext)
                //Acción asíncrona (Corrutina) para eliminar los scans.
                CoroutineScope(Dispatchers.IO).launch {
                    database.scans().deleteAllScans()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}