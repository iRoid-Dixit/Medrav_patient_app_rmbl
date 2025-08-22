package com.medrevpatient.mobile.app.ux.startup.auth.login

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
data class LoginUiState(
    val loginDataFlow:StateFlow<LoginData?> = MutableStateFlow(null),
    val event:(LoginUiEvent)->Unit={}
)
data class LoginData(
    val email: String = "",
    val emailErrorMsg: String? = null,
    val password: String = "",
    val passwordErrorMsg: String? = null,
    val showLoader:Boolean=false
)
sealed interface LoginUiEvent {
    data class EmailValueChange(val email:String):LoginUiEvent
    data class PasswordValueChanges(val password:String):LoginUiEvent
    data class GetContext(val context: Context):LoginUiEvent
    data object DoLogin: LoginUiEvent
    data object SignUp:LoginUiEvent
    data object ForgetPassword: LoginUiEvent
}