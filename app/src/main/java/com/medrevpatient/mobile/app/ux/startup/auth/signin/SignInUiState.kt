package com.medrevpatient.mobile.app.ux.startup.auth.signin

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SignInUiState(
    val state: StateFlow<SignInUiStateValues> = MutableStateFlow(SignInUiStateValues()),
    val event: (SignInUiEvent) -> Unit = {},
    val showVerificationBottomSheet: StateFlow<Boolean> = MutableStateFlow(false),
    val showVerifiedBottomSheet: StateFlow<Boolean> = MutableStateFlow(false),
    val showResetEmailBottomSheet: StateFlow<Boolean> = MutableStateFlow(false),
    val showNewPasswordBottomSheet: StateFlow<Boolean> = MutableStateFlow(false),
    val otpUiState: StateFlow<OtpUiState?> = MutableStateFlow(null),
    val resetPasswordUiState: StateFlow<ResetPasswordUiState?> = MutableStateFlow(null),
    val counter: StateFlow<Int> = MutableStateFlow(30),
    val isFromResetPassword: StateFlow<Boolean> = MutableStateFlow(false)
)


data class SignInUiStateValues(
    val email: String = "",
    val emailErrorMsg: String? = null,
    val password: String = "",
    val passwordErrorMsg: String? = null,
    val isLoading: Boolean = false
)

data class OtpUiState(
    val otp: String = "",
    val errorMsg: String? = null,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isOTPResend: Boolean = false
)

data class ResetPasswordUiState(
    val email: String = "",
    val emailErrorMsg: String? = null,
    val password: String = "",
    val passwordErrorMsg: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordErrorMsg: String? = null
)

//Events
sealed interface SignInUiEvent {
    data class EmailValueChange(val email: String) : SignInUiEvent
    data class ResetEmailValueChange(val email: String) : SignInUiEvent
    data class PasswordValueChange(val password: String) : SignInUiEvent
    data class ResetPasswordValueChange(val password: String) : SignInUiEvent
    data class ResetConfirmPasswordValueChange(val password: String) : SignInUiEvent
    data object MoveToSignUpScreen : SignInUiEvent
    data object MoveToForgotPasswordScreen : SignInUiEvent
    data object PerformLogin : SignInUiEvent
    data class OpenOrCloseOTPDialog(val isOpen: Boolean) : SignInUiEvent
    data class OpenOrCloseVerifiedDialog(val isOpen: Boolean) : SignInUiEvent
    data class OpenOrCloseResetEmailDialog(val isOpen: Boolean) : SignInUiEvent
    data class OpenOrCloseNewPasswordDialog(val isOpen: Boolean) : SignInUiEvent
    data class OnOTPValueInsert(val value: String) : SignInUiEvent
    data object PerformVerifyOtp : SignInUiEvent
    data object PerformResendOTP : SignInUiEvent
    data object PerformGetStarted : SignInUiEvent
    data object PerformSetNewPassword : SignInUiEvent
}