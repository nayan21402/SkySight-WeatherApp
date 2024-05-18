package com.example.skysight.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skysight.R
import com.example.skysight.ui.theme.gotham
import com.example.skysight.viewModel.ui

@Composable
fun Compare(ui: ui, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(7))
            .background(Color(0xD9 / 255f, 0xD9 / 255f, 0xD9 / 255f, 0.25f))
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(
                    vertical = 4.dp
                )
//                .background(Color.LightGray)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    //.clickable(onClick = {})
                    .size(260.dp, 60.dp)
                    .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = colo,
                        shape = RoundedCornerShape(16.dp)
                    ) // Add black outline
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "COMPARE",
                    fontFamily = gotham,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontSize = 30.sp),
                    color = colo
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Compare temperature to average of past 15 years",
//                modifier = Modifier.background(color = Color.Green),
                modifier = Modifier.padding(bottom = 4.dp),
                fontFamily = gotham,
                fontWeight = FontWeight.Thin,
                style = TextStyle(fontSize = 10.sp),
                color = colo
            )
        }
    }
}

@Composable
fun Past_results(ui: ui, modifier: Modifier = Modifier,  content: @Composable () -> Unit ) {
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
                    Text(
                        text = "Then",
                        fontFamily = gotham,
                        fontWeight = FontWeight.Medium,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 30.sp),
                        color = colo
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${ui.LiveWeatherData?.oldTempMax?.let { con(it) }} \n${ui.LiveWeatherData?.oldTempMin?.let { con(it) }}",
                        //text = "${con(96.0)} \n ${con(83.0)}",
                        fontFamily = gotham,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 50.sp),
                        color = colo
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp) // Add padding to the column
                ) {
                    Text(
                        text = "Now",
                        fontFamily = gotham,
                        fontWeight = FontWeight.Medium,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 30.sp),
                        color = colo
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${ui.TodayData?.tempmax?.let { con(it) }}\n${ui.TodayData?.tempmin?.let { con(it) }}",
                        fontFamily = gotham,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 50.sp),
                        color = colo
                    )
                }
            }
        }

    }
}

//@Composable
//@Preview(showBackground = false
//)
//fun weatherContainerPreview(){
//    Compare() {}
//    //Past_results {}
//}