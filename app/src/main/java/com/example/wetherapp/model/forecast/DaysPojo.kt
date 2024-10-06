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
    val groupedByDay = forecast.groupBy { listElement ->
        listElement.dt
    }

    return groupedByDay.map { (day, listElements) ->
        val firstElement = listElements.firstOrNull()

        if (firstElement != null) {

            val day = getDay(firstElement.dt)
            val description = firstElement.weather.firstOrNull()?.description ?: ""
            val degree = "${firstElement.main.tempMin.toDouble()}°C/ ${firstElement.main.tempMax.toString()}°C"
            val thum = firstElement.weather.firstOrNull()?.icon ?: ""

            DaysPojo(day, description, degree, thum)
        } else {
            DaysPojo(day.toString(), "", "", "")
        }
    }
}

private fun getDay(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
    return dayOfWeek ?: ""
}


