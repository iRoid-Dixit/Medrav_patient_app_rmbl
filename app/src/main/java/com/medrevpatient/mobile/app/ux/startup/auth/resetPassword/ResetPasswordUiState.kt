package com.medrevpatient.mobile.app.ux.startup.auth.resetPassword
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ForgetPasswordUiState(
    val resetPasswordUiDataState: StateFlow<ResetPasswordUiDataState?> = MutableStateFlow(null),
    val event: (ResetPasswordUiEvent) -> Unit = {}
)


data class ResetPasswordUiDataState(
    val newPassword: String = "",
    val newPasswordErrorMsg: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordErrorMsg: String? = null,
    val showLoader:Boolean=false,
    val email: String = "",
)
sealed interface ResetPasswordUiEvent {
    data class GetContext(val context: Context): ResetPasswordUiEvent
    data class NewPasswordValueChange(val newPassword:String): ResetPasswordUiEvent
    data class ConfirmPasswordValueChange(val confirmPassword:String): ResetPasswordUiEvent
    data object ResetPasswordSubmit: ResetPasswordUiEvent
    data object OnBackClick: ResetPasswordUiEvent

}