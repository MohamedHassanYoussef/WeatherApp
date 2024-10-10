package com.example.wetherapp.ui.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.ConstantValue
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationSharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            ConstantValue.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        locationSharedPreferences = requireActivity().getSharedPreferences(
            ConstantValue.LOCATION_PREFERENCE_METHOD,
            Context.MODE_PRIVATE
        )
        updateData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle language selection
        binding.radioGroupSettingLanguage.setOnCheckedChangeListener { _, checkedId ->
            val selectedLanguage: RadioButton = binding.root.findViewById(checkedId)
            when (selectedLanguage.text) {
                getString(R.string.arabic) -> {
                    sharedPreferences.edit()
                        .putString(ConstantValue.LANGUAGE_KEY, ConstantValue.SupportedLanguages.Arabic.toString())
                        .apply()
                    changeAppLanguage("ar")
                }

                getString(R.string.english) -> {
                    sharedPreferences.edit()
                        .putString(ConstantValue.LANGUAGE_KEY, ConstantValue.SupportedLanguages.English.toString())
                        .apply()
                    changeAppLanguage("en")
                }
            }
        }

        // Handle temperature unit selection
        binding.radioGroupSettingTemp.setOnCheckedChangeListener { _, checkedId ->
            val selectedUnit: RadioButton = binding.root.findViewById(checkedId)
            when (selectedUnit.text) {
                getString(R.string.celsius) -> {
                    sharedPreferences.edit()
                        .putString(ConstantValue.UNITS_KEY, ConstantValue.SupportedUnits.Metric.toString())
                        .apply()
                }

                getString(R.string.kelvin) -> {
                    sharedPreferences.edit()
                        .putString(ConstantValue.UNITS_KEY, ConstantValue.SupportedUnits.Standard.toString())
                        .apply()
                }

                getString(R.string.fahrenheit) -> {
                    sharedPreferences.edit()
                        .putString(ConstantValue.UNITS_KEY, ConstantValue.SupportedUnits.Imperial.toString())
                        .apply()
                }
            }
        }

        // Handle location method selection
        binding.radioGroupSettingLocation.setOnCheckedChangeListener { _, checkedId ->
            val selectedLocation: RadioButton = binding.root.findViewById(checkedId)
            when (selectedLocation.text) {
                getString(R.string.gps_setting) -> {
                    locationSharedPreferences.edit()
                        .putString(ConstantValue.LOCATION_KEY, ConstantValue.LocationMethod.GPS.toString())
                        .apply()
                }

                getString(R.string.map_title) -> {
                    locationSharedPreferences.edit()
                        .putString(ConstantValue.LOCATION_KEY, ConstantValue.LocationMethod.Map.toString())
                        .apply()

                    // Navigate to MapsSetting Fragment when "Map" is selected
                    findNavController().navigate(R.id.mapsSetting)
                }
            }
        }

        // Handle notifications
        binding.radioGroupSettingNotification.setOnCheckedChangeListener { _, checkedId ->
            val selectedNotification: RadioButton = binding.root.findViewById(checkedId)
            when (selectedNotification.text) {
                getString(R.string.enable) -> {
                    // Enable notifications (your code)
                }

                getString(R.string.disable) -> {
                    // Disable notifications (your code)
                }
            }
        }
    }

    private fun changeAppLanguage(languageTag: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun updateData() {
        // Fetch saved settings
        val savedLanguage = sharedPreferences.getString(ConstantValue.LANGUAGE_KEY, ConstantValue.SupportedLanguages.English.toString())
        val savedUnit = sharedPreferences.getString(ConstantValue.UNITS_KEY, ConstantValue.SupportedUnits.Metric.toString())
        val savedLocation = locationSharedPreferences.getString(ConstantValue.LOCATION_KEY, ConstantValue.LocationMethod.GPS.toString())

        // Update UI based on saved settings
        if (savedLocation == ConstantValue.LocationMethod.GPS.toString()) {
            binding.radioGroupSettingLocation.check(binding.radioSettingGps.id)
        } else {
            binding.radioGroupSettingLocation.check(binding.radioSettingMap.id)
        }

        if (savedLanguage == ConstantValue.SupportedLanguages.English.toString()) {
            binding.radioGroupSettingLanguage.check(binding.radioSettingEnglish.id)
        } else {
            binding.radioGroupSettingLanguage.check(binding.radioSettingArabic.id)
        }

        when (savedUnit) {
            ConstantValue.SupportedUnits.Standard.toString() -> binding.radioGroupSettingTemp.check(binding.radioSettingKelvin.id)
            ConstantValue.SupportedUnits.Imperial.toString() -> binding.radioGroupSettingTemp.check(binding.radioFahrenheit.id)
            else -> binding.radioGroupSettingTemp.check(binding.radioSettingCelsius.id)
        }
    }
}
