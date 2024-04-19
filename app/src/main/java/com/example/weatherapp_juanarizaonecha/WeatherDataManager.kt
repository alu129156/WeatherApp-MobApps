package com.example.weatherapp_juanarizaonecha

import kotlinx.serialization.json.*

object WeatherDataSingleton {
    val citiesCoordinates = mutableMapOf<String, List<Float>>().apply {
        put("Zaragoza", listOf(41.65606f, -0.87734f))
        put("New York", listOf(40.7128f, -74.0060f))
        put("Los Angeles", listOf(34.0522f, -118.2437f))
        put("London", listOf(51.5074f, -0.1278f))
        put("Tokyo", listOf(35.6895f, 139.6917f))
        put("Paris", listOf(48.8566f, 2.3522f))
        put("Berlin", listOf(52.5200f, 13.4050f))
        put("Sydney", listOf(-33.8688f, 151.2093f))
        put("Moscow", listOf(55.7558f, 37.6173f))
        put("Beijing", listOf(39.9042f, 116.4074f))
    }

    // Cada lista interna representa los datos de una ciudad
    var dateTimes = mutableListOf<MutableList<String>>()
    var descriptions = mutableListOf<MutableList<String>>()
    var tempMax = mutableListOf<MutableList<String>>()
    var tempMin = mutableListOf<MutableList<String>>()
    var temperaturesDAY = mutableListOf<MutableList<String>>()
    var windspeedsDAY = mutableListOf<MutableList<String>>()
    var precipprobsDAY = mutableListOf<MutableList<String>>()
    var temperaturesHOUR = mutableListOf<MutableList<String>>()

    fun fillDataWeather(apiResponse: String) {
        val json = Json { ignoreUnknownKeys = true }
        val jsonObject = json.parseToJsonElement(apiResponse).jsonObject

        val days = jsonObject["days"]?.jsonArray
        days?.forEach { dayElement ->
            val dayObject = dayElement.jsonObject

            // Crear listas para almacenar los datos de esta ciudad
            val cityDateTimes = mutableListOf<String>()
            val cityDescriptions = mutableListOf<String>()
            val cityTempMax = mutableListOf<String>()
            val cityTempMin = mutableListOf<String>()
            val cityTemperaturesDAY = mutableListOf<String>()
            val cityWindspeedsDAY = mutableListOf<String>()
            val cityPrecipprobsDAY = mutableListOf<String>()
            val cityTemperaturesHOUR = mutableListOf<String>()

            // Extraer la información del día
            val datetime = dayObject["datetime"]?.jsonPrimitive?.content
            datetime?.let { cityDateTimes.add(it) }

            val description = dayObject["description"]?.jsonPrimitive?.content
            description?.let { cityDescriptions.add(it) }

            val tempmax = dayObject["tempmax"]?.jsonPrimitive?.content
            tempmax?.let { cityTempMax.add(it) }

            val tempmin = dayObject["tempmin"]?.jsonPrimitive?.content
            tempmin?.let { cityTempMin.add(it) }

            val temp = dayObject["temp"]?.jsonPrimitive?.content
            temp?.let { cityTemperaturesDAY.add(it) }

            val windspeed = dayObject["windspeed"]?.jsonPrimitive?.content
            windspeed?.let { cityWindspeedsDAY.add(it) }

            val precipprob = dayObject["precipprob"]?.jsonPrimitive?.content
            precipprob?.let { cityPrecipprobsDAY.add(it) }

            // Extraer la información de las horas
            val hours = dayObject["hours"]?.jsonArray
            hours?.forEach { hourElement ->
                val hourObject = hourElement.jsonObject

                val tempHour = hourObject["temp"]?.jsonPrimitive?.content
                tempHour?.let { cityTemperaturesHOUR.add(it) }
            }

            // Agregar las listas de esta ciudad a las listas principales
            dateTimes.add(cityDateTimes)
            descriptions.add(cityDescriptions)
            tempMax.add(cityTempMax)
            tempMin.add(cityTempMin)
            temperaturesDAY.add(cityTemperaturesDAY)
            windspeedsDAY.add(cityWindspeedsDAY)
            precipprobsDAY.add(cityPrecipprobsDAY)
            temperaturesHOUR.add(cityTemperaturesHOUR)
        }
    }
}


class UserData (val name: String, val email: String, val cities: List<City>){
}

class City (val latitude: Double, val longitude: Double, val resolvedAdress: String,
            val timezone: String, forecasts: List<Forecast>) {
}

class Forecast(val datetime: String, val datetimeEpoch: Long, tempMax: Double,
               tempMin: Double, temp: Double, precipProb: Double, windSpeed: Double, description: String) {
}


