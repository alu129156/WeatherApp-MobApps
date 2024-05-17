package com.example.weatherapp_juanarizaonecha.shared

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.R
import com.example.weatherapp_juanarizaonecha.dao.toCity
import com.example.weatherapp_juanarizaonecha.utils.City
import com.example.weatherapp_juanarizaonecha.utils.DataUtils
import com.example.weatherapp_juanarizaonecha.utils.HistoricUtils

object Shared {

    //SHARED TABLE FOR CITIES LIST AND CITY DETAIL
    fun setWeatherData(city: City, activity: AppCompatActivity) {
        activity.findViewById<TextView>(R.id.twDescription).text = city.description

        val dateViews = listOf(
            R.id.tw00, R.id.tw10, R.id.tw20, R.id.tw30, R.id.tw40, R.id.tw50,
            R.id.tw60, R.id.tw70, R.id.tw80, R.id.tw90, R.id.tw100
        )
        val tempViews = listOf(
            R.id.tw01, R.id.tw11, R.id.tw21, R.id.tw31, R.id.tw41, R.id.tw51,
            R.id.tw61, R.id.tw71, R.id.tw81, R.id.tw91, R.id.tw101
        )
        val tempMaxViews = listOf(
            R.id.tw02, R.id.tw12, R.id.tw22, R.id.tw32, R.id.tw42, R.id.tw52,
            R.id.tw62, R.id.tw72, R.id.tw82, R.id.tw92, R.id.tw102
        )
        val tempMinViews = listOf(
            R.id.tw03, R.id.tw13, R.id.tw23, R.id.tw33, R.id.tw43, R.id.tw53,
            R.id.tw63, R.id.tw73, R.id.tw83, R.id.tw93, R.id.tw103
        )
        val precipViews =  listOf(
            R.id.tw04, R.id.tw14, R.id.tw24, R.id.tw34, R.id.tw44, R.id.tw54,
            R.id.tw64, R.id.tw74, R.id.tw84, R.id.tw94, R.id.tw104
        )
        val windViews = listOf(
            R.id.tw05, R.id.tw15, R.id.tw25, R.id.tw35, R.id.tw45, R.id.tw55,
            R.id.tw65, R.id.tw75, R.id.tw85, R.id.tw95, R.id.tw105
        )

        val numDays = dateViews.size
        for (i in 0 until  numDays) {
            activity.findViewById<TextView>(dateViews[i]).text = city.forecasts[i].getDayAndMonth()
            activity.findViewById<TextView>(tempViews[i]).text = city.forecasts[i].temperature.getDegrees()
            activity.findViewById<TextView>(tempMaxViews[i]).text = city.forecasts[i].temperatureMax.getDegrees()
            activity.findViewById<TextView>(tempMinViews[i]).text = city.forecasts[i].temperatureMin.getDegrees()
            activity.findViewById<TextView>(precipViews[i]).text = city.forecasts[i].getPrecipProb()
            activity.findViewById<TextView>(windViews[i]).text = city.forecasts[i].getWindSpeed()
        }
    }

    //METHOD TO GET THE CURRENT FAVOURITE CITIES --> Look in historical data for reports
    fun getFavCities(): MutableList<City> {
        val favCities = mutableListOf<City>()
        DataUtils.user.cities.forEach { city ->
            if(city.favourite) {
                val historicalCities = HistoricUtils.getCities(city.name) //Maybe you have saved a report detail

                //If has a report add it´s last report, if not add the current today city forecast
                if(historicalCities.isNotEmpty()) {
                    //The last historical city has the most actual report saved in DB
                    val cHist = toCity(historicalCities[historicalCities.size-1],city.favourite)
                    cHist.reported = true
                    cHist.dateTimeLastReport = historicalCities[historicalCities.size-1].dateTimeStr
                    favCities.add(cHist)
                } else {
                    favCities.add(city)
                }
            }
        }
        return favCities
    }
}