package com.example.weatherapp_juanarizaonecha

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.databinding.ActivityCityDetailBinding

class CityDetailActivity : AppCompatActivity() {
    private val view by lazy { ActivityCityDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.root)
        val intent: Intent = intent

        val str = intent.getStringExtra("city")
        val params = str?.split(".  ")
        val params2 = params!![1].split("     ")
        val cityStr = params2[0]
        lateinit var city: City
        DataUtils.cities.forEach { cCity ->
            if(cCity.isCity(cityStr)){
                city = cCity
            }
        }

        view.twCityDet.text = city.name
        view.cbFavourite.isChecked = city.favourite

        view.cbFavourite.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                city.favourite = true
            } else {
                city.favourite = false
            }
        }
    }
}