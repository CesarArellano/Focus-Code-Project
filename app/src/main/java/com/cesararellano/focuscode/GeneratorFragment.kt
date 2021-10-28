package com.cesararellano.focuscode

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

// Este fragmento es el encargado de generar ambos códigos, a partir de lo que pone el usuario en el EditText.
private const val TAG = "GeneratorFragment"
class GeneratorFragment : Fragment() {
    // Se declaran las variables de la UI.
    private lateinit var textToConvertEditText: EditText
    private lateinit var generatorButton: Button
    private lateinit var shareButton: Button
    private lateinit var qrCodeImage: ImageView
    private lateinit var barcodeImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val generatorView = inflater.inflate(R.layout.fragment_generator, container, false)

        // Se establece la referencia de los elementos de la UI.
        textToConvertEditText = generatorView.findViewById(R.id.textToConvertEditText)
        generatorButton = generatorView.findViewById(R.id.generatorButton)
        shareButton = generatorView.findViewById(R.id.shareButton)
        qrCodeImage = generatorView.findViewById(R.id.qrCodeImage)
        barcodeImage = generatorView.findViewById(R.id.barcodeImage)

        shareButton.isEnabled = false

        // TODO: Generar códigos
        // Este botón será el encargado de generar los códigos y pintarlos en la vista.
        generatorButton.setOnClickListener{
            it.hideKeyboard()
            if( textToConvertEditText.text.isNotEmpty() ) {
                Log.d(TAG, "Clicked on generatorButton")
                shareButton.isEnabled = true
            }
        }

        // TODO: Poder compartir los códigos en otras apps del teléfono.
        shareButton.setOnClickListener {
            Log.d(TAG, "Clicked on shareButton")
        }

        return generatorView
    }

    // Función útil para ocultar el teclado.
    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}