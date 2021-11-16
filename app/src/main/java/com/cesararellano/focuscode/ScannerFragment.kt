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

// Este fragment es donde se escanean los códigos QR y los códigos de barras.
class ScannerFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private val focusCodeModel = FocusCodeModel()
    private lateinit var dateFormat: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView = inflater.inflate(R.layout.fragment_scanner, container, false)

        // Estableciendo el formato de la fecha que tendrán los scans.
        dateFormat = SimpleDateFormat( "'Fecha:' dd-MM-yyyy, HH:mm", Locale.getDefault() )

        // Configurando permisos de la cámara y del paquete CodeScanner.
        checkForPermissions()
        codeScanner(fragmentView)

        return fragmentView
    }

    // Inicializamos referencias tanto para CodeScanner como para la base de datos.
    private fun codeScanner(fragmentView:View) {
        val database = AppDatabase.getDatabase(requireContext())
        val scannerView = fragmentView.findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(fragmentView.context, scannerView)

        codeScanner.apply {
            // Configuramos lo necesario para que funcione CodeScanner como nosotros queremos.
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                requireActivity().runOnUiThread {
                    // Detiene CodeScanner.
                    codeScanner.stopPreview()
                    val scanType = when {
                        it.text.contains("http")-> {
                            "http"
                        }
                        else -> {
                            "geo"
                        }
                    }
                    // String de la fecha actual con formato.
                    val currentDate = dateFormat.format( Date() )
                    // Se crea una instancia de ScanItem.
                    val scan = ScanItem(it.text, currentDate, scanType)

                    // Acción asíncrona (corrutina) para hacer un registro en la BD
                    CoroutineScope(Dispatchers.IO).launch {
                        database.scans().insertScan(scan)
                    }

                    val intent = when (scanType) {
                        "http" -> {
                            focusCodeModel.goToUrl(scan.scanCode)
                        }
                        else -> {
                            focusCodeModel.goToMapActivity(requireContext(), scan.scanCode)
                        }
                    }

                    // Navega al Intent correspondiente.
                    startActivity(intent)

                    // Lo redirige al History Fragment.
                    Navigation.findNavController(fragmentView).navigate(R.id.historyFragment)
                }
            }

            errorCallback = ErrorCallback {
                requireActivity().runOnUiThread {
                    // Si surge un error con CodeScanner por permisos de la cámara se abrirá la app de ajustes de Android de esta misma app para conceder los permisos necesarios.
                    goToSettingsApp()
                    Toast.makeText(context, "Por favor active el permiso de la cámara, si desea escanear", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Cuando scannerView esté inicializado, se da comienzo a escanear.
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    // Se hace el request para dar permisos de la cámara.
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

    // Se sobreescribe el método para saber qué hacer al respecto con CodeScanner.
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

    // Muestra el AlertDialog para dar los permisos de la cámara.
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

    // Envía a los settings del teléfono.
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

    // En cualquier de los siguientes estados del ciclo de vida, realizaremos un start o un dispose a CodeScanner.
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }
}


