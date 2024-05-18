package com.example.skysight.viewModel

import com.example.skysight.data.db.WeatherData

data class ui(val gpsLocation : String ="",val locationList: Set<String> = mutableSetOf(), var currentLocation: String? = null, var LiveWeatherData: WeatherData? = null, var TodayData: WeatherData? = null, var weatherForecast: List<WeatherData>?= emptyList())