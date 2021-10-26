package com.cesararellano.focuscode

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.ListView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        var scanList = emptyList<ScanItem>()
        val database = AppDatabase.getDatabase(view.context)
        val historyList = view.findViewById<ListView>(R.id.historyList)
        val emptyScansImage = view.findViewById<ImageView>(R.id.emptyScansImage)

        database.scans().getAllScans().observe( viewLifecycleOwner, {
            scanList = it
            val adapter = ScansAdapter(requireContext(), scanList)
            historyList.adapter = adapter

            if( scanList.isEmpty() ) {
                historyList.visibility = View.GONE
                emptyScansImage.visibility = View.VISIBLE
            } else {
                historyList.visibility = View.VISIBLE
                emptyScansImage.visibility = View.GONE
            }
        })

        historyList.setOnItemClickListener { _, _, position, _ ->
            val currentScan = scanList[position]
            when( currentScan.scanCode ) {
                "http"-> {
                    goToUrl(currentScan.scanCode)
                }
                "geo" -> {
                    goToMapActivity(currentScan.scanCode)
                }
                else -> {
                    Toast.makeText(context, "Texto escaneado: ${ currentScan.scanCode }", Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

    private fun goToMapActivity(scanCode: String) {
        val mapIntent = Intent( requireContext(), MapActivity::class.java ).apply {
            putExtra("PLACE_LOCATION", scanCode)
        }

        startActivity(mapIntent)
    }

    private fun goToUrl(url: String) {
        val uri:Uri = Uri.parse(url)
        startActivity( Intent(Intent.ACTION_VIEW, uri) )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.history_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when( item.itemId ) {
            R.id.deleteHistoryButton -> {
                val database = AppDatabase.getDatabase( requireContext().applicationContext )
                //Acción asíncrona (Corrutina)
                CoroutineScope(Dispatchers.IO).launch {
                    database.scans().deleteAllScans()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}