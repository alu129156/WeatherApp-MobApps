package com.example.weatherapp_juanarizaonecha.dao

/**
 * Insert, delete, findAll, findOne, update
 *
 */
interface IDao<T> {

    fun insert(element : T) : T
    fun delete(element: T) : Int
    fun findAll() : List<T>
    fun findOne(id: Long) : T?
    fun update(element: T) : Int
}