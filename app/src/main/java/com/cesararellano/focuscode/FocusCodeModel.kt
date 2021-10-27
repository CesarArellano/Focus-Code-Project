package com.cesararellano.focuscode

import android.content.Context
import android.content.Intent
import android.net.Uri

class FocusCodeModel {

    fun goToMapActivity(context: Context, scanCode: String): Intent {
        val mapIntent = Intent(context, MapActivity::class.java).apply {
            putExtra("PLACE_LOCATION", scanCode)
        }

        return mapIntent
    }

    fun goToUrl(url: String): Intent {
        val uri: Uri = Uri.parse(url)
        return Intent(Intent.ACTION_VIEW, uri)
    }
}