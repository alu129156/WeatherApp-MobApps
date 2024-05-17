package com.example.weatherapp_juanarizaonecha

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.databinding.ActivityMainScreenBinding
import com.example.weatherapp_juanarizaonecha.shared.Shared
import com.example.weatherapp_juanarizaonecha.utils.City
import com.example.weatherapp_juanarizaonecha.utils.DataUtils
import com.example.weatherapp_juanarizaonecha.utils.WeatherCities

class MainScreenActivity : AppCompatActivity() {
    private val view by lazy { ActivityMainScreenBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        val cityDefault = findCityDefault()
        loadAnimations()
        if (cityDefault != null) {
            setLocation(cityDefault, DataUtils.latitude, DataUtils.longitude)
            Shared.setWeatherData(cityDefault, this)
        }

        view.btnCitiesList.setOnClickListener {
            startActivity(Intent(this, CitiesListActivity::class.java))
        }

        view.btnFavouriteCities.setOnClickListener {
            startActivity(Intent(this, FavouriteCitiesActivity::class.java))
        }
    }

    private fun findCityDefault(): City? {
        DataUtils.user.cities.forEach { city ->
            if (city.isCity(WeatherCities.Zaragoza.name)) {
                return city
            }
        }
        return null
    }

    private fun setLocation(city: City, latitude: Double, longitude: Double) {
        val gpsLocation = city.resolvedAdress + "\n      ( " +
                String.format("%.3f",latitude) + "," +
                String.format("%.3f",longitude) + ")"
        view.twGpsLocation.text = gpsLocation
    }

    private fun loadAnimations() {
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.moon_rotation)
        val rotateAnimationInv = AnimationUtils.loadAnimation(this, R.anim.moon_rotation_inverse)
        view.imMoon.startAnimation(rotateAnimation) //Starting first animation and listening it
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                view.imMoon.startAnimation(rotateAnimationInv) //Start the inverse when finished the first
            }
        })
    }
}
