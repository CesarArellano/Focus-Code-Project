package com.cesararellano.focuscode

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
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
            getCode()
            shareButton.isEnabled = true
        }

        shareButton.setOnClickListener {
            println("Shared Button click")
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
        val bitMatrix: BitMatrix = multiFormatWriter.encode(textToConvert, BarcodeFormat.QR_CODE, 250, 250)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        qrCodeImage.setImageBitmap(bitmap)
    }

    private fun generateBarcode() {
        val textToConvert = textToConvertEditText.text.toString()
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix: BitMatrix = multiFormatWriter.encode(textToConvert, BarcodeFormat.CODE_128, 250, 125, null)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        barcodeImage.setImageBitmap(bitmap)
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}