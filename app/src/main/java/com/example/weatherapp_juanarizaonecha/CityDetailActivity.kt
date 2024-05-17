package com.example.weatherapp_juanarizaonecha

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.dao.CityHistory
import com.example.weatherapp_juanarizaonecha.dao.SQLiteCityDao
import com.example.weatherapp_juanarizaonecha.dao.toCityHistory
import com.example.weatherapp_juanarizaonecha.databinding.ActivityCityDetailBinding
import com.example.weatherapp_juanarizaonecha.filtering.getCity
import com.example.weatherapp_juanarizaonecha.shared.Shared
import com.example.weatherapp_juanarizaonecha.sharedpreferences.CrudAPI
import com.example.weatherapp_juanarizaonecha.sharedpreferences.SHARED_PREFERENCES_NAME
import com.example.weatherapp_juanarizaonecha.sharedpreferences.SharedPreferencesRepository
import com.example.weatherapp_juanarizaonecha.utils.City
import com.example.weatherapp_juanarizaonecha.utils.DataUtils
import com.example.weatherapp_juanarizaonecha.utils.HistoricUtils

class CityDetailActivity : AppCompatActivity() {
    private val view by lazy { ActivityCityDetailBinding.inflate(layoutInflater) }
    private lateinit var lastHistoricalCity: CityHistory //To have the last reportID
    private val repository: CrudAPI by lazy {
        SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREFERENCES_NAME,
                MODE_PRIVATE
            )
        )
    }

    private val dao : SQLiteCityDao by lazy { init() }

    private fun init() : SQLiteCityDao {
        val dao = SQLiteCityDao()
        dao.setContext(this)
        return dao
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.root)
        val intent: Intent = intent

        val str = intent.getStringExtra("city")
        val city = getCity(str!!,false)!!

        view.twCityDet.text = city.name
        Shared.setWeatherData(city,this)
        view.cbFavourite.isChecked = city.favourite

       oneReportPerDay(city)

        view.cbFavourite.setOnCheckedChangeListener { _, isChecked ->
            city.favourite = isChecked
            if(city.favourite){
                repository.save(city.name)
            } else {
                repository.delete(city.name)
            }
        }

        view.cbSaveReport.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                val historicalCity: CityHistory = toCityHistory(city)
                dao.insert(historicalCity)
                HistoricUtils.setCity(historicalCity)
                changeRepInUserCities(city,true)
                lastHistoricalCity = historicalCity
            } else {
                dao.delete(lastHistoricalCity)
                HistoricUtils.removeCity(lastHistoricalCity)
                changeRepInUserCities(city,false)
                Toast.makeText(this,"Report Removed", Toast.LENGTH_SHORT).show()
            }
        }

        view.btnHistoricalData.setOnClickListener {
            val citiesReports = HistoricUtils.getCities(city.name)
            if(citiesReports.isNotEmpty()) {
                val i = Intent(this,HistoricalDataActivity::class.java)
                i.putExtra("History_City",city.name)
                startActivity(i)
            } else {
                Toast.makeText(this,"No reports saved in ${city.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Save the actual changes in the reports
    private fun changeRepInUserCities(searched: City, saved: Boolean) {
        DataUtils.user.cities.forEach { city ->
                if(city.isCity(searched.name)) {
                    city.reported = saved
                    if(saved){
                        //Save datetime of last report
                        city.dateTimeLastReport = searched.forecasts[0].getDayAndMonth()
                    }
                }
        }
    }

    private fun oneReportPerDay(city: City) {
        //No reports saved today --> Only able to save one report per day
        if(HistoricUtils.sameDateTime(city.name,city.forecasts[0].datetime)) {
            view.cbSaveReport.isEnabled = false
        }
    }

}