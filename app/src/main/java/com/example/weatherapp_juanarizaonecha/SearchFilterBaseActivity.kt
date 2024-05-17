package com.example.weatherapp_juanarizaonecha


import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.adapter.CustomCitiesAdapter
import com.example.weatherapp_juanarizaonecha.databinding.ActivitySearchFilterBaseBinding
import com.example.weatherapp_juanarizaonecha.filtering.Filters
import com.example.weatherapp_juanarizaonecha.filtering.SearchFilterController
import com.example.weatherapp_juanarizaonecha.filtering.getCity
import com.example.weatherapp_juanarizaonecha.sharedpreferences.CrudAPI
import com.example.weatherapp_juanarizaonecha.sharedpreferences.SHARED_PREFERENCES_NAME
import com.example.weatherapp_juanarizaonecha.sharedpreferences.SharedPreferencesRepository
import com.example.weatherapp_juanarizaonecha.utils.City

abstract class SearchFilterBaseActivity : AppCompatActivity() {
    private val view by lazy { ActivitySearchFilterBaseBinding.inflate(layoutInflater) }
    protected abstract var cities: MutableList<City>
    protected abstract var sfCities : SearchFilterController
    protected abstract val isFavActivity: Boolean
    private lateinit var adapter: CustomCitiesAdapter
    private val repository: CrudAPI by lazy {
        SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREFERENCES_NAME,
                MODE_PRIVATE
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.root)

        val lwCitiesList = view.lwCitiesList
        val swCitiesList : SearchView = view.swCitiesList

        this.adapter = CustomCitiesAdapter(this,cities,sfCities,isFavActivity,repository)
        lwCitiesList.adapter = adapter

        swCitiesList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateNewDataFromAdapter()
                val filteredCities = sfCities.search(newText!!)
                val auxSFCities = SearchFilterController(filteredCities)

                when (adapter.filterOPT) {
                    Filters.COORDINATES -> updateAdapter(auxSFCities.filterByCoordinates())
                    Filters.TEMPERATURE -> updateAdapter(auxSFCities.filterByTemperature())
                    else -> updateAdapter(auxSFCities.filterByWindSpeed())
                }
                return false
            }
        })

        view.btnFiltWindSp.setOnClickListener {
            adapter.filterOPT = Filters.WIND_SPEED
            updateNewDataFromAdapter()
            updateAdapter(sfCities.filterByWindSpeed())
        }

        view.btnFiltTemp.setOnClickListener {
            adapter.filterOPT = Filters.TEMPERATURE
            updateNewDataFromAdapter()
            updateAdapter(sfCities.filterByTemperature())
        }

        view.btnFiltCoord.setOnClickListener {
            adapter.filterOPT = Filters.COORDINATES
            updateNewDataFromAdapter()
            updateAdapter(sfCities.filterByCoordinates())
        }

        lwCitiesList.setOnItemClickListener { _,_,position,_ ->
            val intent = Intent(this, CityDetailActivity::class.java)
            intent.putExtra("city", adapter.getItem(position).toString())
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        sfCities = SearchFilterController(cities)
        adapter.clear()
        adapter.clearListeners()
        adapter.addAll(sfCities.filterByCoordinates())
        adapter.notifyDataSetChanged()
    }
    private fun updateAdapter(filter: List<SpannableString>) {
        adapter.clear()
        adapter.addAll(filter)
        adapter.notifyDataSetChanged()
    }

    private fun updateNewDataFromAdapter() {
        //Only modifies the list when are changes from the listView adapter
        if(adapter.dataChanged()) {
            val items = List(adapter.count) { index -> adapter.getItem(index) }
            val updatedCities = mutableListOf<City>()
            items.forEach { item ->
                val city = getCity(item.toString(),true)!!
                updatedCities.add(city)
            }

            cities = updatedCities
            sfCities = SearchFilterController(cities)
            adapter.clearListeners() //Remove the listeners from removed cities
        }
    }
}
