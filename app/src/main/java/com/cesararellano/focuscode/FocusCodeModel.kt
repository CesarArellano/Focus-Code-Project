package com.cesararellano.focuscode

import android.content.Context
import android.content.Intent
import android.net.Uri

// Esta clase nos ayuda a centralizar métodos para redirigir al usuario dependiendo del tipo de scan.
class FocusCodeModel {

    // Redirige al usuario a Map Activity.
    fun goToMapActivity(context: Context, scanCode: String): Intent {
        val mapIntent = Intent(context, MapActivity::class.java).apply {
            putExtra("PLACE_LOCATION", scanCode)
        }
        return mapIntent
    }

    // Redirige al usuario a un Action View, para visualizar la página web.
    fun goToUrl(url: String): Intent {
        val uri: Uri = Uri.parse(url)
        return Intent(Intent.ACTION_VIEW, uri)
    }
}