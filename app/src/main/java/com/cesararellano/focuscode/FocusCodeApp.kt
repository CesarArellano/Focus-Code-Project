package com.cesararellano.focuscode

import android.app.Application
import com.google.android.gms.ads.MobileAds

class FocusCodeApp:Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}