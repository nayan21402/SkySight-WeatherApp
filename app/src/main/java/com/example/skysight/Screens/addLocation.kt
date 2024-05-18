package com.example.skysight.Screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skysight.R
import com.example.skysight.data.NetworkObserver
import com.example.skysight.data.Status
import com.example.skysight.ui.theme.gotham
import com.example.skysight.viewModel.WeatherViewModel
import kotlinx.coroutines.flow.first


@Composable
fun LocationScreen(viewModel: WeatherViewModel,networkObserver: NetworkObserver,context:Context,onClick: ()->Unit){
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val backgroundImage: Painter = painterResource(id = R.drawable.day_cloudy)
    var showInputfield by remember {
        mutableStateOf(false)
    }
    var deleteEnabled by remember {
        mutableStateOf(false)
    }
    val netStatus = networkObserver.observe().collectAsState(initial = Status.Unavailable).value

    Image(
        painter = backgroundImage,
        contentDescription = "Background",modifier = Modifier
            .fillMaxSize()
        , contentScale = ContentScale.FillBounds)


    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .padding(15.dp)
            .blur(animateDpAsState(targetValue = if (!showInputfield) 0.dp else 5.dp).value),
        topBar = {
        Row(modifier = Modifier
            .background(Color.Transparent)
            .padding(top = statusBarHeight)
        ){
            //ui.currentLocation?.let { Text(text = it) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Image(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Change Location",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = {
                            onClick()

                        })
                )

            }
            Divider(color = Color.Black)}
    }, floatingActionButton = {FloatingActionButton(
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            onClick = {
                if(netStatus==Status.Available)
                    showInputfield=true
                else
                    Toast.makeText(context,"Cannot Add Location:\n NET UNAVAILABLE",Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .border(2.dp, Color.Black, RoundedCornerShape(20))
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add, // Use the default Add icon
                contentDescription = "Add",
                modifier = Modifier.size(40.dp) // Adjust icon size if necessary
            )
        }}) { it ->
        val ui=viewModel.uiState.collectAsState().value
        val locationList = viewModel.uiState.collectAsState().value.locationList
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Locations", fontFamily = gotham, fontSize = 20.sp)
                    IconButton(onClick = { deleteEnabled=!deleteEnabled }) {
                        Image(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

            }
           item {
               GpsWeather(cityName = ui.gpsLocation,viewModel,if(ui.currentLocation==ui.gpsLocation) true else false,deleteEnabled)
               Spacer(modifier = Modifier.height(5.dp))
           }


            items(locationList.toList()){i->
                viewModel.getWeatherForLocation(i){

                }
                CityWeather(cityName = i,viewModel,if(ui.currentLocation==i) true else false,deleteEnabled)
                Spacer(modifier = Modifier.height(5.dp))
            }

        }


    }
    if (showInputfield){
        AlertDialogWithInput(onConfirm = { it->
            showInputfield=false
            viewModel.AddLocationText(it)
            viewModel.setWeatherFetched(false)
            viewModel.setWeatherForcastFetched(false)
            viewModel.setWLiveweatherFetched(false)
        })
        {
            showInputfield=false

        }
    }


}

@Composable
fun CityWeather(cityName: String,viewModel: WeatherViewModel, isSelected: Boolean,deleteEnabled : Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                animateDpAsState(
                    targetValue = if (!deleteEnabled) {
                        if (isSelected) 2.5.dp else 0.dp
                    } else 2.dp
                ).value,
                animateColorAsState(targetValue = if (deleteEnabled) Color.Red else Color.Black).value,
                RoundedCornerShape(20)
            )
            .padding(20.dp)
            .clickable(enabled = !deleteEnabled) {
                viewModel.setLocationText(cityName)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
            if(viewModel.uiState.collectAsState().value.gpsLocation==cityName)
                Text(text = "Gps Location", fontFamily = gotham, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
            Text(text = cityName, fontFamily = gotham ,modifier = Modifier.weight(0.6f))
            IconButton(onClick = { viewModel.deleteLocation(cityName) }, enabled = deleteEnabled) {
                Image(imageVector = Icons.Filled.Close, contentDescription = "Delete Location", modifier = Modifier.alpha(
                    animateFloatAsState(targetValue = if(deleteEnabled) 1f else 0f).value))
            }

    }
}

@Composable
fun GpsWeather(cityName: String,viewModel: WeatherViewModel, isSelected: Boolean,deleteEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                animateDpAsState(targetValue = if (isSelected) 2.5.dp else 0.dp).value,
                Color.Black,
                RoundedCornerShape(20)
            )
            .padding(20.dp)
            .clickable(enabled = !deleteEnabled) {
                viewModel.setLocationText(cityName)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(viewModel.uiState.collectAsState().value.gpsLocation==cityName)
            Text(text = "Gps Location", fontFamily = gotham, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
        Text(text = cityName, fontFamily = gotham ,modifier = Modifier.weight(0.6f))

    }
}

@Composable
fun AlertDialogWithInput(onConfirm: (String) -> Unit, onDismiss: ()->Unit) {
    // Create a mutable state to track the input text
    val locationText = remember { mutableStateOf("") }
    // Create a mutable state to track whether the dialog is open or not

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

            AlertDialog(
                onDismissRequest = { onDismiss()},
                containerColor = Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.75f),
                title = { Text("Add Location", fontFamily = gotham, color = Color.Black) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(
                            value = locationText.value,
                            onValueChange = { locationText.value = it },
                            label = { Text("Location") },
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Black, unfocusedIndicatorColor = Color.White, cursorColor = Color.Black, focusedLabelColor = Color.Black, unfocusedLabelColor = Color.Black),
                            textStyle = TextStyle(fontFamily = gotham, color = Color.Black)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        onConfirm(locationText.value)
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)) {
                        Text("OK", fontFamily = gotham)
                    }
                }
            )

    }
}
