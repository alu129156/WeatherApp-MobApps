@file:Suppress("DEPRECATION")

package com.example.weatherapp_juanarizaonecha


import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.databinding.ActivitySplashScreenBinding
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val view by lazy { ActivitySplashScreenBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        startRotatingImageLogo()
        loadData()
    }

    private fun loadData() {
        val data = DataExtraction(this)
        data.execute()
    }


    public fun navigateToMainScreen() {
        val intent = Intent(this, MainScreenActivity::class.java)
        startActivity(intent)
        finish() // Activity out of the stack
    }

    private fun startRotatingImageLogo() {
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation)
        val rotateAnimationInverse = AnimationUtils.loadAnimation(this, R.anim.rotate_animation_inverse)
        view.imGearLU.startAnimation(rotateAnimation)
        view.imGearLD.startAnimation(rotateAnimationInverse)
        view.imGearRU.startAnimation(rotateAnimationInverse)
        view.imGearRD.startAnimation(rotateAnimation)
    }

}

class DataExtraction(activity: SplashScreenActivity) : AsyncTask<Unit, Unit, String>() {
    private val activityReference = WeakReference(activity)
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Unit?): String {
        return fetchDataFromApi()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("Singleton.instance.data = result"))
    override fun onPostExecute(result: String) {
        processData(result)
        //print(WeatherDataSingleton.temperaturesHOUR)
        activityReference.get()?.navigateToMainScreen()
    }

    private fun fetchDataFromApi(): String {
        var result = ""
        val url = URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/41.65606,-0.87734?key=X4L4EFE3SE4UUWFRSNTVRHWWB")
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


    private fun processData(jsonData: String) {
        WeatherDataSingleton.fillDataWeather(jsonData)
    }
}

//PRUEBAS
/*
private fun fetchDataFromApi(): String {
    var result = ""
    val url = URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/41.65606,-0.87734?key=X4L4EFE3SE4UUWFRSNTVRHWWB")
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

private fun processWeatherData(jsonData: String) {
    WeatherDataSingleton.fillData(jsonData)
}

fun main(){
    processWeatherData(fetchDataFromApi())
    print(WeatherDataSingleton.descriptions.get(0).get(0))
    print('\n')
    print(WeatherDataSingleton.temperaturesHOUR.get(0))
    //print('\n')
    //print(WeatherDataSingleton.hourlyForecasts)
}
*/