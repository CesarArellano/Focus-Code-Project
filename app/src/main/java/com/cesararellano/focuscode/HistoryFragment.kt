package com.cesararellano.focuscode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import java.util.*


class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        val scanItem = ScanItem("https://github.com/CesarArellano", Calendar.getInstance().time, "http")
        val scanItem2 = ScanItem("geo:13.4545, -92.23232", Calendar.getInstance().time, "geo")

        val scanList = listOf(scanItem, scanItem2)
        val adapter = ScansAdapter(requireContext(), scanList)

        val historyList = view.findViewById<ListView>(R.id.historyList)
        historyList.adapter = adapter

        historyList.setOnItemClickListener { _, _, position, _ ->
            println(scanList[position].scanCode)
        }

        return view
    }
}