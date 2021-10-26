package com.cesararellano.focuscode

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val CAMERA_REQUEST_CODE = 101
class ScannerFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var dateFormat: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView = inflater.inflate(R.layout.fragment_scanner, container, false)

        dateFormat = SimpleDateFormat( "'Fecha:' dd-MM-yyyy, HH:mm", Locale.getDefault() )
        checkForPermissions()
        codeScanner(fragmentView)

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

    private fun codeScanner(fragmentView:View) {
        val database = AppDatabase.getDatabase(requireContext())
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

                    codeScanner.stopPreview()
                    val scanType = when {
                        it.text.contains("http")-> {
                            "http"
                        }
                        it.text.contains("geo")-> {
                            "geo"
                        }
                        else -> {
                            "other"
                        }
                    }
                    val currentDate = dateFormat.format( Date() )
                    val scan = ScanItem(it.text, currentDate, scanType)
                    //Acción asíncrona (Corrutina)
                    CoroutineScope(Dispatchers.IO).launch {
                        database.scans().insertScan(scan)
                    }

                    when( scanType ) {
                        "http"-> {
                            goToUrl(scan.scanCode)
                        }
                        "geo" -> {
                            goToMapActivity(scan.scanCode)
                        }
                        else -> {
                            Toast.makeText(context, "Texto escaneado: ${ scan.scanCode }", Toast.LENGTH_LONG).show()
                        }
                    }

                    Navigation.findNavController(fragmentView).navigate(R.id.historyFragment)
                }
            }

            errorCallback = ErrorCallback {
                requireActivity().runOnUiThread {
                    goToSettingsApp()
                    Toast.makeText(context, "Por favor active el permiso de la cámara, si desea escanear", Toast.LENGTH_LONG).show()
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
        if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            goToSettingsApp()
            Toast.makeText(context, "Por favor active el permiso de la cámara, si desea escanear", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialog() {
        val permission = android.Manifest.permission.CAMERA

        val builder = AlertDialog.Builder(context)
        builder.apply {
            setMessage("El permiso para acceder a su cámara es requerido si desea escanear")
            setTitle("Permiso Requerido")
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

    private fun goToMapActivity(scanCode: String) {
        val mapIntent = Intent(requireContext(), MapActivity::class.java).apply {
            putExtra("PLACE_LOCATION", scanCode)
        }

        startActivity(mapIntent)
    }

    private fun goToUrl(url: String) {
        val uri:Uri = Uri.parse(url)
        startActivity( Intent(Intent.ACTION_VIEW, uri) )
    }
}


