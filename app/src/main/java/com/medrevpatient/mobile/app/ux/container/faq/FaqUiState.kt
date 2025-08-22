package com.medrevpatient.mobile.app.ux.container.faq

import android.content.Context
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.container.faqQuestion.FAQQuestionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ContactUsUiState(
    //data
    val contactUsDataFlow: StateFlow<ContactUsDataState?> = MutableStateFlow(null),
    val faqQuestionListFlow: StateFlow<PagingData<FAQQuestionResponse>> = MutableStateFlow(
        PagingData.empty()),
    //event
    val event: (ContactUsUiEvent) -> Unit = {}
)

data class ContactUsDataState(
    val showLoader: Boolean = false,
)

sealed interface ContactUsUiEvent {
    data class GetContext(val context:Context): ContactUsUiEvent
    data class GetScreenName(val screen: String) : ContactUsUiEvent
    data object BackClick : ContactUsUiEvent




}