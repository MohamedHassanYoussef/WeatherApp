package com.example.wetherapp.model

import com.example.wetherapp.db.PlaceFavPojo
import com.example.wetherapp.model.Current.Current
import com.example.wetherapp.model.Current.Main
import com.example.wetherapp.model.Current.Wind
import com.example.wetherapp.model.Current.Clouds
import com.example.wetherapp.model.Current.Sys
import com.example.wetherapp.model.forecast.Forecast
import com.example.wetherapp.model.forecast.ListElement
import com.example.wetherapp.model.forecast.MainClass
import com.example.wetherapp.model.forecast.City
import com.example.wetherapp.model.forecast.Clouds as ForecastClouds
import com.example.wetherapp.model.forecast.Coord
import com.example.wetherapp.model.forecast.MainEnum
import com.example.wetherapp.model.forecast.Pod
import com.example.wetherapp.model.forecast.Sys as ForecastSys
import com.example.wetherapp.model.forecast.Weather as ForecastWeather
import com.example.wetherapp.model.forecast.Wind as ForecastWind
import com.example.wetherapp.network.FakeWeatherNetworkResponse
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class RepoImplementationTest {

    private lateinit var fakeFavouriteDao: FakeFavouriteDao
    private lateinit var fakeWeatherNetworkResponse: FakeWeatherNetworkResponse
    private lateinit var repoImplementation: RepoImplementation


    val currentWeather = Current(
        id = 501,
        coord = Coord(10.99, 44.34),
        weather = listOf(
            com.example.wetherapp.model.Current.Weather(
                id = 804,
                main = "Clouds",
                description = "overcast clouds",
                icon = "04n"
            )
        ),
        base = "stations",
        main = Main(
            temp = 285.95,
            feelsLike = 285.74,
            tempMin = 284.94,
            tempMax = 287.76,
            pressure = 1009,
            humidity = 94,
            seaLevel = 1009,
            grndLevel = 942
        ),
        visibility = 8412,
        wind = Wind(speed = 2.87, deg = 167, gust = 5.81),
        clouds = Clouds(all = 100),
        dt = 1728380785,
        sys = Sys(
            type = 2,
            id = 2044440,
            country = "IT",
            sunrise = 1728364943,
            sunset = 1728405860
        ),
        timezone = 7200,
        name = "Zocca",
        cod = 200
    )


    var weatherForecast = Forecast(
        cod = "200",
        message = 0,
        cnt = 1,
        city = City(
            id = 2044440,
            name = "Zocca",
            coord = Coord(44.34, 10.99),
            country = "IT",
            population = 5000,
            timezone = 7200,
            sunrise = 1728364943,
            sunset = 1728405860
        ),
        list = listOf(
            ListElement(
                dt = 1728380785,
                main = MainClass(
                    temp = 20.5,
                    feelsLike = 19.8,
                    tempMin = 18.0,
                    tempMax = 23.0,
                    pressure = 1012,
                    seaLevel = 1012,
                    grndLevel = 1000,
                    humidity = 85,
                    tempKf = 0.0
                ),
                weather = listOf(
                    ForecastWeather(
                        id = 800,
                        main = MainEnum.Clear,
                        description = "clear sky",
                        icon = "01d"
                    )
                ),
                clouds = ForecastClouds(all = 0),
                wind = ForecastWind(speed = 1.5, deg = 350, gust = 3.0),
                visibility = 10000,
                pop = 0.0,
                sys = ForecastSys(Pod.D),
                dtTxt = "2024-10-08 12:00:00"
            )
        )
    )

    @Before
    fun setup() {

        fakeFavouriteDao = FakeFavouriteDao()
        fakeWeatherNetworkResponse = FakeWeatherNetworkResponse(
            remoteCurrent = currentWeather,
            remoteForecast = weatherForecast
        )
        repoImplementation = RepoImplementation(
            fakeWeatherNetworkResponse,
            fakeFavouriteDao
        )
    }

    @Test
    fun getCurrent_longLatLanguage_Current() = runBlocking {

        val result = repoImplementation.getCurrentWeather(10.99, 44.34, "en", "metric").first()


        assertEquals(currentWeather.name, result.name)
        assertEquals(currentWeather.main.temp, result.main.temp, 0.01)
        assertEquals(currentWeather.weather[0].description, result.weather[0].description)
        assertEquals(currentWeather.wind.speed, result.wind.speed, 0.01)
    }

    @Test
    fun getForecast_longLatLanguage_Forecast() = runBlocking {

        val result = repoImplementation.getForecastWeather(10.99, 44.34, "en", "metric").first()

        assertEquals(weatherForecast.city.name, result.city.name)
        assertEquals(weatherForecast.list[0].main.temp, result.list[0].main.temp, 0.01)
        assertEquals(
            weatherForecast.list[0].weather[0].description,
            result.list[0].weather[0].description
        )
        assertEquals(weatherForecast.list[0].wind.speed, result.list[0].wind.speed, 0.01)
    }

    @Test
    fun insertPlaceToFav_PlaceInsertedSuccessfully() = runBlocking {
        val place = PlaceFavPojo(
            id = 1,
            cityName = "Zocca",
            latitude = 44.34,
            longitude = 10.99
        )

        repoImplementation.insertPlaceToFav(place)

        val favPlaces = repoImplementation.getAllFavouritePlaces().first()
        assertEquals(1, favPlaces.size)
        assertEquals(place.cityName, favPlaces[0].cityName)
    }

    @Test
    fun deletePlaceFromFav_PlaceDeletedSuccessfully() = runBlocking {

        val place = PlaceFavPojo(
            id = 1,
            cityName = "Zocca",
            latitude = 44.34,
            longitude = 10.99
        )
        repoImplementation.insertPlaceToFav(place)
        repoImplementation.deletePlaceFromFav(place)
        val favPlaces = repoImplementation.getAllFavouritePlaces().first()
        assertTrue(favPlaces.isEmpty())
    }

    @Test
    fun getAllFavouritePlaces_ReturnsFavouritePlaces() = runBlocking {

        val place1 = PlaceFavPojo(
            id = 1,
            cityName = "Zocca",
            latitude = 44.34,
            longitude = 10.99
        )

        val place2 = PlaceFavPojo(
            id = 2,
            cityName = "Rome",
            latitude = 41.89,
            longitude = 12.49
        )
        repoImplementation.insertPlaceToFav(place1)
        repoImplementation.insertPlaceToFav(place2)


        val favPlaces = repoImplementation.getAllFavouritePlaces().first()
        assertEquals(2, favPlaces.size)
        assertEquals("Zocca", favPlaces[0].cityName)
        assertEquals("Rome", favPlaces[1].cityName)
    }


}
