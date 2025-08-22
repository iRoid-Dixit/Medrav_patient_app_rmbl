package com.griotlegacy.mobile.app.ux.startup.auth.forgetPassword
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ForgetPasswordUiState(
    val forgetPasswordUiDataState: StateFlow<ForgetPasswordUiDataState?> = MutableStateFlow(null),
    val event: (ForgetPasswordUiEvent) -> Unit = {}
)


data class ForgetPasswordUiDataState(
    val email: String = "",
    val emailErrorMsg: String? = null,
    val showLoader:Boolean=false
)
sealed interface ForgetPasswordUiEvent {
    data class GetContext(val context: Context): ForgetPasswordUiEvent
    data class EmailValueChange(val email:String): ForgetPasswordUiEvent
    data object ForgetPasswordClick: ForgetPasswordUiEvent
    data object OnBackClick: ForgetPasswordUiEvent

}