package com.cesararellano.focuscode

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
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

        checkForPermissions()
        codeScanner(fragmentView)

        return fragmentView
    }

    private fun codeScanner(fragmentView:View) {
        val scannerView = fragmentView.findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(fragmentView.context, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                requireActivity().runOnUiThread {
                    Log.d(TAG, "Texto escaneado: ${ it.text }")
                    Navigation.findNavController(fragmentView).navigate(R.id.historyFragment)
                }
            }

            errorCallback = ErrorCallback {
                requireActivity().runOnUiThread {
                    Log.d( TAG, "Camera initialization error: ${ it.message }" )
                }
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }


    private fun checkForPermissions() {
        val permission = android.Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(context, "¡Comienza a escanear!", Toast.LENGTH_SHORT).show()
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog()
            else -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), CAMERA_REQUEST_CODE)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                goToSettingsApp()

                Toast.makeText(context,
                    "$name permission refused",
                    Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(context,
                    "$name permission granted",
                    Toast.LENGTH_SHORT).show()
            }
        }

        innerCheck("Camera")
    }

    private fun showDialog() {
        val permission = android.Manifest.permission.CAMERA

        val builder = AlertDialog.Builder(context)
        builder.apply {
            setMessage("Permission to access your camera is required to use this app")
            setTitle("Permission required")
            setPositiveButton("Ok") { _, _ ->
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), CAMERA_REQUEST_CODE)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun goToSettingsApp() {
        val intend = Intent()
        intend.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intend.addCategory(Intent.CATEGORY_DEFAULT)
        intend.data = Uri.parse("package:" + requireActivity().applicationContext.packageName)
        intend.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intend.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intend.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(intend)
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

