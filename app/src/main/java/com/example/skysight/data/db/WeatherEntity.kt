package com.example.skysight.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "Weather")
data class WeatherData(
    @PrimaryKey
    val datetime: String,
    val address: String,
    val tempmax: Double,
    val tempmin: Double,
    val temp: Double,
    val feelslike: Double,
    val humidity: Double,
    val precip: Double?,
    val precipprob: Double,
    val windspeed: Double,
    val winddir: Double,
    val cloudcover: Double,
    val uvindex: Int,
    val sunrise: String,
    val sunset: String,
    val moonphase: Double,
    val conditions: String,
    val icon: String,
    val aqius: Int?,
    val fetchedTime: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(Date()),
    val oldTempMin: Double,
    val oldTempMax: Double

)