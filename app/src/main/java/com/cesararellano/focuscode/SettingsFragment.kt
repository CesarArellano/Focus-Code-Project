package com.cesararellano.focuscode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatDelegate

// Este fragment es el encargado de mostrar las opciones que tiene la app.
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        buildDropdownItems(view)
        return view
    }

    // Construimos de nuevo los items si es que se destruyen en alguna etapa del ciclo de vida.
    override fun onResume() {
        super.onResume()
        buildDropdownItems(requireView())
    }

    // Creamos los elementos del Dropdown para la selección del tema.
    private fun buildDropdownItems(view: View) {
        val themes = resources.getStringArray(R.array.themes)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.theme_dropdown_item, themes)
        val themeDropdown = view.findViewById<AutoCompleteTextView>(R.id.themeDropdown)
        themeDropdown.setAdapter( arrayAdapter )
        // Establecemos las acciones del los items del dropdown.
        themeDropdown.setOnItemClickListener{ _, _, position, _ ->
            val userTheme = when(position) {
                0 -> AppCompatDelegate.MODE_NIGHT_NO
                1 -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(userTheme)
        }

    }
}