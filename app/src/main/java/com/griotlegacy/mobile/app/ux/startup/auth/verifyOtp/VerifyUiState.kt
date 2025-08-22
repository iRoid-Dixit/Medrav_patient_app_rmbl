package com.griotlegacy.mobile.app.ux.startup.auth.verifyOtp
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class VerifyOtpUiState(
    val verifyOtpUiDataState: StateFlow<VerifyOtpUiDataState?> = MutableStateFlow(null),
    val event: (VerifyOtpUiEvent) -> Unit = {}
)


data class VerifyOtpUiDataState(
    val otpValue: String = "",
    val otpErrorMsg: String? = null,
    var remainingTimeFlow: String? = "",
    var isResendVisible: Boolean? = false,
    val email: String = "",
    val screenName:String="",
    val showLoader:Boolean=false


)
sealed interface VerifyOtpUiEvent {
    data class GetContext(val context: Context): VerifyOtpUiEvent
    data class OtpTextValueChange(val otp:String): VerifyOtpUiEvent
    data object ResendOtp: VerifyOtpUiEvent
    data object OtpVerify: VerifyOtpUiEvent
    data object OnBackClick: VerifyOtpUiEvent
}