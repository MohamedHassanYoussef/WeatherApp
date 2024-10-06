
package com.example.wetherapp.model.forecast


import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HoursPojo (
    val day: String,
    val hours: String,
    val degree: String,
    val thum: String,
)

fun extractWeatherData(forecast: Forecast): List<HoursPojo> {
    val timeFormat = SimpleDateFormat("hh:mma", Locale.getDefault())

    return forecast.list.map { listElement ->
        val day = getDay(listElement.dt)
        val timeInMillis = listElement.dt * 1000
        val hours = timeFormat.format(Date(timeInMillis))
        val degree = "${listElement.main.temp}°C"
        val thum = listElement.weather.firstOrNull()?.icon ?: ""

        HoursPojo(day, hours, degree, thum)
    }
}

private fun getDay(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
    return dayOfWeek ?: ""
}
