package com.example.weatherapp_juanarizaonecha

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherapp_juanarizaonecha.dao.SQLiteCityDao
import com.example.weatherapp_juanarizaonecha.databinding.ActivitySplashScreenBinding
import com.example.weatherapp_juanarizaonecha.sharedpreferences.CrudAPI
import com.example.weatherapp_juanarizaonecha.sharedpreferences.SHARED_PREFERENCES_NAME
import com.example.weatherapp_juanarizaonecha.sharedpreferences.SharedPreferencesRepository
import com.example.weatherapp_juanarizaonecha.utils.CityRequest
import com.example.weatherapp_juanarizaonecha.utils.DataUtils
import com.example.weatherapp_juanarizaonecha.utils.HistoricUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val view by lazy { ActivitySplashScreenBinding.inflate(layoutInflater) }
    private lateinit var flpc: FusedLocationProviderClient
    private val repository: CrudAPI by lazy {
        SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREFERENCES_NAME,
                MODE_PRIVATE
            )
        )
    }
    private val dao : SQLiteCityDao by lazy { init() }
    private fun init() : SQLiteCityDao {
        val dao = SQLiteCityDao()
        dao.setContext(this)
        return dao
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        flpc = LocationServices.getFusedLocationProviderClient(this)
        startRotatingImageLogo()

        val scope = CoroutineScope(Dispatchers.IO)
        val jobs = mutableListOf<Job>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) { //REQUEST LOCATION PERMISIONS
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }

        flpc.lastLocation.addOnSuccessListener { location->
            DataUtils.latitude = location.latitude
            DataUtils.longitude = location.longitude

            DataUtils.citiesRequest.forEach { cityReq ->
                val job = scope.launch {
                    val apiData = fetchDataFromApi(cityReq)
                    DataUtils.fillData(cityReq, apiData)
                }
                jobs.add(job)
            }

            scope.launch {
                jobs.joinAll()
                withContext(Dispatchers.Main) {
                    DataUtils.setCitiesIntoList()
                    DataUtils.setFavCities(repository)
                    loadAllReports()
                    navigateToMainActivity(this@SplashScreenActivity)
                }
            }
        }

    }


    private fun startRotatingImageLogo() {
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation)
        val rotateAnimationInverse = AnimationUtils.loadAnimation(this, R.anim.rotate_animation_inverse)
        view.imGearLU.startAnimation(rotateAnimation)
        view.imGearLD.startAnimation(rotateAnimationInverse)
        view.imGearRU.startAnimation(rotateAnimationInverse)
        view.imGearRD.startAnimation(rotateAnimation)
    }

    private fun fetchDataFromApi(cityRequest: CityRequest): String {
        var result = ""
        val url = cityRequest.getUrl()
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val inSt = BufferedInputStream(urlConnection.inputStream)
            result = readStream(inSt)
        } finally {
            urlConnection.disconnect()
        }
        return result
    }

    private fun readStream(inputStream : InputStream) : String {
        val br = BufferedReader(InputStreamReader(inputStream))
        val total = StringBuilder()
        while (true) {
            val line = br.readLine() ?: break
            total.append(line).append('\n')
        }
        return total.toString()
    }


    private fun loadAllReports() {
        DataUtils.user.cities.forEach { city ->
            val histories = dao.findReportsByCity(city.name)
            //HistoricUtils.setAllCities(histories)
            if(histories.isNotEmpty()){
                city.reported = true
                city.dateTimeLastReport = histories[histories.size -1].dateTime //Last report time
            } else {
                city.reported = false
            }
        }
        HistoricUtils.setAllCities(dao.findAll())
    }

    private fun navigateToMainActivity(context: Context) {
        val intent = Intent(context,MainScreenActivity::class.java)
        startActivity(intent)
        finish() //Out of the Stack
    }
}