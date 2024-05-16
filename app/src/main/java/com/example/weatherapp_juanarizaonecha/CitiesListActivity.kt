package com.example.weatherapp_juanarizaonecha

import com.example.weatherapp_juanarizaonecha.filtering.SearchFilterController
import com.example.weatherapp_juanarizaonecha.utils.DataUtils


class CitiesListActivity : SearchFilterBaseActivity() {
    override var cities = DataUtils.user.cities
    override var sfCities = SearchFilterController(cities)
    override val isFavActivity = false
    override fun onResume() {
        this.cities = DataUtils.user.cities
        super.onResume()
    }
}