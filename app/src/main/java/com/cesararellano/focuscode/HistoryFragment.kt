package com.cesararellano.focuscode

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        database.scans().getAllScans().observe(viewLifecycleOwner, {
            scanList = it
            val adapter = ScansAdapter(requireContext(), scanList)
            historyList.adapter = adapter
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