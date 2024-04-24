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
        UsersSingleton.fillCities() //Load the 10 cities with all the data stored
        val cityDefault = "Zaragoza"
        loadAnimations()
        setGpsLocation(cityDefault)
        setWeatherData(cityDefault)

        view.btnCitiesList.setOnClickListener{
            startActivity(Intent(this,CitiesListActivity::class.java))
        }

        view.btnFavouriteCities.setOnClickListener {
            startActivity(Intent(this,FavouriteCitiesActivity::class.java))
        }
    }

    private fun setGpsLocation(city: String) {
        val gpsLocation = WeatherDataSingleton.resolvedAddress[city] + "\n( " +
                WeatherDataSingleton.latitude[city].toString() + "," +
                WeatherDataSingleton.longitude[city].toString() + ")"
        view.twGpsLocation.text = gpsLocation
    }

    private fun setWeatherData(city: String) {
        view.twDescription.text = WeatherDataSingleton.descriptions[city]

        val dateViews = listOf(view.tw00, view.tw10, view.tw20, view.tw30, view.tw40)
        val tempViews = listOf(view.tw01, view.tw11, view.tw21, view.tw31, view.tw41)
        val tempMaxViews = listOf(view.tw02, view.tw12, view.tw22, view.tw32, view.tw42)
        val tempMinViews = listOf(view.tw03, view.tw13, view.tw23, view.tw33, view.tw43)

        val numDays = dateViews.size
        for (i in 0 until  numDays) {
            dateViews[i].text = WeatherDataSingleton.dateTimesDAYS[city]?.get(i)
            tempViews[i].text = WeatherDataSingleton.temperaturesDAYS[city]?.get(i).toString()
            tempMaxViews[i].text = WeatherDataSingleton.tempMaxDAYS[city]?.get(i).toString()
            tempMinViews[i].text = WeatherDataSingleton.tempMinDAYS[city]?.get(i).toString()
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