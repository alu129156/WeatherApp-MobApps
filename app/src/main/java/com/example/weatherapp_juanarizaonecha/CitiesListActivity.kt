package com.example.weatherapp_juanarizaonecha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.databinding.ActivityCitiesListBinding

class CitiesListActivity : SearchFilterBaseActivity() {
    override var cities = DataUtils.cities
    override var sfCities = SearchFilterController(cities)
    override val isFavActivity = false
    override fun onResume() {
        this.cities = DataUtils.cities
        super.onResume()
    }
}