package com.example.weatherapp_juanarizaonecha.dao

import com.example.weatherapp_juanarizaonecha.utils.City
import com.example.weatherapp_juanarizaonecha.utils.Forecast
import kotlin.properties.Delegates

//CLASS TO SAVE THE REPORT OF THE CURRENT DATE TIME
data class CityHistory(var dateTime: String, var name: String, var tempMin: Float,
                       var tempMax: Float, var temper: Float, var precipProb: Float, var windSpeed: Float,
                       var latitude: Float, var longitude: Float, var dateTimeStr: String) {
    var reportID by Delegates.notNull<Long>()
}

//Takes the todays forecast and set in Historic Cities
fun toCityHistory(city: City): CityHistory {
    return CityHistory(
        dateTime = city.forecasts[0].datetime,
        name = city.name,
        tempMin = city.forecasts[0].temperatureMin.getFloatValue(),
        tempMax = city.forecasts[0].temperatureMax.getFloatValue(),
        temper = city.forecasts[0].temperature.getFloatValue(),
        precipProb = city.forecasts[0].preciProb,
        windSpeed = city.forecasts[0].windSpeed,
        latitude = city.latitude,
        longitude = city.longitude,
        dateTimeStr = city.forecasts[0].getDayAndMonth())
}

//Gets a City with only one forecast. Is also useful in the filtering operations
fun toCity(cityHistory: CityHistory, favourite: Boolean): City {
    val currentForecast = Forecast(cityHistory.dateTime,null, cityHistory.tempMax, cityHistory.tempMin,
        cityHistory.temper,cityHistory.precipProb,cityHistory.windSpeed)

    val lastForecast = mutableListOf<Forecast>()
    lastForecast.add(currentForecast)
    return City(cityHistory.name, cityHistory.latitude,cityHistory.longitude,favourite,lastForecast)
}