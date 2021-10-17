package com.cesararellano.focuscode

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate

class SettingsFragment : Fragment() {
    private lateinit var removeAdsButton:Button
    override fun onResume() {
        super.onResume()
        buildDropdownItems(requireView())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        buildDropdownItems(view)
        removeAdsButton = view.findViewById(R.id.removeAdsButton)
        removeAdsButton.setOnClickListener{
            showDialog()
        }
        return view
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Compra")
        builder.setMessage("¿Desea remover la publicidad pagando $49 MXN?")
        builder.setPositiveButton("Confirmar") { dialog, _ -> // Confirma la eliminación de la cosa.
            removeAdsButton.visibility = View.GONE
            dialog.cancel()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> // Cancela la acción de eliminar.
            dialog.cancel()
        }

        val alert: AlertDialog = builder.create()
        alert.show() // Despliega en pantalla el AlertDialog.
    }

    private fun buildDropdownItems(view: View) {
        val themes = resources.getStringArray(R.array.themes)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.theme_dropdown_item, themes)
        val themeDropdown = view.findViewById<AutoCompleteTextView>(R.id.themeDropdown)
        themeDropdown.setAdapter( arrayAdapter )

        themeDropdown.setOnItemClickListener{ _, _, position, _ ->
            val userTheme = when(position) {
                0 -> AppCompatDelegate.MODE_NIGHT_NO
                1 -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            val sharedPreferences = requireContext().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("userTheme", userTheme)
            editor.apply()

            AppCompatDelegate.setDefaultNightMode(userTheme)
        }

    }
}