package com.example.weatherapp

object Constants {

    const val LANGUAGE_KEY = "languageKey"
    const val UNITS_KEY = "unitsKey"
    const val LOCATION_KEY = "locationMethod"
    const val SHARED_PREFERENCES_NAME = "appSharedPreferences"
    const val LOCATION_PREFERENCE_METHOD = "locationPreferenceMethod"
    enum class SupportedLanguages { Arabic, English }
    enum class SupportedUnits { Standard, Metric, Imperial }
    enum class LocationMethod { GPS, Map }

}
