package com.example.weatherapp_juanarizaonecha.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.weatherapp_juanarizaonecha.database.CityDatabaseHelper

class SQLiteCityDao: IDao<CityHistory> {
    private var helper: CityDatabaseHelper? = null

    private lateinit var db: SQLiteDatabase

    private var TABLE = "Cities"

    fun setContext(context: Context) {
        helper = CityDatabaseHelper(context, "CityDB", null, 2)
    }

    //Inserts a city with the forecast of a datetime
    override fun insert(element: CityHistory): CityHistory {
        db = helper!!.writableDatabase
        val city = ContentValues()
        city.put("dateTime",element.dateTime)
        city.put("name", element.name)
        city.put("tempMax", element.tempMax)
        city.put("tempMin", element.tempMin)
        city.put("temper", element.temper)
        city.put("precipProb", element.precipProb)
        city.put("windSpeed", element.windSpeed)
        city.put("latitude",element.latitude)
        city.put("longitude",element.longitude)
        city.put("dateTimeStr",element.dateTimeStr)
        val id = db.insert(TABLE, null, city)
        element.reportID = id //Save reportID in the CityHistory instance
        db.close()
        Log.d("INSERT IN DB","INSERT ${element.reportID}: ${element.name}")
        return findOne(id)!!
    }

    override fun delete(element: CityHistory): Int {
        db = helper!!.writableDatabase
        val result = db.delete(TABLE, "reportID = ?", arrayOf(element.reportID.toString()))
        db.close()
        Log.d("DELETE IN DB","DELETE ${element.reportID}: ${element.name}")
        return result
    }

    override fun findAll(): List<CityHistory> {
        db = helper!!.readableDatabase
        val cities = mutableListOf<CityHistory>()
        val cursor = db.rawQuery("SELECT reportID, dateTime, name, tempMin, tempMax, temper, precipProb," +
                " windSpeed, latitude, longitude, dateTimeStr FROM $TABLE", arrayOf())
        if (cursor.moveToFirst()) {
            do {
                val reportID = cursor.getLong(0)
                val dateTime = cursor.getString(1)
                val name = cursor.getString(2)
                val tempMin = cursor.getFloat(3)
                val tempMax = cursor.getFloat(4)
                val temper = cursor.getFloat(5)
                val precipProb = cursor.getFloat(6)
                val windSpeed = cursor.getFloat(7)
                val latitude = cursor.getFloat(8)
                val longitude = cursor.getFloat(9)
                val dateTimeStr = cursor.getString(10)
                val city = CityHistory(dateTime, name, tempMin, tempMax, temper, precipProb, windSpeed,
                    latitude,longitude,dateTimeStr)
                city.reportID = reportID
                cities.add(city)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return cities
    }

    override fun findOne(id: Long): CityHistory? {
        db = helper!!.readableDatabase
        var city: CityHistory? = null
        val cursor = db.rawQuery("SELECT reportID, dateTime, name, tempMin, tempMax, temper, precipProb, windSpeed,"
                + "latitude, longitude, dateTimeStr FROM $TABLE WHERE reportID = ?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            do {
                val dateTime = cursor.getString(1)
                val name = cursor.getString(2)
                val tempMin = cursor.getFloat(3)
                val tempMax = cursor.getFloat(4)
                val temper = cursor.getFloat(5)
                val precipProb = cursor.getFloat(6)
                val windSpeed = cursor.getFloat(7)
                val latitude = cursor.getFloat(8)
                val longitude = cursor.getFloat(9)
                val dateTimeStr = cursor.getString(10)
                city = CityHistory(dateTime, name, tempMin, tempMax, temper, precipProb, windSpeed,latitude,
                    longitude, dateTimeStr)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return city
    }

    override fun update(element: CityHistory): Int {
        TODO("Not needed")
    }

    fun findReportsByCity(cityName: String): List<CityHistory> {
        db = helper!!.readableDatabase
        val cities = mutableListOf<CityHistory>()
        val cursor = db.rawQuery("SELECT reportID, dateTime, name, tempMin, tempMax, temper, precipProb," +
                " windSpeed, latitude, longitude, dateTimeStr" +
                " FROM $TABLE WHERE name = ? ORDER BY dateTime ASC", arrayOf(cityName))
        if (cursor.moveToFirst()) {
            do {
                val reportID = cursor.getLong(0)
                val dateTime = cursor.getString(1)
                val name = cursor.getString(2)
                val tempMin = cursor.getFloat(3)
                val tempMax = cursor.getFloat(4)
                val temper = cursor.getFloat(5)
                val precipProb = cursor.getFloat(6)
                val windSpeed = cursor.getFloat(7)
                val latitude = cursor.getFloat(8)
                val longitude = cursor.getFloat(9)
                val dateTimeStr = cursor.getString(10)
                val city = CityHistory(dateTime, name, tempMin, tempMax, temper, precipProb, windSpeed,
                    latitude, longitude, dateTimeStr)
                city.reportID = reportID
                cities.add(city)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return cities
    }

}