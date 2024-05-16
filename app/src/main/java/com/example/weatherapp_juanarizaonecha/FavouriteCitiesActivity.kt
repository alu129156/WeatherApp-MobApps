package com.example.weatherapp_juanarizaonecha

import com.example.weatherapp_juanarizaonecha.filtering.SearchFilterController
import com.example.weatherapp_juanarizaonecha.shared.Shared

class FavouriteCitiesActivity : SearchFilterBaseActivity() {
    override var cities = Shared.getFavCities()
    override var sfCities = SearchFilterController(this.cities)
    override val isFavActivity = true

    override fun onResume() {
        this.cities = Shared.getFavCities()
        super.onResume()
    }
}