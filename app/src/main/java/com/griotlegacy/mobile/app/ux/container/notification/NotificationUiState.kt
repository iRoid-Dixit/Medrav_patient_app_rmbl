package com.griotlegacy.mobile.app.ux.container.notification

import android.content.Context
import androidx.paging.PagingData
import com.griotlegacy.mobile.app.model.domain.response.notification.NotificationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NotificationUiState(
    //data
    val notificationDataFlow: StateFlow<NotificationDataState?> = MutableStateFlow(null),

    val notificationList: StateFlow<PagingData<NotificationResponse>> = MutableStateFlow(
        PagingData.empty()
    ),
    //event
    val event: (NotificationUiEvent) -> Unit = {}
)

data class NotificationDataState(
    val showLoader: Boolean = false,

    )

sealed interface NotificationUiEvent {
    data class GetContext(val context: Context) : NotificationUiEvent
    data object BackClick : NotificationUiEvent
    data class RejectNotificationClick(val tribeId: String) : NotificationUiEvent
    data class AcceptNotificationClick(val tribeId: String) : NotificationUiEvent


}