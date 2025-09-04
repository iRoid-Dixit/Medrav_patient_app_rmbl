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
    val firstName: String = "",
    val firstNameErrorMsg: String? = null,
    val lastName: String = "",
    val lastNameErrorMsg: String? = null,
    val email: String = "",
    var dateSelected:String="",
    var profileImage:String="",
    val selectGender:String="",
    val showDialog:Boolean=false,
    val showPermissionDialog:Boolean=false,
    // Medical Information fields
    val height: String = "",
    val weight: String = "",
    val bmi: String = "",
    val bmiStatus: String = "Overweight",
    val allergies: String = "",
    val medicalConditions: String = "",
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

}