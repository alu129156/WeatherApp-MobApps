package com.example.weatherapp_juanarizaonecha.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CityDatabaseHelper(context: Context?, database: String?,
                         cursorFactory: SQLiteDatabase.CursorFactory?,
                         version: Int): SQLiteOpenHelper(context, database, cursorFactory, version) {
    companion object {
        private const val CREATE_TABLE = "CREATE TABLE Cities (reportID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ",dateTime TEXT, name TEXT, tempMax REAL, tempMin REAL, temper REAL, precipProb REAL," +
                " windSpeed REAL,latitude REAL, longitude REAL, dateTimeStr TEXT)"
        private const val DROP_TABLE = "DROP TABLE IF EXISTS City"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(DROP_TABLE)
        db.execSQL(CREATE_TABLE)
    }
}