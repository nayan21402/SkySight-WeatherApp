package com.example.skysight.Screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.skysight.R
import com.example.skysight.ui.theme.gotham
import com.example.skysight.viewModel.WeatherViewModel


@Composable
fun OnBoardingScreen(context: Context,viewModel: WeatherViewModel,onComplete: () -> Unit){

    var permission by remember {
        mutableStateOf(false)
    }
    val locPermission = locationPermissionRequestAndObtain(viewModel = viewModel, context = LocalContext.current){
        permission=true
    }
    WeatherBackground(imageId = R.drawable.day_sunny) {
        var loc by remember {
            mutableStateOf("NA")
        }
        loc=viewModel.uiState.collectAsState().value.currentLocation.toString()
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "Welcome to SkySight!\n Please allow location and Notification permission to get accurate weather data.", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontFamily = gotham, fontWeight = FontWeight.Medium, color = Color.Black)
            Button(
                onClick = {
                    locPermission.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.ACCESS_FINE_LOCATION) )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Text(text = "Give Location Permission", fontFamily = gotham)
            }

            if(permission){
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(viewModel.weatherFetched.collectAsState().value && viewModel.liveWeatherFetched.collectAsState().value && viewModel.weatherForecastFetched.collectAsState().value){
                        onComplete()
                    }
                }
                else {
                    Toast.makeText(context,"Location Permission Denied:\n Can't Proceed",Toast.LENGTH_LONG).show()
                }
            }


        }

    }


}



@Composable
fun locationPermissionRequestAndObtain(viewModel: WeatherViewModel, context: Context,onLaunched:()->Unit): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult: Map<String, Boolean> ->
        val locationPermissionGranted = permissionsResult[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (locationPermissionGranted) {
            viewModel.setLocationGps(context)
            onLaunched()

        }

    }
    return requestPermissionLauncher
}




