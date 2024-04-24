package com.example.weatherapp_juanarizaonecha

import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


object WeatherDataSingleton {
    val cities = mutableMapOf<String,String>().apply {
        put("Zaragoza","ES")
        put("Madrid","ES")
        put("Rome","IT")
        put("London","UK")
        put("Tokyo","JP")
        put("Paris","FR")
        put("Berlin","DE")
        put("Sydney","AU")
        put("Moscow","RU")
        put("Beijing","CN")
    }

    var latitude = mutableMapOf<String,Float>()
    var longitude = mutableMapOf<String,Float>()
    var resolvedAddress = mutableMapOf<String,String>()
    var timezone = mutableMapOf<String,String>()
    var descriptions = mutableMapOf<String, String>() //Only one value per city
    var dateTimesDAYS = mutableMapOf<String, MutableList<String>>() //Several values per city
    var dateTimesEpochDAYS =  mutableMapOf<String, MutableList<Long>>()
    var tempMaxDAYS = mutableMapOf<String, MutableList<Float>>()
    var tempMinDAYS = mutableMapOf<String, MutableList<Float>>()
    var temperaturesDAYS = mutableMapOf<String, MutableList<Float>>()
    var windspeedsDAYS = mutableMapOf<String, MutableList<Float>>()
    var precipprobsDAYS = mutableMapOf<String, MutableList<Float>>()
    private var temperaturesHOURS = mutableMapOf<String, MutableList<Float>>()

    fun fillDataWeather(cityName: String, apiResponse: String) {
        val json = Json { ignoreUnknownKeys = true }
        val jsonObject = json.parseToJsonElement(apiResponse).jsonObject

        //Initialize the lists for each city & get the Strings and Float values
        latitude[cityName] = jsonObject["latitude"]?.jsonPrimitive?.content?.toFloat()!!
        longitude[cityName] = jsonObject["longitude"]?.jsonPrimitive?.content?.toFloat()!!
        resolvedAddress[cityName] = jsonObject["resolvedAddress"]?.jsonPrimitive?.content!!
        timezone[cityName] = jsonObject["timezone"]?.jsonPrimitive?.content!!
        descriptions[cityName] = jsonObject["description"]?.jsonPrimitive?.content!!
        dateTimesDAYS[cityName] = mutableListOf()
        dateTimesEpochDAYS[cityName] = mutableListOf()
        tempMaxDAYS[cityName] = mutableListOf()
        tempMinDAYS[cityName] = mutableListOf()
        temperaturesDAYS[cityName] = mutableListOf()
        windspeedsDAYS[cityName] = mutableListOf()
        precipprobsDAYS[cityName] = mutableListOf()
        temperaturesHOURS[cityName] = mutableListOf()

        val days = jsonObject["days"]?.jsonArray
        days?.forEach { dayElement ->
            val dayObject = dayElement.jsonObject

            dayObject["datetime"]?.jsonPrimitive?.content?.let { datetime ->
                dateTimesDAYS[cityName]?.add(datetime) }

            dayObject["datetimeEpoch"]?.jsonPrimitive?.content?.let { datetimeEpoch ->
                dateTimesEpochDAYS[cityName]?.add(datetimeEpoch.toLong()) }

            dayObject["tempmax"]?.jsonPrimitive?.content?.let { tempmax ->
                    tempMaxDAYS[cityName]?.add(tempmax.toFloat()) }

            dayObject["tempmin"]?.jsonPrimitive?.content?.let { tempmin ->
                    tempMinDAYS[cityName]?.add(tempmin.toFloat()) }

            dayObject["temp"]?.jsonPrimitive?.content?.let { temp ->
                    temperaturesDAYS[cityName]?.add(temp.toFloat()) }

            dayObject["windspeed"]?.jsonPrimitive?.content?.let { windspeed ->
                    windspeedsDAYS[cityName]?.add(windspeed.toFloat()) }

            dayObject["precipprob"]?.jsonPrimitive?.content?.let { precipprob ->
                    precipprobsDAYS[cityName]?.add(precipprob.toFloat()) }

            val hours = dayObject["hours"]?.jsonArray
            hours?.forEach { hourElement ->
                val hourObject = hourElement.jsonObject
                hourObject["temp"]?.jsonPrimitive?.content?.let { tempHour ->
                        temperaturesHOURS[cityName]?.add(tempHour.toFloat()) }
            }
        }
    }
}



class User (val name: String, val email: String, val cities: List<City>){
}

class City (val name: String, val latitude: Float, val longitude: Float, val resolvedAdress: String,
            val timezone: String, forecasts: List<Forecast>) {
}

class Forecast(val datetime: String, val datetimeEpoch: Long, tempMax: Float, tempMin: Float,
            temp: Float, precipProb: Float, windSpeed: Float, description: String) {
}

object UsersSingleton {
    var cities = mutableListOf<City>()
    var favCities = mutableListOf<City>()
    fun fillCities(){
        WeatherDataSingleton.cities.keys.forEach { city ->
            val forecasts = mutableListOf<Forecast>()
            val dateTimes = WeatherDataSingleton.dateTimesDAYS[city]
            val datetimeEpochs = WeatherDataSingleton.dateTimesEpochDAYS[city]
            val tempMaxs = WeatherDataSingleton.tempMaxDAYS[city]
            val tempMins = WeatherDataSingleton.tempMinDAYS[city]
            val temps = WeatherDataSingleton.temperaturesDAYS[city]
            val precipProbs = WeatherDataSingleton.precipprobsDAYS[city]
            val windSpeeds = WeatherDataSingleton.windspeedsDAYS[city]
            val description = WeatherDataSingleton.descriptions[city]!!

            dateTimes?.indices?.forEach { index ->
                val datetime = dateTimes[index]
                val datetimeEpoch = datetimeEpochs?.get(index)!!
                val tempMax = tempMaxs?.get(index)!!
                val tempMin = tempMins?.get(index)!!
                val temp = temps?.get(index)!!
                val precipProb = precipProbs?.get(index)!!
                val windSpeed = windSpeeds?.get(index)!!

                forecasts.add(Forecast(datetime, datetimeEpoch, tempMax, tempMin,
                    temp, precipProb, windSpeed, description))
            }

            val latitude = WeatherDataSingleton.latitude[city]!!
            val longitude = WeatherDataSingleton.longitude[city]!!
            val resolvedAddress = WeatherDataSingleton.resolvedAddress[city]!!
            val timezone = WeatherDataSingleton.timezone[city]!!

            cities.add(City(city, latitude, longitude, resolvedAddress, timezone, forecasts))
        }
    }

    fun fillCityFavourite(cityName: String) {
        cities.forEach { city ->
            if(city.name == cityName) { //Unique name in each city
                favCities.add(city)
            }
        }
    }
}
