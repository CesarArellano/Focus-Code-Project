package com.cesararellano.focuscode

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

private const val CAMERA_REQUEST_CODE = 101
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        codeScanner()
    }

    private fun codeScanner() {
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val codeTextLabel = findViewById<TextView>(R.id.codeTextLabel)
        codeScanner = CodeScanner(this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    codeTextLabel.text = it.text
                    codeScanner.stopPreview()
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.d( TAG, "Camera initialization error: ${ it.message }" )
                }
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission( this,
            android.Manifest.permission.CAMERA )

        if( permission != PackageManager.PERMISSION_GRANTED ) {
            makeRequest()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if( grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED ) {
                    Toast.makeText(this, "You need the camera permission to be able to use this app", Toast.LENGTH_SHORT).show()
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