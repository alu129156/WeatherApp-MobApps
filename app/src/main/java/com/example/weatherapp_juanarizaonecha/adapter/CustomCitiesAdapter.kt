package com.example.weatherapp_juanarizaonecha.adapter

import android.content.Context
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherapp_juanarizaonecha.filtering.Filters
import com.example.weatherapp_juanarizaonecha.R
import com.example.weatherapp_juanarizaonecha.filtering.SearchFilterController
import com.example.weatherapp_juanarizaonecha.filtering.getCity
import com.example.weatherapp_juanarizaonecha.shared.Shared
import com.example.weatherapp_juanarizaonecha.sharedpreferences.CrudAPI
import com.example.weatherapp_juanarizaonecha.utils.City
import java.util.Stack

class CustomCitiesAdapter(context: Context, private var cities: MutableList<City>, private var sfCities: SearchFilterController,
                          private val isFavActivity: Boolean, private val repository: CrudAPI) :
    ArrayAdapter<SpannableString>(context, 0, sfCities.filterByCoordinates()) {

    private var listeners = Stack<DataChangeListener>()
    var filterOPT = Filters.COORDINATES
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent,false)
        val cityName = view.findViewById<TextView>(R.id.twCity)
        val btnFavorite = view.findViewById<Button>(R.id.btnFav)
        val tvFav = view.findViewById<TextView>(R.id.tvHeart)
        val report = view.findViewById<ImageView>(R.id.imSavedReport)
        val noReport = view.findViewById<ImageView>(R.id.imNoReport)
        val twDateItem = view.findViewById<TextView>(R.id.twDateItem)

        val citySpan = getItem(position)
        val city = getCity(citySpan.toString(),false)!! //The cities here are updated, no report
        cityName.text = citySpan


        btnFavorite.text = updateFavIcon(city)

        if(isFavActivity) {
            //FOR THE IMAGE REPORTS
            if(city.reported) {
                report.visibility = View.VISIBLE
                noReport.visibility = View.GONE
                twDateItem.visibility = View.VISIBLE
                twDateItem.text = city.dateTimeLastReport //Has text only if has a report
            } else {
                noReport.visibility = View.VISIBLE
                report.visibility = View.GONE
                twDateItem.visibility = View.GONE
            }

            tvFav.visibility = View.GONE
            btnFavorite.visibility = View.VISIBLE

            btnFavorite.setOnClickListener {
                city.favourite = !city.favourite
                btnFavorite.text = updateFavIcon(city)
                if(!city.favourite) { //Update the ListView
                    updateData()
                }
            }
        } else {
            report.visibility = View.GONE
            noReport.visibility = View.GONE
            btnFavorite.visibility = View.GONE
            tvFav.visibility = View.VISIBLE
            tvFav.text = updateFavIcon(city)
            twDateItem.visibility = View.GONE
        }
        return view
    }

    private fun updateData() {
        //Only adds a listener when are changes in fav activity
        val listener = object : DataChangeListener {
            override fun onDataChanged() {}
        }
        listeners.add(listener)
        clear()
        cities = Shared.getFavCities()
        storeDataInRepository()
        sfCities = SearchFilterController(cities)

        when (filterOPT) {
            Filters.COORDINATES -> addAll(sfCities.filterByCoordinates())
            Filters.TEMPERATURE -> addAll(sfCities.filterByTemperature())
            else -> addAll(sfCities.filterByWindSpeed())
        }
        notifyDataSetChanged()
    }
    private fun updateFavIcon(city: City): String {
        if(city.favourite) {
            return "\u2764" //Heart
        }
        return ""
    }

    fun dataChanged(): Boolean {
        return listeners.isNotEmpty()
    }

    fun clearListeners() {
        if(listeners.isNotEmpty()) {
            listeners.clear()
        }
    }

    private fun storeDataInRepository() {
        repository.clear()
        cities.forEach { city->
            if(city.favourite) {
                repository.save(city.name)
            }
        }
    }
}

interface DataChangeListener {
    fun onDataChanged()
}
