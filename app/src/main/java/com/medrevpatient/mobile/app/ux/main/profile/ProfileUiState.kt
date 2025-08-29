package com.medrevpatient.mobile.app.ux.main.profile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProfileUiState(
    //data flow
    val messageUiDataFlow: StateFlow<ProfileUiDataState?> = MutableStateFlow(null),
    //event
    val event: (ProfileUiEvent) -> Unit = {}
)
data class ProfileUiDataState(

    val showSendInvitationDialog: Boolean = false,

    )
sealed interface ProfileUiEvent {
    object EditProfile : ProfileUiEvent
    object ChangePassword : ProfileUiEvent
    object DeleteAccount : ProfileUiEvent
    object Logout : ProfileUiEvent
}