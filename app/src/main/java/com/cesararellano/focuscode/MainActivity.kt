package com.cesararellano.focuscode

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val userTheme = sharedPreferences.getInt("userTheme", 0)
        val premiumMode = sharedPreferences.getBoolean("premiumMode", false)

        editor.putBoolean("premiumMode", premiumMode)
        editor.apply()

        AppCompatDelegate.setDefaultNightMode(userTheme)

        Log.d(TAG, "premiumMode: $premiumMode")

        if( !premiumMode ) initLoadAds()
    }

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

    }

    private fun initLoadAds() {
        val mainBanner = findViewById<AdView>(R.id.mainBanner)
        val adRequest = AdRequest.Builder().build()
        mainBanner.loadAd(adRequest)
    }

}