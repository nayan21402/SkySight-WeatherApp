package com.example.skysight

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.skysight.Gemini.chatScreenActivity
import com.example.skysight.Screens.LocationScreen
import com.example.skysight.Screens.OnBoardingScreen
import com.example.skysight.Screens.homeScreen
import com.example.skysight.backgroundService.worker
import com.example.skysight.data.DataRepository
import com.example.skysight.data.NetworkObserver
import com.example.skysight.data.api.WeatherApi
import com.example.skysight.data.db.WeatherDatabase
import com.example.skysight.ui.theme.SkySightTheme
import com.example.skysight.viewModel.WeatherViewModel
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    lateinit var viewModel: WeatherViewModel
    lateinit var repository: DataRepository
    lateinit var connectivityObserver: NetworkObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        ), navigationBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        ))
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        connectivityObserver = NetworkObserver(applicationContext)
        repository = DataRepository(WeatherDatabase.getDatabase(applicationContext).itemDao(), WeatherApi,connectivityObserver)
        viewModel= WeatherViewModel(repository,sharedPreferences)

        setContent {
            backgroundService(applicationContext)

            val navCont = rememberNavController()
            navCont.addOnDestinationChangedListener { _, destination, _ ->
                // Log the current destination
                Log.d("Current Screen", "Current destination: ${destination.route}")
            }
            SkySightTheme {
                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                 viewModel.setLocationGps(applicationContext)
                // viewModel.setLocationText("Los Angles")
                }
                NavHost(navController = navCont, modifier = Modifier.fillMaxSize(), startDestination = if (isFirstTimeLaunch(applicationContext)) "OnBoarding" else "Home") {
                    composable("OnBoarding") {
                        OnBoardingScreen(viewModel = viewModel, context = applicationContext) {
                            navCont.navigate("Home") {
                                // Pop all destinations up to "Home" from the back stack
                                popUpTo("Home") { inclusive = true }
                            }
                        }
                    }
                    composable("Home") {
                        homeScreen(viewModel = viewModel, onClick =  { navCont.navigate("Locations") }) { it ->
                            val intent = Intent(applicationContext, chatScreenActivity::class.java).apply {
                                putExtra("weather", it)
                            }
                            startActivity(intent)
                        }
                    }
                    composable("Locations") {
                        LocationScreen(viewModel = viewModel,connectivityObserver,applicationContext) {
                            navCont.navigate("Home") {
                                // Pop "Locations" from the back stack when navigating to "Home"
                                popUpTo("Home") { inclusive = true }
                            }
                        }
                    }
                }

            }


        }
    }
    private fun isFirstTimeLaunch(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            // Update the flag to indicate that the app has been launched before
            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        }
        return isFirstTime
    }

    private fun backgroundService(context: Context) {
        // Create a periodic work request to run every hour
        val workRequest = PeriodicWorkRequest.Builder(
            worker::class.java,
            1, // Repeat interval
            TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "example"
            val descriptionText = "test description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun scheduleNotificationAtSpecificTime(context: Context,time:Int) {
        // Calculate the time when you want to send the notification
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0) // Set hour (24-hour format)
        calendar.set(Calendar.MINUTE, 0) // Set minute
        calendar.set(Calendar.SECOND, 0) // Set second

        // Get the current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()

        // Calculate the initial delay until the specified time
        val delayMillis = calendar.timeInMillis - currentTimeMillis

        // Build the OneTimeWorkRequest with the initial delay
        val workRequest = OneTimeWorkRequest.Builder(worker::class.java)
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()

        // Enqueue the work request
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}


@Preview(showBackground = true)
@Composable
fun WeatherButtonsPreview() {
    SkySightTheme {

    }
}