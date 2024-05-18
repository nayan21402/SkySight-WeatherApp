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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    val generativeModel: GenerativeModel,val chatHistory: MutableList<Content>
) : ViewModel() {


    private var chat = generativeModel.startChat(
        history = chatHistory
    )
    private var _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(
            ChatUiState(
            // Map the initial messages
            listOf(
                ChatMessage(
                text = "Get suggestions for things to do!",
                participant = Participant.MODEL,
                isPending = false
            )
            )


        )
        )

    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    fun resetChat() {
        chat=generativeModel.startChat(
            history = chatHistory
        )
        _uiState.value = _uiState.value.copy(
            messages = listOf(
                ChatMessage(
                text = chatHistory.lastOrNull()?.parts?.firstOrNull()?.asTextOrNull() ?: "",
                participant = Participant.MODEL,
                isPending = false
            )
            )
        )
    }
    fun sendMessageWithoutBubble(userMessage: String) {

        _uiState.value.addMessage(
            ChatMessage(
                text = "Survey Result Uploaded,Analysing",
                participant = Participant.USER,
                isPending = true
            )
        )

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(userMessage)

                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    val sanitizedResponse = modelResponse.replace("*", "")
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = sanitizedResponse,
                            participant = Participant.MODEL,
                            isPending = false
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage,
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }



    fun sendMessage(userMessage: String) {
        // Add a pending message
        _uiState.value.addMessage(
            ChatMessage(
                text = userMessage,
                participant = Participant.USER,
                isPending = true
            )
        )

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(userMessage)

                _uiState.value.replaceLastPendingMessage()
/*
                response.text?.let { modelResponse ->
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = modelResponse,
                            participant = Participant.MODEL,
                            isPending = false
                        )
                    )
                }


 */
                response.text?.let { modelResponse ->
                    val sanitizedResponse = modelResponse.replace("*", "")
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = sanitizedResponse,
                            participant = Participant.MODEL,
                            isPending = false
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage,
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }
}




