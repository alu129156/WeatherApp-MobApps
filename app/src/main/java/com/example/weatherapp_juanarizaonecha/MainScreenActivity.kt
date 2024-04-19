package com.example.weatherapp_juanarizaonecha

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.databinding.ActivityMainScreenBinding
class MainScreenActivity : AppCompatActivity() {
    private val view by lazy { ActivityMainScreenBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
    }

}