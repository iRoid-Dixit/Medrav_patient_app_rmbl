package com.medrevpatient.mobile.app.ux.main.profile

import com.medrevpatient.mobile.app.ux.container.changePassword.ChangePasswordUiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProfileUiState(
    //data flow
    val messageUiDataFlow: StateFlow<ProfileUiDataState?> = MutableStateFlow(null),
    //event
    val event: (ProfileUiEvent) -> Unit = {}
)
data class ProfileUiDataState(

    val logoutSheetVisible:Boolean=false,
    val deleteSheetVisible:Boolean=false,

    )
sealed interface ProfileUiEvent {
    object EditProfile : ProfileUiEvent
    object ChangePassword : ProfileUiEvent
    object CustomerService : ProfileUiEvent
    object DeleteAccount : ProfileUiEvent
    object Logout : ProfileUiEvent
    object LogoutAPICall : ProfileUiEvent
    object DeleteAPICall : ProfileUiEvent
    data class LogoutSheetVisibility(val isVisible:Boolean): ProfileUiEvent
    data class DeleteSheetVisibility(val isVisible:Boolean): ProfileUiEvent

}