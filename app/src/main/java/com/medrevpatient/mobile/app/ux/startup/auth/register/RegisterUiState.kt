package com.medrevpatient.mobile.app.ux.startup.auth.register

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RegisterUiState(
    val registerUiDataState: StateFlow<RegisterUiDataState?> = MutableStateFlow(null),
    val event: (RegisterUiEvent) -> Unit = {}
)

data class RegisterUiDataState(
    val name: String = "",
    val nameErrorMsg: String? = null,
    val email: String = "",
    val emailErrorMsg: String? = null,
    val phoneNumber: String = "",
    val phoneNumberErrorMsg: String? = null,
    val password: String = "",
    val passwordErrorMsg: String? = null,
    val confirmPassword : String = "",
    val confirmPasswordErrorMsg: String? = null,
    var dateSelected:String="",
    val dateOfBirthValidationMsg: String? = null,
    val defaultCountryCode:String="",
    val selectGender:String="",
    val selectGanderErrorMsg: String? = null,
    val isTermAndConditionChecked: Boolean = false,
    val isTermAndConditionCheckedErrorMsg: String? = null,
    val showLoader:Boolean=false
)
sealed interface RegisterUiEvent {
    data class NameValueChange(val name:String):RegisterUiEvent
    data class EmailValueChange(val email:String):RegisterUiEvent
    data class PhoneNumberValueChange(val phoneNumber:String):RegisterUiEvent
    data class PasswordValueChange(val password:String):RegisterUiEvent
    data class ConfirmPasswordValueChange(val confirmPassword:String):RegisterUiEvent
    data class RoleDropDownExpanded(val selectGender:String):RegisterUiEvent
    data class OnCheckedChange(val isChecked: Boolean): RegisterUiEvent
    data class GetContext(val context: Context):RegisterUiEvent
    data object DoSignIn: RegisterUiEvent
    data object DoSignUp: RegisterUiEvent
    data object PrivacyPolicy : RegisterUiEvent
    data object TermAndCondition : RegisterUiEvent
    data class OnClickOfDate(val date: String): RegisterUiEvent

}