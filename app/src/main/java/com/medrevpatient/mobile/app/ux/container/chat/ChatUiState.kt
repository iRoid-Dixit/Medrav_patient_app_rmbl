package com.medrevpatient.mobile.app.ux.container.chat

import android.content.Context
import com.medrevpatient.mobile.app.model.domain.response.chat.ChatResponse
import com.medrevpatient.mobile.app.model.domain.response.chat.MessageTabResponse
import com.medrevpatient.mobile.app.model.domain.response.tribe.MemberResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ChatUiState(
    //data
    val chatDataFlow: StateFlow<ChatDataState?> = MutableStateFlow(null),

    val chatDataList: StateFlow<List<ChatResponse>> = MutableStateFlow(emptyList()),
    //event
    val event: (ChatUiEvent) -> Unit = {}
)

data class ChatDataState(
    val showLoader: Boolean = false,
    val messageResponse: MessageTabResponse? = null,
    var sendText: String = "",
    val isLoading: Boolean = false,
    val screen: String = "",
    val chatData: MemberResponse? = null,
    val userId: String = "",
    val isApiStatus: Boolean = false,
)

sealed interface ChatUiEvent {
    data class GetContext(val context: Context) : ChatUiEvent
    data class MessageResponse(val messageResponse: String) : ChatUiEvent
    data object BackClick : ChatUiEvent
    data class SendMessage(val sendMessage: String) : ChatUiEvent
    data object OnMessageSendButtonClick : ChatUiEvent
    data class InitSocketListener(val context: Context) : ChatUiEvent
    data object OnLoadNextPage : ChatUiEvent
    data object GroupDetailsUpdate : ChatUiEvent
    data object AddNewMember : ChatUiEvent


}