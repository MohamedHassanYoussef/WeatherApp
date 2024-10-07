package com.example.wetherapp.model.forecast

import java.util.Calendar
import java.util.Locale

data class DaysPojo(
    val day: String?,
    val description: String?,
    val degree: String?,
    val thum: String?
)

fun extractDailyWeatherData(forecast: List<ListElement>): List<DaysPojo> {
    val processedDays = mutableSetOf<String>()

    val groupedByDay = forecast.groupBy { listElement ->
        getDay(listElement.dt)
    }

    return groupedByDay.mapNotNull { (day, listElements) ->
        val firstElement = listElements.firstOrNull()

        if (firstElement != null) {
            val description = firstElement.weather.firstOrNull()?.description ?: ""
            val degree = "${firstElement.main.tempMin.toDouble()}°C/ ${firstElement.main.tempMax.toString()}°C"
            val thum = firstElement.weather.firstOrNull()?.icon ?: ""


            if (day !in processedDays) {
                processedDays.add(day)
                DaysPojo(day, description, degree, thum)
            } else {
                null
            }
        } else {
            null
        }
    }
}

private fun getDay(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
    return dayOfWeek ?: ""
}
