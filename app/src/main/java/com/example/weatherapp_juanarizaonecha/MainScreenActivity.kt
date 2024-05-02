package com.example.weatherapp_juanarizaonecha

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.databinding.ActivityMainScreenBinding
class MainScreenActivity : AppCompatActivity() {
    private val view by lazy { ActivityMainScreenBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        val cityDefault = findCityDefault()
        loadAnimations()
        if (cityDefault != null) {
            setGpsLocation(cityDefault)
            setWeatherData(cityDefault)
        }

        view.btnCitiesList.setOnClickListener{
            startActivity(Intent(this,CitiesListActivity::class.java))
        }

        view.btnFavouriteCities.setOnClickListener {
            startActivity(Intent(this,FavouriteCitiesActivity::class.java))
        }
    }

    private fun findCityDefault(): City? {
        DataUtils.cities.forEach { city ->
            if(city.isCity(WeatherCities.Zaragoza.name)){
                return city;
            }
        }
        return null;
    }

    private fun setGpsLocation(city: City) {
        val gpsLocation = city.resolvedAdress + "\n( " +
                city.latitude.toString() + "," +
                city.longitude.toString() + ")"
        view.twGpsLocation.text = gpsLocation
    }

    private fun setWeatherData(city: City) {
        view.twDescription.text = city.description

        val dateViews = listOf(view.tw00, view.tw10, view.tw20, view.tw30, view.tw40)
        val tempViews = listOf(view.tw01, view.tw11, view.tw21, view.tw31, view.tw41)
        val tempMaxViews = listOf(view.tw02, view.tw12, view.tw22, view.tw32, view.tw42)
        val tempMinViews = listOf(view.tw03, view.tw13, view.tw23, view.tw33, view.tw43)

        val numDays = dateViews.size
        for (i in 0 until  numDays) {
            dateViews[i].text = city.forecasts[i].getDayAndMonth()
            tempViews[i].text = city.forecasts[i].temperature.getDegrees()
            tempMaxViews[i].text = city.forecasts[i].temperatureMax.getDegrees()
            tempMinViews[i].text = city.forecasts[i].temperatureMin.getDegrees()
        }
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