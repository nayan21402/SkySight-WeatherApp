package com.example.skysight.Screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.skysight.MainActivity
import com.example.skysight.R
import com.example.skysight.backgroundService.worker
import com.example.skysight.data.DataRepository
import com.example.skysight.data.api.WeatherApi
import com.example.skysight.data.db.WeatherDatabase
import com.example.skysight.ui.theme.SkySightTheme
import com.example.skysight.viewModel.WeatherViewModel
import java.util.concurrent.TimeUnit

class SplashActivity : ComponentActivity() {
    lateinit var viewModel: WeatherViewModel
    lateinit var repository: DataRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        ), navigationBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT
        ))

        setContent {
            SkySightTheme {
                SplashScreenPage()
            }
        }

        // Start MainActivity after 1 second
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the SplashActivity to prevent going back to it when pressing back
        }, 2500) //
    }

}

@Composable
fun SplashScreenPage(){
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    WeatherBackground2(imageId = R.drawable.splash_screen)
    {
        Scaffold(
            modifier = Modifier.padding(15.dp),
            containerColor = Color.Transparent,
            topBar = {
                Row(modifier = Modifier
                    .background(Color.Transparent)
                    .padding(top = statusBarHeight, start = 5.dp, end = 5.dp)
                ){
                }
            }
        ) {it ->

            Column(modifier = Modifier
                .padding(
                    it
                )
                .fillMaxWidth()
                .fillMaxSize()
            ) {
                val context = LocalContext.current

            }
            }

        }
    }


@Composable
fun WeatherBackground2(imageId: Int,content: @Composable () -> Unit){
    Box{
        Image(painter = painterResource(id = imageId), contentDescription = "sunny",
            modifier = Modifier
                .fillMaxSize()
            , contentScale = ContentScale.FillBounds)
        content()
    }
}