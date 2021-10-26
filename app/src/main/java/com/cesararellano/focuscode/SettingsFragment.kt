package com.cesararellano.focuscode

import android.annotation.SuppressLint
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
import com.google.android.gms.ads.AdView

class SettingsFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var removeAdsButton: Button
    private lateinit var adBanner: AdView

    override fun onResume() {
        super.onResume()
        buildDropdownItems(requireView())
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        // Construyendo opciones de temas (claro/oscuro) del dropdown.
        buildDropdownItems(view)
        // Inicializando variables.
        sharedPreferences = view.context.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        adBanner = requireActivity().findViewById(R.id.mainBanner)
        removeAdsButton = view.findViewById(R.id.removeAdsButton)
        removeAdsButton.setOnClickListener{
            showDialog()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val premiumMode = sharedPreferences.getBoolean("premiumMode", false)
        if( premiumMode ) removeAdsButton.visibility = View.GONE
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Compra")
        builder.setMessage("¿Desea remover la publicidad pagando $49 MXN?")
        builder.setPositiveButton("Confirmar") { dialog, _ -> // Confirma la eliminación de la cosa.
            removeAdsButton.visibility = View.GONE
            adBanner.isEnabled = false
            adBanner.visibility = View.GONE
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("premiumMode", true)
            editor.apply()
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
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("userTheme", userTheme)
            editor.apply()

            AppCompatDelegate.setDefaultNightMode(userTheme)
        }
    }
}