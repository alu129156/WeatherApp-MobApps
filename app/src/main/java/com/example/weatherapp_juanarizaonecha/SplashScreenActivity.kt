package com.example.weatherapp_juanarizaonecha

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val view by lazy { ActivitySplashScreenBinding.inflate(layoutInflater) }
    private val fileName = "cties.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        startRotatingImageLogo()
        val scope = CoroutineScope(Dispatchers.IO) //IO operations
        val jobs = mutableListOf<Job>()

        DataUtils.citiesRequest.forEach { cityReq ->
            val job = scope.launch {
                val apiData = fetchDataFromApi(cityReq)
                DataUtils.fillData(cityReq, apiData)
            }
            jobs.add(job)
        }

        // Mueve la lógica de espera a una corrutina en IO
        scope.launch {
            jobs.joinAll()
            withContext(Dispatchers.Main) {
                DataUtils.setCitiesIntoList()
                navigateToMainActivity()
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

    private fun navigateToMainActivity() {
        val intent = Intent(this@SplashScreenActivity,MainScreenActivity::class.java)
        startActivity(intent)
        finish() //Out of the Stack
    }

    private fun writeFile(text: String) {
        var fos : FileOutputStream? = null
        fos = openFileOutput(fileName, MODE_PRIVATE)
        fos.write(text.toByteArray())
        fos?.close()
        Log.d("TAG1", "File save in $filesDir/$fileName")
    }
}