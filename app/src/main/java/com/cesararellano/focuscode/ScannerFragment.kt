package com.cesararellano.focuscode

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

private const val CAMERA_REQUEST_CODE = 101
private const val TAG = "ScannerFragment"
class ScannerFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView = inflater.inflate(R.layout.fragment_scanner, container, false)

        setupPermissions(fragmentView)
        codeScanner(fragmentView)
        return fragmentView
    }

    private fun codeScanner(fragmentView:View) {
        val scannerView = fragmentView.findViewById<CodeScannerView>(R.id.scanner_view)
        val codeTextLabel = fragmentView.findViewById<TextView>(R.id.codeTextLabel)
        codeScanner = CodeScanner(fragmentView.context, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                (fragmentView.context as Activity).runOnUiThread {
                    codeTextLabel.text = it.text
                    //codeScanner.stopPreview()
                }
            }

            errorCallback = ErrorCallback {
                (fragmentView.context as Activity).runOnUiThread {
                    Log.d( TAG, "Camera initialization error: ${ it.message }" )
                }
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun setupPermissions(fragmentView:View) {
        val permission = ContextCompat.checkSelfPermission( fragmentView.context,
            android.Manifest.permission.CAMERA )

        if( permission != PackageManager.PERMISSION_GRANTED ) {
            makeRequest()
        }

    }

    private fun makeRequest() {
        requestPermissions(
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if( grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED ) {
                    println("You need the camera permission to be able to use this app")
                    //Toast.makeText(fragmentView.context, "You need the camera permission to be able to use this app", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }
}

