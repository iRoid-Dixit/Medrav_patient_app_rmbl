package com.medrevpatient.mobile.app.ux.container.changePassword
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ChangePasswordUiState(
    //data
    val changePasswordUsDataFlow: StateFlow<ChangePasswordDataState?> = MutableStateFlow(null),
    //event
    val event: (ChangePasswordUiEvent) -> Unit = {}
)

data class ChangePasswordDataState(
    val showLoader: Boolean = false,
    val oldPassword: String = "",
    val oldPasswordErrorMsg: String? = null,
    val newPassword: String = "",
    val newPasswordErrorMsg: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordErrorMsg: String? = null,
)

sealed interface ChangePasswordUiEvent {
    data class OldPasswordValueChange(val oldPassword:String): ChangePasswordUiEvent
    data class GetContext(val context:Context): ChangePasswordUiEvent
    data class NewPasswordValueChange(val newPassword:String): ChangePasswordUiEvent
    data class ConfirmPasswordValueChange(val confirmPassword:String): ChangePasswordUiEvent
    data object BackClick : ChangePasswordUiEvent
    data object ChangePasswordSubmit : ChangePasswordUiEvent


}