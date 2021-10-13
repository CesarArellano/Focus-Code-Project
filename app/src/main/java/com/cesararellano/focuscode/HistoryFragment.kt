package com.cesararellano.focuscode

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.ListView

class HistoryFragment : Fragment() {

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

        database.scans().getAllScans().observe(viewLifecycleOwner, {
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
            if(currentScan.scanType == "http") {
                goToUrl(currentScan.scanCode)
            } else {
                goToMapActivity(currentScan.scanCode)
            }
        }

        return view
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