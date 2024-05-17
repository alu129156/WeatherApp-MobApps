package com.example.weatherapp_juanarizaonecha.utils

import com.example.weatherapp_juanarizaonecha.sharedpreferences.CrudAPI
import java.net.URL
import kotlinx.serialization.json.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.Delegates

enum class WeatherCities(val isoCountryCode: String) {
    Zaragoza("ES"),
    Madrid("ES"),
    Rome("IT"),
    London("UK"),
    Tokyo("JP"),
    Paris("FR"),
    Berlin("DE"),
    Sydney("AU"),
    Moscow("RU"),
    Beijing("CN")
}

private fun getMonthName(monthNumber: Int): String {
    val months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    return if (monthNumber in 1..12) months[monthNumber - 1] else "Invalid month"
}

data class CityRequest(val name: String, val countryCode: String) {
    private val apiKey: String = "X4L4EFE3SE4UUWFRSNTVRHWWB"
    fun getUrl(): URL {
        return URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${name},${countryCode}?unitGroup=metric&key=${apiKey}")
    }
}

data class User(val name: String, val email: String, val cities: MutableList<City>)

data class City(
    val name: String, val latitude: Float, val longitude: Float, val resolvedAdress: String?,
    val description: String?, val timezone: String?, var favourite: Boolean,
    val forecasts: MutableList<Forecast>) {

    var reported by Delegates.notNull<Boolean>()
    lateinit var dateTimeLastReport: String //Format : ${day} of ${month}

    //Use this constructor to get the last report of fav cities
    constructor(name: String, latitude: Float, longitude: Float,
                favourite: Boolean, forecasts: MutableList<Forecast>) : this(
        name = name,
        latitude = latitude,
        longitude = longitude,
        resolvedAdress = null,
        description = null,
        timezone = null,
        favourite = favourite,
        forecasts = forecasts
    )
    fun isCity(cityName: String): Boolean{
        return cityName == name
    }
}

data class Forecast(
    val datetime: String, val datetimeEpoch: Long?, private val tempMax: Float,
    private val tempMin: Float, private val temp: Float, val preciProb: Float,
    val windSpeed: Float) {

    val temperature = Temperature(temp)
    val temperatureMax = Temperature(tempMax)
    val temperatureMin = Temperature(tempMin)
    private val valuesDateTime = datetime.split("-")// "YYYY-MM-DD"
    private fun getMonth(): Int {
        return valuesDateTime[1].toInt()
    }
    private fun getDay(): Int {
        return valuesDateTime[2].toInt()
    }
    fun getDayAndMonth(): String {
        return getDay().toString() + " of " + getMonthName(getMonth())
    }

    fun getPrecipProb(): String {
        return "$preciProb%"
    }

    fun getWindSpeed(): String {
        return "$windSpeed km/h"
    }
}

data class Temperature(private val temperature: Float) {
    fun getDegrees(): String {
        return temperature.toString() +"ºC"
    }

    fun getFloatValue(): Float {
        return temperature
    }
}

/**
 * SINGLETON to store data fetched from the API. My user is going to have
 * always the data of the 10 cities lists whith it´s last reports
 */
object DataUtils {
    var citiesRequest = mutableListOf<CityRequest>().apply {
        WeatherCities.entries.forEach { city ->
            add(CityRequest(city.name, city.isoCountryCode))
        }
    }
    private var citiesMap = ConcurrentHashMap<CityRequest, City>() //Avoid race conditions in corrutines
    private lateinit var cities: MutableList<City>
    var latitude by Delegates.notNull<Double>()
    var longitude by Delegates.notNull<Double>()
    lateinit var user: User

    fun fillData(cityRequest: CityRequest, apiResponse: String) {
        val jsonObject = parseJson(apiResponse)
        val forecasts = extractForecasts(jsonObject)
        val city = createCity(cityRequest, jsonObject, forecasts)
        citiesMap[cityRequest] = city
    }
    private fun parseJson(apiResponse: String): JsonObject {
        val json = Json { ignoreUnknownKeys = true }
        return json.parseToJsonElement(apiResponse).jsonObject
    }
    private fun extractForecasts(jsonObject: JsonObject): MutableList<Forecast> {
        val forecasts = mutableListOf<Forecast>()
        val days = jsonObject["days"]?.jsonArray
        days?.forEach { dayElement ->
            val dayObject = dayElement.jsonObject
            val forecast = createForecast(dayObject)
            forecasts.add(forecast)
        }
        return forecasts
    }
    private fun createForecast(dayObject: JsonObject): Forecast {
        val datetime = dayObject["datetime"]?.jsonPrimitive?.content!!
        val datetimeEpoch = dayObject["datetimeEpoch"]?.jsonPrimitive?.content!!.toLong()
        val tempMax = dayObject["tempmax"]?.jsonPrimitive?.content!!.toFloat()
        val tempMin = dayObject["tempmin"]?.jsonPrimitive?.content!!.toFloat()
        val temp = dayObject["temp"]?.jsonPrimitive?.content!!.toFloat()
        val preciProb = dayObject["precipprob"]?.jsonPrimitive?.content!!.toFloat()
        val windSpeed = dayObject["windspeed"]?.jsonPrimitive?.content!!.toFloat()

        return Forecast(datetime, datetimeEpoch, tempMax, tempMin, temp, preciProb, windSpeed)
    }
    private fun createCity(cityRequest: CityRequest, jsonObject: JsonObject, forecasts: MutableList<Forecast>): City {
        return City(
            cityRequest.name,
            jsonObject["latitude"]?.jsonPrimitive?.content?.toFloat()!!,
            jsonObject["longitude"]?.jsonPrimitive?.content?.toFloat()!!,
            jsonObject["resolvedAddress"]?.jsonPrimitive?.content!!,
            jsonObject["description"]?.jsonPrimitive?.content!!,
            jsonObject["timezone"]?.jsonPrimitive?.content!!,
            false, forecasts)
    }
    fun setCitiesIntoList() {
        cities = citiesMap.values.toMutableList()
    }
    fun setFavCities(repository: CrudAPI) {
        cities.forEach { city ->
            city.favourite = repository.contains(city.name)
        }
        addUser()
    }
    private fun addUser() {
        user = User("Juan","alu129156@usj.es", cities)
    }
}