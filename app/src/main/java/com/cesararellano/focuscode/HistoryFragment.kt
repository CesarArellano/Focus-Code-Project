package com.cesararellano.focuscode

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.ListView

// En este fragment se visualiza el historial de scans
class HistoryFragment : Fragment() {
    private val focusCodeModel = FocusCodeModel()
    private var scanList = emptyList<ScanItem>()
    private lateinit var database: AppDatabase
    private lateinit var historyList: ListView
    private lateinit var emptyScansImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        // Obteniendo la instancia de la BD.
        database = AppDatabase.getDatabase(view.context)
        // Estableciendo referencias de los elementos de la UI.
        historyList = view.findViewById(R.id.historyList)
        emptyScansImage = view.findViewById(R.id.emptyScansImage)

        // Poniendo un observer, estilo de listener que escucha los cambios y refresca la vista.
        database.scans().getAllScans().observe( viewLifecycleOwner, {
            scanList = it

            // Adapter del ListView.
            val adapter = ScansAdapter(requireContext(), scanList)
            historyList.adapter = adapter

            // Evalúa que vista debe mostrar.
            if( scanList.isEmpty() ) {
                historyList.visibility = View.GONE
                emptyScansImage.visibility = View.VISIBLE
            } else {
                historyList.visibility = View.VISIBLE
                emptyScansImage.visibility = View.GONE
            }
        })

        // Se establecen las acciones que hará el ListView dependiendo del scan seleccionado.
        historyList.setOnItemClickListener { _, _, position, _ ->
            val currentScan = scanList[position]

            val intent = when (currentScan.scanType) {
                "http" -> {
                    focusCodeModel.goToUrl(currentScan.scanCode)
                }
                else -> {
                    focusCodeModel.goToMapActivity(requireContext(), currentScan.scanCode)
                }
            }

            startActivity(intent)
        }

        return view
    }

}