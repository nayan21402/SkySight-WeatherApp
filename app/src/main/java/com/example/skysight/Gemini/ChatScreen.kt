/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.skysight.Gemini

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.skysight.MainActivity
import com.example.skysight.R
import com.example.skysight.ui.theme.gotham
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

class chatScreenActivity: ComponentActivity(){
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
            val intentExtra = intent.getStringExtra("weather")

            // Use the intentExtra to initialize chat history
            val chatHistory = provideChatHistory(intentExtra ?: "")
            val chatViewModel = ChatViewModel(
                GenerativeModel(
                "gemini-1.0-pro",
                // Retrieve API key as an environmental variable defined in a Build Configuration
                // see https://github.com/google/secrets-gradle-plugin for further instructions
                "AIzaSyC4lbtQhwQtNhGOJoFrDrQp66gWbARUXAk",
                generationConfig = generationConfig {
                    temperature = 0.9f
                    topK = 1
                    topP = 1f
                    maxOutputTokens = 2048
                },
                safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
                    SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
                )), chatHistory = chatHistory
            )
            intent.getStringExtra("weather")?.let { Log.d("chat", it) }
            val intent = Intent(this,MainActivity::class.java)
            ChatScreen(chatViewModel = chatViewModel){
                startActivity(intent)
            }
        }
    }
}

fun provideChatHistory(history: String): MutableList<Content> {
    return mutableListOf(
        content("user") { text("You are a chatbot which will give suggestions for weather. This is the weather data \n$history")
        }
        ,
        content("model") { text(
            "understood i'll give weather suggestions"
        )
        }

    )


}
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    onClick: ()->Unit
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val chatUiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    WeatherBackground(imageId = R.drawable.day_cloudy) {
        Scaffold(containerColor = Color.Transparent,
            topBar = { Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top=statusBarHeight,end = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
            Button(onClick = { onClick()
            },colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)) {
                Text(text = "Go Back")
            }
        } }) {
            Scaffold(containerColor = Color.Transparent,
                modifier = Modifier.padding(it),
                bottomBar = {
                    MessageInput(
                        onSendMessage = { inputText ->
                            chatViewModel.sendMessage(inputText)
                        },
                        resetScroll = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    // Messages List
                    ChatList(chatUiState.messages, listState)
                }
            }
        }
    }


    }



@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    listState: LazyListState
) {
    LazyColumn(
        reverseLayout = true,
        state = listState
    ) {
        items(chatMessages.reversed()) { message ->
            ChatBubbleItem(message)
        }
    }
}

@Composable
fun ChatBubbleItem(
    chatMessage: ChatMessage
) {
    val isModelMessage = chatMessage.participant == Participant.MODEL ||
            chatMessage.participant == Participant.ERROR

    val backgroundColor = when (chatMessage.participant) {
        Participant.MODEL -> Color.Black
        Participant.USER -> Color.White
        Participant.ERROR -> MaterialTheme.colorScheme.errorContainer
    }

    val bubbleShape = if (isModelMessage) {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    }

    val horizontalAlignment = if (isModelMessage) {
        Alignment.Start
    } else {
        Alignment.End
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Row {
            if (chatMessage.isPending) {
                CircularProgressIndicator(
                    color = Color.Black
                    ,modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(all = 8.dp)
                )
            }
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                ) {
                    Text(
                        text = chatMessage.text,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(16.dp),
                        fontFamily = gotham,
                        color = if(chatMessage.participant==Participant.MODEL) Color.White else if (chatMessage.participant==Participant.USER) Color.Black else Color.Black

                    )
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {}
) {
    var userMessage by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = userMessage,
            label = { Text("input") },
            onValueChange = { userMessage = it },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
            ),colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Black, unfocusedIndicatorColor = Color.Black, cursorColor = Color.Black, focusedLabelColor = Color.Black, unfocusedLabelColor = Color.Black),
            textStyle = TextStyle(fontFamily = gotham)
            ,modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
                .weight(0.85f)
        )
        IconButton(
            onClick = {
                if (userMessage.isNotBlank()) {
                    onSendMessage(userMessage)
                    userMessage = ""
                    resetScroll()
                }
            },
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
                .weight(0.15f)
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "send",
                modifier = Modifier
            )
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