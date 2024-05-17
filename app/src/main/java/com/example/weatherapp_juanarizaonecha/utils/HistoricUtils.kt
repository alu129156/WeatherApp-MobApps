package com.example.weatherapp_juanarizaonecha.utils

import com.example.weatherapp_juanarizaonecha.dao.CityHistory

/**
 * SINGLETON to store the historic data. There are few rows in the database
 * so with this singleton we reduce the amount of queries to de database,
 * replaced with list operations (much efficient)
 */
object HistoricUtils {
    private var allCities = mutableListOf<CityHistory>()

    fun setCities(elements: List<CityHistory>) {
        elements.forEach { element ->
            allCities.add(element)
        }
    }

    fun setCity(element: CityHistory) {
        allCities.add(element)
    }

    fun removeCity(element: CityHistory){
        allCities.remove(element)
    }

    fun getCities(cityName: String): List<CityHistory> {
        val searchedCities: MutableList<CityHistory> = mutableListOf()
        allCities.forEach { city ->
            if(city.name == cityName) {
                searchedCities.add(city)
            }
        }
        return searchedCities
    }

    fun sameDateTime(cityName: String, dateTime: String): Boolean {
        allCities.forEach { city ->
            if(city.name == cityName && city.dateTime == dateTime) {
                return true
            }
        }
        return false
    }
}