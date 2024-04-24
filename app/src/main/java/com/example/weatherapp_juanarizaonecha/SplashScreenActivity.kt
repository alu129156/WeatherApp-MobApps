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

class DataExtraction(activity: SplashScreenActivity) : AsyncTask<Unit, Unit, Map<String,String>>() {
    // I use HashMap to ensure that pairs city->apiResponse is well ordered

    private val activityReference = WeakReference(activity)
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Unit?): Map<String,String> {
        val results = mutableMapOf<String,String>();
        WeatherDataSingleton.cities.keys.map{ city ->
            results[city] = fetchDataFromApi(city)
            persistDataInFile(city,results[city]!!)
        }
        return results
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(results: Map<String,String>) {
        results.keys.map{ city ->
            WeatherDataSingleton.fillDataWeather(city,results[city]!!) //Process data
        }
        activityReference.get()?.navigateToMainScreen()
    }

    private fun fetchDataFromApi(city: String): String {
        var result = ""
        val countryISO = WeatherDataSingleton.cities[city]!!
        val url = URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city},${countryISO}?key=X4L4EFE3SE4UUWFRSNTVRHWWB")
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

    private fun persistDataInFile(city: String, data: String) {
        //FALTA IMPLEMENTAR
        val file = "${city}.txt"
    }
}

/*
fun main(){
    val results = mutableMapOf<String,String>();
    WeatherDataSingleton.citiesCoordinates.keys.map{ city ->
        print(city+"\n")
        results.put(city,fetchDataFromApi(city))
    }
    results.keys.map{ city ->
        WeatherDataSingleton.fillDataWeather(city,results[city]!!) //Process data
    }
    print(WeatherDataSingleton.temperaturesDAYS["Moscow"])
    print("\n")
    print(WeatherDataSingleton.precipprobsDAYS["Beijing"])
    print("\n")
    print(WeatherDataSingleton.descriptionsDAYS["Zaragoza"])
}

private fun fetchDataFromApi(city: String): String {
    var result = ""
    val coordinates = WeatherDataSingleton.citiesCoordinates[city]!!
    val url = URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${coordinates[0]},${coordinates[1]}?key=X4L4EFE3SE4UUWFRSNTVRHWWB")
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
*/
