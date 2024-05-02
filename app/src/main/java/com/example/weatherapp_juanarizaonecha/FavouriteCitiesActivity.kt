package com.example.weatherapp_juanarizaonecha



import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class FavouriteCitiesActivity : SearchFilterBaseActivity() {
    override var cities = getFavCities()
    override var sfCities = SearchFilterController(this.cities)
    override val isFavActivity = true
    override fun onResume() {
        this.cities = getFavCities()
        super.onResume()
    }
}