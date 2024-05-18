package com.example.skysight.data

import android.util.Log
import com.example.skysight.data.api.CurrentConditions
import com.example.skysight.data.api.Day
import com.example.skysight.data.api.WeatherApi
import com.example.skysight.data.db.WeatherDao
import com.example.skysight.data.db.WeatherData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DataRepository(
    private val db: WeatherDao,
    private val weatherApi: WeatherApi,
    private val connectivityObserver: NetworkObserver
) {

    suspend fun fetchWeather(address: String, callback: (WeatherData?) -> Unit) {
        val currDate = Date()
        val date = convertDateToString(currDate)

        val existingData = db.getWeather("$date $address", address)

        if (existingData != null) {
            callback(existingData)
            Log.d("weather", existingData.toString())
        } else {
            val networkStatus = connectivityObserver.observe().first()
            if (networkStatus == Status.Available) {
                try {
                    val apiResponse = weatherApi.retrofitService.getWeather(location = address)
                    val oldData = fetchOldWeather(address)
                    Log.d("weather", apiResponse.toString())
                    db.insert(mapCurrentConditionToWeatherData(apiResponse.currentConditions, address, oldData))
                    apiResponse.days.forEach {
                        db.insert(mapDayToWeatherData(it, address))
                    }
                    callback(mapDayToWeatherData(apiResponse.days[0], address))
                } catch (e: Exception) {
                    Log.e("DataRepository", "Error fetching weather data", e)
                    callback(null) // Indicate failure
                }
            } else {
                Log.w("DataRepository", "Network unavailable, unable to fetch weather data")
                callback(null) // Indicate no network
            }
        }
    }

    suspend fun fetchOldWeather(address: String): Pair<Double, Double> {
        val currentDate = Date()
        val calendar = Calendar.getInstance()

        val averageMinTemperatures = mutableListOf<Double>()
        val averageMaxTemperatures = mutableListOf<Double>()

        for (i in 1..10) {
            calendar.time = currentDate
            calendar.add(Calendar.YEAR, -i)
            val date = convertDateToString(calendar.time)
            val apiResponse = weatherApi.retrofitService.getOldWeather(location = address, date)
            val min = apiResponse.days[0].tempmin
            val max = apiResponse.days[0].tempmax
            Log.d("weather", apiResponse.toString())
            averageMinTemperatures.add(min)
            averageMaxTemperatures.add(max)
        }

        val avgMin = calculateAverage(averageMinTemperatures)
        val avgMax = calculateAverage(averageMaxTemperatures)
        return Pair(avgMin, avgMax)
    }

    fun calculateAverage(temperatures: List<Double>): Double {
        if (temperatures.isEmpty()) return 0.0
        return temperatures.sum() / temperatures.size
    }

    suspend fun liveWeather(address: String, callback: (WeatherData?) -> Unit) {
        val currDate = Calendar.getInstance().time
        val existingData = db.getWeather("0000$address", address)

        if (existingData != null && isWithinOneHour(existingData.fetchedTime, currDate)) {
            Log.d("weather", existingData.toString())
            callback(existingData)
        } else {
            val networkStatus = connectivityObserver.observe().first()
            if (networkStatus == Status.Available) {
                try {
                    val apiResponse = weatherApi.retrofitService.getWeather(location = address)
                    val newWeatherData = existingData?.let {
                        mapCurrentConditionToWeatherData(apiResponse.currentConditions, address, Pair(it.oldTempMin, it.oldTempMax))
                    }
                    if (newWeatherData != null) {
                        db.insert(newWeatherData)
                        callback(newWeatherData)
                    } else {
                        callback(null) // Indicate failure
                    }
                    Log.d("weather", apiResponse.toString())
                } catch (e: Exception) {
                    Log.e("DataRepository", "Error fetching live weather data", e)
                    callback(null) // Indicate failure
                }
            } else {
                Log.w("DataRepository", "Network unavailable, unable to fetch live weather data")
                callback(null) // Indicate no network
            }
        }
    }

    suspend fun getWeatherForecast(address: String, callback: (List<WeatherData>?) -> Unit) {
        val currDate = LocalDate.now()
        val endDate = currDate.plusDays(7)
        val startDateString = currDate.toString()
        val endDateString = endDate.toString()
        val existingData = db.getWeatherForecast("$startDateString $address", "$endDateString $address", address)

        if (existingData.isNotEmpty()) {
            callback(existingData)
        } else {
            val networkStatus = connectivityObserver.observe().first()
            if (networkStatus == Status.Available) {
                try {
                    val weatherDataList = mutableListOf<WeatherData>()
                    val apiResponse = weatherApi.retrofitService.getWeather(location = address)
                    apiResponse.days.forEach { day ->
                        val weatherData = mapDayToWeatherData(day, address)
                        weatherDataList.add(weatherData)
                        db.insert(weatherData)
                    }
                    callback(weatherDataList)
                } catch (e: Exception) {
                    Log.e("DataRepository", "Error fetching weather forecast", e)
                    callback(null) // Indicate failure
                }
            } else {
                Log.w("DataRepository", "Network unavailable, unable to fetch weather forecast")
                callback(null) // Indicate no network
            }
        }
    }

    suspend fun deleteLocation(address: String){
        db.delete(address)
    }

    suspend fun getWeatherTable(callback: (List<WeatherData>) -> Unit) {
        db.getTable().collect { it ->
            callback(it)
        }
    }

    private fun convertDateToString(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun mapDayToWeatherData(day: Day, address: String): WeatherData {
        return WeatherData(
            datetime = day.datetime + " $address",
            address = address,
            tempmax = day.tempmax,
            tempmin = day.tempmin,
            temp = day.temp,
            feelslike = day.feelslike,
            humidity = day.humidity,
            precip = day.precip,
            precipprob = day.precipprob,
            windspeed = day.windspeed,
            winddir = day.winddir,
            cloudcover = day.cloudcover,
            uvindex = day.uvindex,
            sunrise = day.sunrise,
            sunset = day.sunset,
            moonphase = day.moonphase,
            conditions = day.conditions,
            icon = day.icon,
            aqius = day.aqius,
            oldTempMin = 0.0,
            oldTempMax = 0.0
        )
    }

    private fun mapCurrentConditionToWeatherData(day: CurrentConditions, address: String, oldData: Pair<Double, Double>): WeatherData {
        return WeatherData(
            datetime = "0000$address",
            address = address,
            tempmax = 0.0,
            tempmin = 0.0,
            temp = day.temp,
            feelslike = day.feelslike,
            humidity = day.humidity,
            precip = day.precip,
            precipprob = day.precipprob,
            windspeed = day.windspeed,
            winddir = day.winddir,
            cloudcover = day.cloudcover,
            uvindex = day.uvindex,
            sunrise = day.sunrise,
            sunset = day.sunset,
            moonphase = day.moonphase,
            conditions = day.conditions,
            icon = day.icon,
            aqius = day.aqius,
            oldTempMin = oldData.first,
            oldTempMax = oldData.second
        )
    }

    fun isWithinOneHour(oldDateString: String, newDate: Date): Boolean {
        val oldDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).parse(oldDateString)
        val diff = newDate.time - oldDate.time
        val diffInHours = diff / (1000 * 60 * 60)
        return diffInHours <= 1
    }

}
