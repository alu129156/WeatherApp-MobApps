package com.example.weatherapp_juanarizaonecha.sharedpreferences

interface CrudAPI {

    fun save(value: String)

    fun delete(value: String)

    fun list(): Set<String>

    fun contains(value: String): Boolean

    fun clear()
}