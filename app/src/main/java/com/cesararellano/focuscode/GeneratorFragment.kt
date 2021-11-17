package com.cesararellano.focuscode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

// Este fragmento es el encargado de generar ambos códigos, a partir de lo que pone el usuario en el EditText.
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
        val view = inflater.inflate(R.layout.fragment_generator, container, false)

        // Se establece la referencia de los elementos de la UI.
        textToConvertEditText = view.findViewById(R.id.textToConvertEditText)
        generatorButton = view.findViewById(R.id.generatorButton)
        shareButton = view.findViewById(R.id.shareButton)
        qrCodeImage = view.findViewById(R.id.qrCodeImage)
        barcodeImage = view.findViewById(R.id.barcodeImage)

        shareButton.isEnabled = false

        // Este botón será el encargado de generar los códigos y pintarlos en la vista.
        generatorButton.setOnClickListener{
            it.hideKeyboard() // Esconde el teclado
            if( textToConvertEditText.text.isNotEmpty() ) {
                getCode() // Genera los códigos
                shareButton.isEnabled = true // Habilita la opción de compartir.
            }
        }

        // Poder compartir los códigos en otras apps del teléfono.
        shareButton.setOnClickListener {
            shareScreenshot()
        }


        return view
    }

    // Genera los códigos (QR y Barcode)
    private fun getCode() {
        try {
            val qrBitmap = generateCode(BarcodeFormat.QR_CODE, 225,255 )
            qrCodeImage.setImageBitmap(qrBitmap)
            val barcodeBitmap = generateCode(BarcodeFormat.CODE_128, 250, 100)
            barcodeImage.setImageBitmap(barcodeBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Función encargada de generar ambos códigos, se le pasa el codeFormat para saber cómo va a genera el Bitmap junto con sus medidas (ancho y alto).
    private fun generateCode( codeFormat:BarcodeFormat, width: Int, height: Int ):Bitmap {
        val textToConvert = textToConvertEditText.text.toString()
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix: BitMatrix = multiFormatWriter.encode(textToConvert, codeFormat, width, height)
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.createBitmap(bitMatrix)
    }

    // Función utilizada para tomar un screenshot de los códigos (QR y Barcode) y para compartir la imagen generada con otras apps que tenga instalada el teléfono.
    private fun shareScreenshot() {
        try {
            val imageUri = takeScreenshot() // Devuelve el Uri donde se encuentra el screenshot.
            val shareIntent = Intent().apply { // Intent configurado para compartir el screenshot en otras apps.
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                flags =  Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = "image/jpeg"
            }
            startActivity( Intent.createChooser(shareIntent, "Compartiendo código") ) // Inicia el Intent.
        } catch (e: Exception) { // Si algo malo sucede, mostrará un Toast.
            e.printStackTrace()
            Toast.makeText( requireContext(),"Ocurrió un error al compartir códigos", Toast.LENGTH_LONG).show()
        }

    }

    // Función que toma un screenshot de los códigos, y retonar el Uri donde se almacena dicho screenshot.
    private fun takeScreenshot(): Uri {

        val containerView = requireView().findViewById<ConstraintLayout>(R.id.codeContainer)
        val bitmap = Bitmap.createBitmap(containerView.width, containerView.height, Bitmap.Config.ARGB_8888)

        // Convertimos un bitmap a un Canvas para modificar su estilos.
        val canvas = Canvas(bitmap)

        // El background del containerView se pinta de blanco.
        canvas.drawColor(Color.WHITE)
        containerView.draw(canvas)

        // Proceso de compresión en JPEG y combinación del Bitmap con el photoPath.
        val screenshotFile = getScreenshotFile()
        val fOutStream = FileOutputStream(screenshotFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream)
        fOutStream.flush()
        fOutStream.close()

        return FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".provider",  //(app signature + ".provider" )
            screenshotFile
        )
    }

    // Esta función retorna un File para el screenshot
    private fun getScreenshotFile(): File {
        val photoPath = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(photoPath, "codes.jpeg")
    }

    // Función útil para ocultar el teclado.
    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}