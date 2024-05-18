package com.example.skysight.backgroundService

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skysight.R
import com.example.skysight.data.DataRepository
import com.example.skysight.data.NetworkObserver
import com.example.skysight.data.api.WeatherApi
import com.example.skysight.data.db.WeatherData
import com.example.skysight.data.db.WeatherDatabase

class worker(val context: Context, val parameters: WorkerParameters) : CoroutineWorker(context,parameters) {

    override suspend fun doWork(): Result {

        val connectivityObserver = NetworkObserver(context)
        val repository = DataRepository(WeatherDatabase.getDatabase(context).itemDao(), WeatherApi,connectivityObserver)
        var weatherData : WeatherData? = null
        val liveData = repository.liveWeather("New Delhi"){
            if (it != null) {
                weatherData = it
                val notification = createNotification(applicationContext, weatherData!!)
                showNotification(applicationContext, notification)
            }
        }

       // repository.fetchWeather("New Delhi",{})
        // Indicate whether the work finished successfully or failed
        return Result.success()
    }

    private fun createNotification(context: Context,weatherData: WeatherData): Notification {
        val iconPainterMap = mapOf(
            "snow" to R.drawable.snow,
            "rain" to R.drawable.rain,
            "fog" to R.drawable.fog,
            "wind" to R.drawable.wind,
            "cloudy" to R.drawable.cloudy,
            "partly-cloudy-day" to R.drawable.partly_cloudy_day,
            "partly-cloudy-night" to R.drawable.partly_cloudy_night,
            "clear-day" to R.drawable.sunny,
            "clear-night" to R.drawable.new_moon,
            "thunder" to R.drawable.thunder ,
            "new_moon" to R.drawable.new_moon ,
            "wan_gib" to R.drawable.waning__gibbous ,
            "wan_cre" to R.drawable.waning_crescent ,
            "third_quart" to R.drawable.third_quarter ,
            "wax_cre" to R.drawable.waxing_crescent ,
            "wax_gib" to R.drawable.waxing_gibbous ,
            "full_moon" to R.drawable.full_moon ,
            "first_quart" to R.drawable.first_quarter ,
            "drop" to R.drawable.water_drop,
        )
        // Create the notification using NotificationCompat.Builder
        val builder = NotificationCompat.Builder(context, "1")
            .setSmallIcon(weatherData.icon?.let { iconPainterMap[it] } ?: R.drawable.partly_cloudy_day)
            .setContentTitle("New Delhi")
            .setContentText("${weatherData.temp}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Return the built notification
        return builder.build()
    }

    private fun showNotification(context: Context, notification: Notification) {
        // Show the notification using NotificationManagerCompat
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(1, notification)
    }



}