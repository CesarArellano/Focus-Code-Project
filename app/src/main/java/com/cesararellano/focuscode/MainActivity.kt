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
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.historyFragment, R.id.scannerFragment, R.id.generatorFragment, R.id.settingsFragment)
        )

        setupActionBarWithNavController( navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

    }

    override fun onStart() {
        super.onStart()
        val userTheme = sharedPreferences.getInt("userTheme", 0)
        val premiumMode = sharedPreferences.getBoolean("premiumMode", false)

        AppCompatDelegate.setDefaultNightMode(userTheme)

        if( !premiumMode ) initLoadAds()
    }

    private fun initLoadAds() {
        val mainBanner = findViewById<AdView>(R.id.mainBanner)
        val adRequest = AdRequest.Builder().build()
        mainBanner.loadAd(adRequest)
    }

}