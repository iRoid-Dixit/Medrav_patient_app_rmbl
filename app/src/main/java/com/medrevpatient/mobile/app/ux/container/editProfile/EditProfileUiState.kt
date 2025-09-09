package com.medrevpatient.mobile.app.ux.container.editProfile

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import com.medrevpatient.mobile.app.ux.startup.auth.login.LoginUiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EditProfileUiState(
    //data
    val editProfileDataFlow: StateFlow<EditProfileDataState?> = MutableStateFlow(null),
    //event
    val event: (EditProfileUiEvent) -> Unit = {}
)

data class EditProfileDataState(
    val showLoader: Boolean = false,
    val firstName: String = "",
    val firstNameErrorMsg: String? = null,
    val lastName: String = "",
    val lastNameErrorMsg: String? = null,
    val email: String = "",
    val emailErrorMsg: String? = null,
    val originalEmail: String = "",
    val isEmailChanged: Boolean = false,
    val isEmailValid: Boolean = false,
    var dateSelected:String="",
    var profileImage:String="",
    val bmiCategory: Int=0,
    val selectGender:String="",
    val otpValue: String = "",
    val showDialog:Boolean=false,
    val showPermissionDialog:Boolean=false,
    // Medical Information fields
    val height: String = "",
    val heightErrorMsg: String? = null,
    val weight: String = "",
    val weightErrorMsg: String? = null,
    val bmi: String = "",
    val bmiStatus: String = "Overweight",
    val verifySheetVisible:Boolean=false,
    val allergies: String = "",
    val medicalConditions: String = "",
    val otpErrorMsg: String? = null,
    var remainingTimeFlow: String? = "",
    var isResendVisible: Boolean? = false,
    val isVerifyButtonLoading: Boolean = false,
    val isResendButtonLoading: Boolean = false,

)

sealed interface EditProfileUiEvent {
    data class GetContext(val context:Context): EditProfileUiEvent
    data object BackClick : EditProfileUiEvent
    data class FirstNameValueChange(val firstName:String): EditProfileUiEvent
    data class LastNameValueChange(val lastName:String): EditProfileUiEvent
    data class EmailValueChange(val email:String): EditProfileUiEvent
    data class BmiValueChange(val bmi:String): EditProfileUiEvent
    data class OnClickOfDate(val date: String): EditProfileUiEvent
    data object UpdateClick : EditProfileUiEvent
    data class ProfileValueChange(val profile:String): EditProfileUiEvent
    data class ShowDialog(val show:Boolean): EditProfileUiEvent
    data class ShowPermissionDialog(val show:Boolean): EditProfileUiEvent
    // Medical Information events
    data class HeightValueChange(val height: String): EditProfileUiEvent
    data class WeightValueChange(val weight: String): EditProfileUiEvent
    data class AllergiesValueChange(val allergies: String): EditProfileUiEvent
    data class MedicalConditionsValueChange(val medicalConditions: String): EditProfileUiEvent
    data class VerifySheetVisibility(val isVisible:Boolean):EditProfileUiEvent
    data class OtpValueChange(val otp: String): EditProfileUiEvent
    data object ResendCode: EditProfileUiEvent

    data class EditEmailClick@OptIn(ExperimentalMaterial3Api::class) constructor(val sheetState: SheetState, val scope: CoroutineScope) : EditProfileUiEvent

    data class VerifyClick@OptIn(ExperimentalMaterial3Api::class) constructor(val sheetState: SheetState, val scope: CoroutineScope) : EditProfileUiEvent
    data object VerifyEmailClick  : EditProfileUiEvent

}