package com.cesararellano.focuscode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
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

class GeneratorFragment : Fragment() {
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

        textToConvertEditText = generatorView.findViewById(R.id.textToConvertEditText)
        generatorButton = generatorView.findViewById(R.id.generatorButton)
        shareButton = generatorView.findViewById(R.id.shareButton)
        qrCodeImage = generatorView.findViewById(R.id.qrCodeImage)
        barcodeImage = generatorView.findViewById(R.id.barcodeImage)

        shareButton.isEnabled = false
        generatorButton.setOnClickListener{
            it.hideKeyboard()
            if( textToConvertEditText.text.isNotEmpty() ) {
                getCode()
                shareButton.isEnabled = true
            }
        }

        shareButton.setOnClickListener {
            shareScreenshot()
        }


        return generatorView
    }

    private fun getCode() {
        try {
            generateQrCode()
            generateBarcode()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateQrCode() {
        val textToConvert = textToConvertEditText.text.toString()
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix: BitMatrix = multiFormatWriter.encode(textToConvert, BarcodeFormat.QR_CODE, 200, 200)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        qrCodeImage.setImageBitmap(bitmap)
    }

    private fun generateBarcode() {
        val textToConvert = textToConvertEditText.text.toString()
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix: BitMatrix = multiFormatWriter.encode(textToConvert, BarcodeFormat.CODE_128, 250, 100, null)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        barcodeImage.setImageBitmap(bitmap)
    }

    private fun takeScreenshot(): Uri {

        val containerView = requireView().findViewById<ConstraintLayout>(R.id.codeContainer)
        val bitmap = Bitmap.createBitmap(containerView.width, containerView.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(bitmap)
        //Get the view's background
        val bgDrawable = containerView.background
        if (bgDrawable != null) //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        else  //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        containerView.draw(canvas)
        val screenshotFile = File(requireContext().externalCacheDir, "codes.jpeg")
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
    private fun shareScreenshot() {
        try {
            val imageUri = takeScreenshot()
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                flags =  Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = "image/jpeg"
            }
            startActivity( Intent.createChooser(shareIntent, "Compartiendo código") )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Ocurrió un error al compartir códigos",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}