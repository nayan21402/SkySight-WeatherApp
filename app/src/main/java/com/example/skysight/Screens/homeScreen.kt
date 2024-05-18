package com.example.skysight.Screens

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skysight.R
import com.example.skysight.ui.theme.gotham
import com.example.skysight.viewModel.WeatherViewModel
import com.example.skysight.viewModel.ui
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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


val backgroundMap = mapOf(
    "fog" to R.drawable.day_cloudy,
    "cloudy" to R.drawable.day_cloudy,
    "wind" to R.drawable.day_cloudy,
    "partly-cloudy-day" to R.drawable.day_partial_cloudy,
    "snow" to R.drawable.day_partial_cloudy,
    "rain" to R.drawable.day_raingy,
    "clear-day" to R.drawable.day_sunny,
    "clear-night" to R.drawable.night_clear,
    "partly-cloudy-night" to R.drawable.night_rainy,
    "thunder" to R.drawable.night_thunder,
)

fun getDayOfWeek(dateString: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf.parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = date
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    // Convert day of week to string
    return when (dayOfWeek) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> throw IllegalArgumentException("Invalid day of week")
    }
}

fun con(fahrenheit: Double): String {
    if (CFswitch){ return "${fahrenheit.toInt()}°F"}
    else {
        val temp = (fahrenheit - 32) * 5 / 9
        return "${temp.toInt()}°C"
    }
}

var CFswitch by mutableStateOf(false)
var colo by mutableStateOf(Color.Black)

@Composable
fun homeScreen(viewModel: WeatherViewModel, onClick : ()-> Unit, aiAct : (String) -> Unit){
    var ui = viewModel.uiState.collectAsState().value
    Log.d("home",ui.locationList.toString())
    Log.d("home",ui.currentLocation.toString())
    Log.d("home",ui.TodayData.toString())
    Log.d("home",ui.LiveWeatherData.toString())

    if(ui.TodayData==null || ui.weatherForecast==null){
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            viewModel.getWeather(ui.gpsLocation)
            CircularProgressIndicator(color = Color.Black)
        }
    }
    else{
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        var switchState by remember { mutableStateOf(false) }

        if (ui.LiveWeatherData?.icon.toString()  == "clear-night" || ui.LiveWeatherData?.icon.toString()  == "partly-cloudy-night" )
        {
            colo = Color.White
        }
        else {colo = Color.Black}
        if(ui.LiveWeatherData==null){
            ui.LiveWeatherData=ui.TodayData
        }

        WeatherBackground(
            imageId = (if (ui.LiveWeatherData?.icon == null) R.drawable.day_cloudy
            else {
                if (ui.LiveWeatherData!!.icon in backgroundMap) backgroundMap[ui.LiveWeatherData!!.icon]
                else R.drawable.day_cloudy
            }) ?: R.drawable.day_cloudy
        )
        {
            Scaffold(
                modifier = Modifier.padding(15.dp),
                containerColor = Color.Transparent,
                topBar = {
                    Row(modifier = Modifier
                        .background(Color.Transparent)
                        .padding(top = statusBarHeight, start = 5.dp, end = 5.dp)
                    ){
                        //ui.currentLocation?.let { Text(text = it) }
                        val loc=viewModel.uiState.collectAsState().value.currentLocation

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
                                    .clickable(onClick = { onClick() }),
                                colorFilter = ColorFilter.tint(colo))

                            IconButton(onClick = { if (loc != null) {
                                viewModel.getLiveWeather(
                                    loc
                                )
                            }}) {
                                Image(imageVector = Icons.Filled.Refresh,
                                    contentDescription = "refresh",
                                    colorFilter = ColorFilter.tint(colo))

                            }
                            IconButton(onClick = { aiAct(ui.toString()) }) {
                                Image(painterResource(id = R.drawable.ai_icon),
                                    contentDescription = "Ai",
                                    colorFilter = ColorFilter.tint(colo))

                            }
                            Switcher(roundUp = switchState, onRoundUpChanged = { switchState = it
                                CFswitch=it}, colo)

                        }
                        Divider(color = Color.Black)

                    }
                }
            ) {it ->

                Column(modifier = Modifier
                    .padding(
                        it
                    )
                    .fillMaxWidth()
//                .fillMaxSize()
                ) {
                    TopContent(ui)

                    Spacer(modifier = Modifier.height(15.dp))
                    LazyColumn {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))

//                        LazyRow {
//                            items(12) {
//                                Hour_container(){}
//                                Spacer(modifier = Modifier.width(3.dp))
//                            }
//                        }

//                            Spacer(modifier = Modifier.height(8.dp))
                            ui.weatherForecast?.forEachIndexed(){ index, weather ->
                                if(index+1<7)
                                    Week_container (ui,index+1)

                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Compare(ui){}

                            Spacer(modifier = Modifier.height(8.dp))
                            Past_results(ui){} // can go on new sreen when pressed Compare
                            Spacer(modifier = Modifier.height(8.dp))


                            Extras(ui)

                            Spacer(modifier = Modifier.height(8.dp))
                            Moon_container(ui){}

                            Spacer(modifier = Modifier.height(8.dp))
                            Sun_container (ui){}

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }


                }

            }
        }
    }
    }




@Composable
fun WeatherBackground(imageId: Int,content: @Composable () -> Unit){
    Box{
        Image(painter = painterResource(id = imageId), contentDescription = "sunny",
            modifier = Modifier
                .fillMaxSize()
            , contentScale = ContentScale.FillBounds)
        content()
    }
}

@Composable
fun Switcher(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    colo: Color){
    Switch(
        checked = roundUp,
        onCheckedChange = {
            onRoundUpChanged(it)
            CFswitch = it
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = colo,
            uncheckedThumbColor = colo,
            checkedTrackColor = Color.Transparent,
            uncheckedTrackColor = Color.Transparent,
            checkedBorderColor = colo,
            uncheckedBorderColor = colo
        )
    )
}

@Composable
fun TopContent(ui: ui) {
        Divider(color = colo)
//        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = ui.LiveWeatherData?.address.toString(),
                    fontFamily = gotham,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = colo
                )
                Spacer(modifier = Modifier.height(16.dp))
                val day = ui.TodayData?.let { getDayOfWeek(it.datetime) }
                Text(
                    text = "$day - ${ui.TodayData?.datetime}",
                    fontFamily = gotham,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = colo
                )
            }
            iconPainterMap[ui.LiveWeatherData?.icon]?.let { painterResource(id = it) }?.let {
                Image(
                    painter = it,
                    contentDescription = ui.LiveWeatherData?.icon,
                    modifier = Modifier.size(80.dp),
                    colorFilter = ColorFilter.tint(colo)
                )
            }
        }
//        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "${ui.LiveWeatherData?.temp?.let { con(it) }}",
            fontFamily = gotham,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 0.sp,
            fontSize = 120.sp,
            color = colo
//            style = TextStyle(
//                fontSize = 170.sp
//            ),
//            modifier = Modifier
//                .padding(0.dp)
        )

    Row (modifier = Modifier
        .fillMaxWidth()
//        .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.15f)),
        .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = "${ui.LiveWeatherData?.icon}",
            fontFamily = gotham,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colo)
        Text(text = "${ui.TodayData?.tempmax?.let { con(it) }} / ${ui.TodayData?.tempmin?.let {
            con(
                it
            )
        }}",
            fontFamily = gotham,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colo)
    }
}

@Composable
fun Suggestions(ui:ui){
    Column(modifier = Modifier
        .clip(RoundedCornerShape(7))
        .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.25f))
        .padding(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = 8.dp
        )
    ) {
        Row (modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Things you can do today",
                fontFamily = gotham,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = colo
            )
//            iconPainterMap[ui.LiveWeatherData?.icon]?.let { painterResource(id = it) }?.let {
                Image(
//                    painter = it,
                    painterResource(id = R.drawable.ai_icon),
                    contentDescription = ui.LiveWeatherData?.icon,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(colo)
                )
//            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(
            color = colo
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "Enjoy the sun with a scoop of ice cream delight. Every bite, a taste of sunshine",
            fontFamily = gotham,
            fontWeight = FontWeight.Light,
            fontSize = 10.sp,
            color = colo
        )

    }

}

@Composable
fun Extras(ui: ui){
//    Box(modifier = Modifier
//        .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.15f))
//        .padding(8.dp)
//        .clip(RoundedCornerShape(15))
//    ){
//
//    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7))
            .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ui.TodayData?.humidity?.let {
                    SingleExtra(stringResource(R.string.humidity),
                        it
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                ui.TodayData?.uvindex?.let { SingleExtra(stringResource(R.string.uv_index), it.toDouble()) }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ui.TodayData?.let { SingleExtra(stringResource(R.string.precipitation), it.precipprob) }
                Spacer(modifier = Modifier.height(5.dp))
                ui.TodayData?.let { SingleExtra(stringResource(R.string.wind_speed), it.windspeed) }
//            SingleExtra(stringResource(R.string.precipitation), 1.0f)
//            Spacer(modifier = Modifier.height(5.dp))
//            SingleExtra(stringResource(R.string.wind_speed), 1.0f)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
@Composable
fun SingleExtra(heading: String , value: Double){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column (
        ){
            Text(
                text = heading,
                fontFamily = gotham,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colo
            )
            Text(
//                text = "$value %", if (heading == R.string.humidity or heading == R.string.precipitation) else "$value",
                text = if (heading == R.string.humidity.toString() || heading == R.string.precipitation.toString()) {
                    "$value %"
                } else {
                    "$value"
                },
                fontFamily = gotham,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                color = colo
            )
        }
        Spacer(modifier = Modifier.width(35.dp))

        if (heading == "Wind Speed") {
            Image(
                painter = painterResource(id = R.drawable.wind),
                contentDescription = "Extra info icon",
                modifier = Modifier
                    .size(20.dp),
                colorFilter = ColorFilter.tint(colo)
            )
        }

        else if (heading == "Precipitation") {
            Image(
                painter = painterResource(id = R.drawable.water_drop),
                contentDescription = "Extra info icon",
                modifier = Modifier
                    .size(20.dp),
                colorFilter = ColorFilter.tint(colo)
            )
        }

        else if (heading == "UV Index") {
            Image(
                painter = painterResource(id = R.drawable.fog),
                contentDescription = "Extra info icon",
                modifier = Modifier
                    .size(20.dp),
                colorFilter = ColorFilter.tint(colo)
            )
        }

        else {
            Image(
                painter = painterResource(id = R.drawable.humidity),
                contentDescription = "Extra info icon",
                modifier = Modifier
                    .size(20.dp),
                colorFilter = ColorFilter.tint(colo)
            )
        }

    }
}

@Composable
fun Hour_container(modifier: Modifier=Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.15f))
            .padding(8.dp)
            .clip(RoundedCornerShape(15))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp) // Add padding to the column
        ) {
            Text(
                text = "2 pm",
                fontFamily = gotham,
                fontWeight = FontWeight.Normal,
                style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp),
                color = colo
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.sunny),
                contentDescription = "sunny",
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "35°C",
                fontFamily = gotham,
                fontWeight = FontWeight.Normal,
                style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp),
                color = colo
            )

        }
    }
}

@Composable
fun Week_container(ui: ui, wday: Int, modifier: Modifier = Modifier){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7))
            .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.25f))
            .padding(
                top = 8.dp,
                start = 20.dp,
                end = 30.dp,
                bottom = 8.dp
            )

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically // Align vertically centered
        ) {
            Text(
                text = "${ui.weatherForecast?.get(wday)?.datetime?.let { getDayOfWeek(it) }}",
                fontFamily = gotham,
                fontWeight = FontWeight.Normal,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = colo
                )
            )

            Spacer(modifier = Modifier.weight(1f)) // Flexible spacer

            Image(
                painter = painterResource(id = R.drawable.water_drop),
                contentDescription = "precipitation",
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(colo)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "${ui.weatherForecast?.get(wday)?.precipprob}%",
                fontFamily = gotham,
                fontWeight = FontWeight.Normal,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,
                    color = colo
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            iconPainterMap[ui.weatherForecast?.get(wday)?.icon]?.let { painterResource(it) }?.let {
                Image(
                    painter = it,
                    contentDescription = ui.weatherForecast?.get(2)?.icon,
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(colo)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${ui.weatherForecast?.get(wday)?.let { con(it.tempmax) }} / ${ui.weatherForecast?.get(wday)?.let { con(it.tempmin) }}",
                fontFamily = gotham,
                fontWeight = FontWeight.Normal,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,
                    color = colo
                )
            )
        }
    }
}

@Composable
fun Moon_container(ui: ui, modifier: Modifier = Modifier,  content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(7))
            .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.25f))
            .padding(8.dp)
            .fillMaxWidth()

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp) // Add padding to the column
        ) {
            Text(
                text = "MOON PHASE",
                fontFamily = gotham,
                fontWeight = FontWeight.Medium,
                style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                color = colo
            )

            Spacer(modifier = Modifier.height(4.dp))

            Divider(color = colo)

            Spacer(modifier = Modifier.height(8.dp))

            val moonPhase = ui.TodayData?.moonphase
            val moonImage = moonPhase?.let { phase ->
                when {
                    phase == 0.0 -> "new_moon"
                    phase in 0.0..0.25 -> "wax_cre"
                    phase == 0.25 -> "first_quart"
                    phase in 0.25..0.5 -> "wax_gib"
                    phase == 0.5 -> "full_moon"
                    phase in 0.5..0.75 -> "wan_gib"
                    phase == 0.75 -> "third_quart"
                    phase in 0.75..1.0 -> "wan_cre"
                    else -> "new_moon"
                }
            } ?: "new_moon"

            val imageResourceId = iconPainterMap[moonImage] ?: R.drawable.new_moon

            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = moonImage,
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(colo)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${ui.TodayData?.moonphase}",
                fontFamily = gotham,
                fontWeight = FontWeight.Normal,
                style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                color = colo
            )

        }
    }
}

@Composable
fun Sun_container(ui: ui, modifier: Modifier = Modifier,  content: @Composable () -> Unit ) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(7))
            .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.25f))
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column (modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "SUNRISE/SUNSET",
                fontFamily = gotham,
                fontWeight = FontWeight.Medium,
                style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                color = colo
            )
            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = colo)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Left column for "sunrise"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp) // Add padding to the column
                ) {

                    Spacer(modifier = Modifier.height(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.sunrise),
                        contentDescription = "sunrise",
                        modifier = Modifier.size(48.dp),
                        colorFilter = ColorFilter.tint(colo)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${ui.TodayData?.sunrise} am",
                        fontFamily = gotham,
                        fontWeight = FontWeight.Normal,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        color = colo
                    )
                }

                // Right column for "sunset"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp) // Add padding to the column
                ) {

                    Spacer(modifier = Modifier.height(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.sunset),
                        contentDescription = "sunset",
                        modifier = Modifier.size(48.dp),
                        colorFilter = ColorFilter.tint(colo)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${ui.TodayData?.sunset} am",
                        fontFamily = gotham,
                        fontWeight = FontWeight.Normal,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                        color = colo
                    )
                }
            }
        }

    }
}
