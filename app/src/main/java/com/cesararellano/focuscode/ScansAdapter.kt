package com.cesararellano.focuscode

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

// Creación del adapter para nuestro ListView.
class ScansAdapter(private val mcontext: Context, private val scansList: List<ScanItem>) : ArrayAdapter<ScanItem>(mcontext, 0, scansList ) {


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mcontext).inflate(R.layout.scan_item, parent, false)
        val scanItem = scansList[position]
        var scanCode = scanItem.scanCode

        // Si el scanCode es extenso para no desbordarlo de la pantalla, lo limitamos.
        if(scanCode.length > 32 ) {
            scanCode = "${ scanCode.substring(0, 32) }..."
        }

        // Establecemos los valores al item.
        layout.findViewById<TextView>(R.id.scanCodeLabel).text = scanCode
        layout.findViewById<TextView>(R.id.dateScanLabel).text = scanItem.scanDate

        // Establecemos el icono dependiendo del tipo de scan.
        val scanIcon = layout.findViewById<ImageView>(R.id.scanIcon)

        if( scanItem.scanType.contains("http") ) {
            scanIcon.setImageResource(R.drawable.ic_web)
        } else {
            scanIcon.setImageResource(R.drawable.ic_baseline_map_24)
        }

        return layout
    }
}