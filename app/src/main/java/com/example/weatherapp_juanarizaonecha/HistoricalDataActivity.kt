package com.example.weatherapp_juanarizaonecha

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.dao.CityHistory
import com.example.weatherapp_juanarizaonecha.databinding.ActivityHistoricalDataBinding
import com.example.weatherapp_juanarizaonecha.utils.HistoricUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class HistoricalDataActivity : AppCompatActivity() {
    private val view by lazy { ActivityHistoricalDataBinding.inflate(layoutInflater) }
    private lateinit var temps: MutableList<Float>
    private lateinit var barChart: BarChart
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        val intent = intent
        val cities = HistoricUtils.getCities(intent.getStringExtra("History_City")!!)

        view.twInitHistoric.text = "HISTORY IN ${cities[0].name}"
        val temps = mutableListOf<Float>()
        val tempsMax = mutableListOf<Float>()
        val tempsMin = mutableListOf<Float>()
        val windsSpeed = mutableListOf<Float>()
        val precipProbs = mutableListOf<Float>()

        cities.forEach { city ->
            temps.add(city.temper)
            tempsMax.add(city.tempMax)
            tempsMin.add(city.tempMin)
            windsSpeed.add(city.windSpeed)
            precipProbs.add(city.precipProb)
        }
        this.temps = temps
        barChart = findViewById(R.id.barChart)
        val windSp = getAVG(windsSpeed)
        val precP = getAVG(precipProbs)
        setWeatherImage(windSp,precP)

        selectFirstAndLastDatetime(cities)
        view.twHistPrecip.text = "   Average % precipitation: " + String.format("%.2f", precP) + "%"
        view.twHistTempMax.text = "   Average MAX temperature: " + String.format("%.2f", getAVG(tempsMax)) + "ºC"
        view.twHistTempMin.text = "   Average MIN temperature: " + String.format("%.2f", getAVG(tempsMin)) + "ºC"
        view.twHistWind.text = "   Average Wind speed: " + String.format("%.2f", windSp) + "km/h"

        setBarChart()
    }

    private fun setWeatherImage(windSp: Float, precP: Float) {
        val windy = 20 //Km/h
        val rainy = 40 //%
        if(windSp > windy && precP < rainy) { // More important rain than wind
            view.imWindHist.visibility = View.VISIBLE
            view.imRainHist.visibility = View.GONE
            view.imSunHist.visibility = View.GONE
        } else if(precP >= rainy) {
            view.imRainHist.visibility = View.VISIBLE
            view.imWindHist.visibility = View.GONE
            view.imSunHist.visibility = View.GONE
        } else { //temp to be sunny
            view.imSunHist.visibility = View.VISIBLE
            view.imRainHist.visibility = View.GONE
            view.imWindHist.visibility = View.GONE
        }
    }

    private fun getAVG(values: List<Float>): Float {
        var res = 0f
        values.forEach { value ->
            res += value
        }
        return res / values.size.toFloat()
    }

    private fun setBarChart() {
        val entries: ArrayList<BarEntry> = ArrayList()

        // Añadir los datos al gráfico
        for (i in temps.indices) {
            entries.add(BarEntry(i.toFloat(), temps[i]))
        }

        val barDataSet = BarDataSet(entries, "Temperatures")

        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val data = BarData(barDataSet)
        barChart.data = data

        barChart.description.isEnabled = false

        barChart.invalidate()
    }

    private fun selectFirstAndLastDatetime(cities: List<CityHistory>) {
        lateinit var lastCity: CityHistory
        var first = true    //First reportID == firstDateTime
        var text = "   From "
        cities.forEach { city ->
            if(first) {
                text += city.dateTimeStr + " to "
                first = false
            }
            lastCity = city     //Last reportID == lastDateTime
        }
        text += lastCity.dateTimeStr
        view.twHistDate.text = text
    }
}

