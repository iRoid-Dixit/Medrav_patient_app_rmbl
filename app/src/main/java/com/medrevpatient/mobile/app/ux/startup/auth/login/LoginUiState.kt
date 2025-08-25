package com.medrevpatient.mobile.app.ux.startup.auth.login

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import kotlinx.coroutines.CoroutineScope
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
    val showLoader:Boolean=false,
    val resetSheetVisible:Boolean=false,
    val resendEmail: String = "",
    val resendEmailErrorMsg: String? = null,
    val emailVerificationSheetVisible:Boolean=false,
    val countdownTime: String = "01:00",
    val otpValue: String = "",
    val otpErrorMsg: String? = null,
    var remainingTimeFlow: String? = "",
    var isResendVisible: Boolean? = false,
    val setPasswordVisible:Boolean=false,
    val newPassword: String="",
    val newPasswordErrorMsg: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordErrorMsg: String? = null,
    val successSheetVisible:Boolean=false,
)
sealed interface LoginUiEvent {
    data class EmailValueChange(val email:String):LoginUiEvent
    data class PasswordValueChanges(val password:String):LoginUiEvent
    data class GetContext(val context: Context):LoginUiEvent
    data object DoLogin: LoginUiEvent
    data object SignUp:LoginUiEvent
    data class ResentSheetVisibility(val isVisible:Boolean):LoginUiEvent
    data class SetPasswordSheetVisibility(val isVisible:Boolean):LoginUiEvent
    data class ResendValueChange(val resendEmail:String):LoginUiEvent
    data class ProceedClick @OptIn(ExperimentalMaterial3Api::class) constructor(val sheetState: SheetState, val scope: CoroutineScope) : LoginUiEvent
    data object BackToLoginClick: LoginUiEvent
    data class EmailVerificationSheetVisibility(val isVisible:Boolean):LoginUiEvent
    data class OtpValueChange(val otp: String): LoginUiEvent
    data object ResendCode: LoginUiEvent
    data class EditEmailClick@OptIn(ExperimentalMaterial3Api::class) constructor(val sheetState: SheetState, val scope: CoroutineScope) : LoginUiEvent
    data class VerifyClick@OptIn(ExperimentalMaterial3Api::class) constructor(val sheetState: SheetState, val scope: CoroutineScope) : LoginUiEvent
    data class NewPasswordValueChange(val newPassword: String): LoginUiEvent
    data class ConfirmPasswordValueChange(val confirmPassword: String): LoginUiEvent
    data class ConfirmClick@OptIn(ExperimentalMaterial3Api::class) constructor(val sheetState: SheetState, val scope: CoroutineScope) : LoginUiEvent
    data class SuccessSheetVisibility(val isVisible:Boolean):LoginUiEvent
    data class ProceedClickSuccess@OptIn(ExperimentalMaterial3Api::class) constructor(val sheetState: SheetState, val scope: CoroutineScope) : LoginUiEvent
}