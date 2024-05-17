package com.example.weatherapp_juanarizaonecha.filtering


import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.example.weatherapp_juanarizaonecha.dao.toCity
import com.example.weatherapp_juanarizaonecha.utils.City
import com.example.weatherapp_juanarizaonecha.utils.DataUtils
import com.example.weatherapp_juanarizaonecha.utils.HistoricUtils

enum class Filters {
    WIND_SPEED,
    TEMPERATURE,
    COORDINATES
}
class SearchFilterController(private val list: List<City>) {

    fun search(query: String): List<City> {
        if (query.isEmpty()) {
            return list
        }
        return list.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun filterByTemperature(): List<SpannableString> {
        return filterMold(Filters.TEMPERATURE)
    }

    fun filterByCoordinates(): List<SpannableString> {
        return filterMold(Filters.COORDINATES)
    }

    fun filterByWindSpeed(): List<SpannableString> {
        return filterMold(Filters.WIND_SPEED)
    }

    private fun filterMold(paramFilter: Filters): List<SpannableString> {
        var index = 1
        val result = mutableListOf<SpannableString>()
        list.forEach { city ->
            val param = when (paramFilter) {
                Filters.WIND_SPEED -> city.forecasts[0].windSpeed.toString() + "km/h"
                Filters.TEMPERATURE -> city.forecasts[0].temperature.getDegrees()
                else -> "(${String.format("%.1f",city.latitude)},${String.format("%.1f",city.longitude)})"
            }
            result.add(changeStringToColour(city,param,index))
            index += 1
        }
        return result
    }

    private fun changeStringToColour(city: City, secondParam: String, index: Int): SpannableString {
        val str = index.toString() + ".  " + city.name + "\n\n" + secondParam
        val spannable = SpannableString(str)
        val blackLength = index.toString().length + 3 + city.name.length

        spannable.setSpan(ForegroundColorSpan(Color.BLACK), 0,
            blackLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(ForegroundColorSpan(Color.GREEN), blackLength,
            str.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}

//METHOD TO GET A CITY BY THE SPANABLE STRING
fun getCity(str: String, needAReportInCity: Boolean): City? {
    val params = str.split(".  ")
    val params2 = params[1].split("\n\n")
    val cityStr = params2[0]
    DataUtils.user.cities.forEach { cCity ->
        if(cCity.isCity(cityStr)){
            if(cCity.favourite && needAReportInCity) { //This is 4 the last report filtering in Fav Cities Activity
                val historicalCities = HistoricUtils.getCities(cCity.name) //Maybe you have saved a report detail

                //If has a report add it´s last report, if not add the current today city forecast
                if(historicalCities.isNotEmpty()) {
                    val cHist = toCity(historicalCities[historicalCities.size - 1], cCity.favourite)
                    cHist.reported = true
                    cHist.dateTimeLastReport = historicalCities[historicalCities.size - 1].dateTimeStr
                    return cHist
                }
            }
            return cCity
        }
    }
    return null
}

