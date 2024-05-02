package com.example.weatherapp_juanarizaonecha


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import com.example.weatherapp_juanarizaonecha.databinding.ActivitySearchFilterBaseBinding
import kotlin.properties.Delegates

abstract class SearchFilterBaseActivity : AppCompatActivity() {
    private val view by lazy { ActivitySearchFilterBaseBinding.inflate(layoutInflater) }
    protected abstract var cities: MutableList<City>
    protected abstract var sfCities : SearchFilterController
    protected abstract val isFavActivity: Boolean
    protected lateinit var adapter: ArrayAdapter<SpannableString>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //if(!isFavActivity) {setContentView(view.root)}
        setContentView(view.root)

        val lwCitiesList = view.lwCitiesList
        val swCitiesList : SearchView = view.swCitiesList

        this.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            sfCities.filterByCoordinates(isFavActivity))

            lwCitiesList.adapter = adapter

        swCitiesList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredCities = sfCities.search(newText!!)
                val auxSFCities = SearchFilterController(filteredCities)
                adapter.clear()
                adapter.addAll(auxSFCities.filterByCoordinates(isFavActivity))
                adapter.notifyDataSetChanged()
                return false
            }
        })

        view.btnFiltWindSp.setOnClickListener {
            adapter.clear()
            adapter.addAll(sfCities.filterByWindSpeed(isFavActivity))
        }

        view.btnFiltTemp.setOnClickListener {
            adapter.clear()
            adapter.addAll(sfCities.filterByTemperature((isFavActivity)))
        }

        view.btnFiltCoord.setOnClickListener {
            adapter.clear()
            adapter.addAll(sfCities.filterByCoordinates((isFavActivity)))
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
        adapter.addAll(sfCities.filterByCoordinates(isFavActivity))
    }
}
