package com.example.weatherapp_juanarizaonecha

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_juanarizaonecha.databinding.ActivityNoWifiConnectionBinding

class NoWifiConnectionActivity : AppCompatActivity() {
    private val view by lazy { ActivityNoWifiConnectionBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
    }
}