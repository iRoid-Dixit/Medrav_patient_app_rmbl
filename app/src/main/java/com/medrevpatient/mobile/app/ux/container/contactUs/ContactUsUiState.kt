package com.medrevpatient.mobile.app.ux.container.contactUs

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ContactUsUiState(
    //data
    val contactUsDataFlow: StateFlow<ContactUsDataState?> = MutableStateFlow(null),
    //event
    val event: (ContactUsUiEvent) -> Unit = {}
)

data class ContactUsDataState(
    val showLoader: Boolean = false,
    val fullName: String = "",
    val fullNameErrorMsg: String? = null,
    val message: String = "",
    val messageErrorMsg:String? = null,
)

sealed interface ContactUsUiEvent {
    data class GetContext(val context:Context): ContactUsUiEvent
    data class FullNameValueChange(val fullName:String): ContactUsUiEvent
    data class MessageValueChange(val message:String): ContactUsUiEvent
    data object BackClick : ContactUsUiEvent
    data object ContactUsClick : ContactUsUiEvent




}