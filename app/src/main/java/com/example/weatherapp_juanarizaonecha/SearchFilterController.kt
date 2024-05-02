package com.example.weatherapp_juanarizaonecha


import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

class SearchFilterController(private val list: List<City>) {

    fun search(query: String): List<City> {
        if (query.isEmpty()) {
            return list
        }
        return list.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun filterByTemperature(isFavActivity: Boolean): List<SpannableString> {
        var index = 1
        val result = mutableListOf<SpannableString>()
        list.forEach { city ->
            val temp = city.forecasts[0].temperature.getDegrees()
            result.add(changeStringToColour(city, temp, index, isFavActivity))
            index += 1
        }
        return result
    }

    fun filterByCoordinates(isFavActivity: Boolean): List<SpannableString> {
        val namesAndAddress = mutableListOf<SpannableString>()
        var index = 1
        list.forEach { city ->
            val coordinateParam = "(" + city.latitude + "," +
                    city.longitude + ")"
            namesAndAddress.add(changeStringToColour(city,coordinateParam,index,isFavActivity))
            index += 1
        }

        return namesAndAddress
    }

    fun filterByWindSpeed(isFavActivity: Boolean): List<SpannableString> {
        var index = 1
        val result = mutableListOf<SpannableString>()
        list.forEach { city ->
            val windSpeedParam = city.forecasts[0].windSpeed.toString() + "km/h"
            result.add(changeStringToColour(city,windSpeedParam,index,isFavActivity))
            index += 1
        }
        return result
    }

    private fun changeStringToColour(city: City, secondParam: String, index: Int,
                                     isFavActivity: Boolean): SpannableString {
        val heart = "\u2764"
        var str = index.toString() + ".  " + city.name +
                "     " + secondParam
        if(city.favourite and !isFavActivity) {
            str += "    $heart"
        }

        val spannable = SpannableString(str)
        val blackLength = index.toString().length + 3 + city.name.length

        spannable.setSpan(ForegroundColorSpan(Color.GREEN), blackLength,
            str.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}

