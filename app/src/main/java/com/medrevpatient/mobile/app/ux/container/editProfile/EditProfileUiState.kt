package com.medrevpatient.mobile.app.ux.container.editProfile

import android.content.Context
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
    val name: String = "",
    val nameErrorMsg: String? = null,
    val email: String = "",
    val emailErrorMsg: String? = null,
    val phoneNumber: String = "",
    val phoneNumberErrorMsg: String? = null,
    var dateSelected:String="",
    var profileImage:String="",
    val dateOfBirthValidationMsg: String? = null,
    val selectGender:String="",
    val selectGanderErrorMsg: String? = null,
    val showCountryCode:String="",
    val showDialog:Boolean=false,
    val showPermissionDialog:Boolean=false,
)

sealed interface EditProfileUiEvent {
    data class GetContext(val context:Context): EditProfileUiEvent
    data object BackClick : EditProfileUiEvent
    data class NameValueChange(val name:String): EditProfileUiEvent
    data class EmailValueChange(val email:String): EditProfileUiEvent
    data class PhoneNumberValueChange(val phoneNumber:String): EditProfileUiEvent
    data class RoleDropDownExpanded(val selectGender:String): EditProfileUiEvent
    data class OnClickOfDate(val date: String): EditProfileUiEvent
    data object ProfileSubmitClick : EditProfileUiEvent
    data class ProfileValueChange(val profile:String): EditProfileUiEvent
    data class ShowDialog(val show:Boolean): EditProfileUiEvent
    data class ShowPermissionDialog(val show:Boolean): EditProfileUiEvent






}