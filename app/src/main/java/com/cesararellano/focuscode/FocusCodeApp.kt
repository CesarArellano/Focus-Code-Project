package com.cesararellano.focuscode

import android.app.Application
import com.google.android.gms.ads.MobileAds

// Clase ocupada para inicializar los MobileAds, dicha clase se llama desde el AndroidManifest (por eso sale el warning, pero si se ocupa).
class FocusCodeApp:Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}