package com.griotlegacy.mobile.app.ux.main.message

import androidx.paging.PagingData
import com.griotlegacy.mobile.app.model.domain.response.chat.MessageTabResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MessageUiState(
    //data
    val messageUiDataState: StateFlow<MessageUiDataState?> = MutableStateFlow(null),
    val messageList: StateFlow<PagingData<MessageTabResponse>> = MutableStateFlow(
        PagingData.empty()
    ),

    //event
    val event: (MessageUiEvent) -> Unit = {}
)

data class MessageUiDataState(

    val isStoringPurchaseInfo: Boolean = false,
    val isAPIStatus: Boolean = false,
)

sealed interface MessageUiEvent {
    data class NavigateToChatScreen(val messageTabResponse: MessageTabResponse) : MessageUiEvent
    data object NavigateToAddGroupMember : MessageUiEvent
    data object MessageListAPICall : MessageUiEvent
    // data object NavigateToChatScreen : MessageUiEvent

}