package com.cesararellano.focuscode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class SettingsFragment : Fragment() {

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
        return view
    }

    private fun buildDropdownItems(view: View) {
        val themes = resources.getStringArray(R.array.themes)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.theme_dropdown_item, themes)
        val themeDropdown = view.findViewById<AutoCompleteTextView>(R.id.themeDropdown)
        themeDropdown.setAdapter( arrayAdapter )
    }
}