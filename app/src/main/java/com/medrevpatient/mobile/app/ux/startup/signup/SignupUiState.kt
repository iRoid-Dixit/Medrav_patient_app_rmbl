package com.medrevpatient.mobile.app.ux.startup.signup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SignupUiState(
    val state: StateFlow<SignupUiStateValues> = MutableStateFlow(SignupUiStateValues()),
    val event: (SignupUiEvent) -> Unit = {},
    val showVerificationBottomSheet: StateFlow<Boolean> = MutableStateFlow(false),
    val showVerifiedBottomSheet: StateFlow<Boolean> = MutableStateFlow(false),
    val signUpUiStateFlow: StateFlow<SignupUiStateValues?> = MutableStateFlow(null),
    val otpUiState: StateFlow<OtpUiState?> = MutableStateFlow(null),
    val counter: StateFlow<Int> = MutableStateFlow(30),
    val genderValue: StateFlow<Int> = MutableStateFlow(1),
    val isEmailVerified: StateFlow<Boolean> = MutableStateFlow(true),
)


data class SignupUiStateValues(
    val email: String = "",
    val emailErrorMsg: String? = null,
    val firstName: String = "",
    val firstNameErrorMsg: String? = null,
    val lastName: String = "",
    val lastNameErrorMsg: String? = null,
    val password: String = "",
    val passwordErrorMsg: String? = null,
    val confirmPassword: String = "",
    val emptyCPErrorMsf: String? = null,
    val passwordNotMatch: String? = null,
    val isLoading: Boolean = false
)

data class OtpUiState(
    val otp: String = "",
    val errorMsg: String? = null,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isOTPResend: Boolean = false
)

//Events
sealed interface SignupUiEvent {
    data class EmailValueChange(val email: String) : SignupUiEvent
    data class FirstNameValueChange(val firstName: String) : SignupUiEvent
    data class LastNameValueChange(val lastname: String) : SignupUiEvent
    data class PasswordValueChange(val password: String) : SignupUiEvent
    data class ConfirmPasswordValueChange(val confirmPassword: String) : SignupUiEvent
    data class OpenOrCloseOTPDialog(val isOpen: Boolean) : SignupUiEvent
    data class OpenOrCloseVerifiedDialog(val isOpen: Boolean) : SignupUiEvent
    data class OnOTPValueInsert(val value: String) : SignupUiEvent
    data object MoveToLoginScreen : SignupUiEvent
    data object PerformVerifyOtp : SignupUiEvent
    data object PerformResendOTP : SignupUiEvent
    data object PerformSignUp : SignupUiEvent
    data object PerformGetStarted : SignupUiEvent
    data class GenderClick(val gender: String) : SignupUiEvent
    data class TermsConditionClick(val value: Int) : SignupUiEvent
}