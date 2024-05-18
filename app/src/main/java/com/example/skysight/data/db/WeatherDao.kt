package com.example.skysight.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: WeatherData)

    @Update
    fun update(data: WeatherData)

    @Query("DELETE FROM Weather WHERE address=:address ")
    fun delete(address: String)

    @Query("SELECT * FROM Weather WHERE datetime=:date AND address=:address ")
    fun getWeather(date: String, address: String): WeatherData?

    @Query("SELECT * FROM Weather WHERE datetime BETWEEN :startDate AND :endDate AND address = :address")
    fun getWeatherForecast(startDate: String, endDate: String, address: String): List<WeatherData>

    @Query("SELECT * from Weather")
    fun getTable(): Flow<List<WeatherData>>


}