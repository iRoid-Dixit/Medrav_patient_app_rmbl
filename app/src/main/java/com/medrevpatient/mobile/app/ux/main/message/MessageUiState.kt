package com.medrevpatient.mobile.app.ux.main.message

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MessageUiState(
    //data flow
    val messageUiDataFlow: StateFlow<MessageUiDataState?> = MutableStateFlow(null),
    //event
    val event: (MessageUiEvent) -> Unit = {}
)
data class MessageUiDataState(

    val showSendInvitationDialog: Boolean = false,

    )
sealed interface MessageUiEvent {

}