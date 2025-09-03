package com.medrevpatient.mobile.app.ux.container.notification

import android.content.Context
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.notification.NotificationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NotificationUiState(
    //data
    val notificationDataFlow: StateFlow<NotificationDataState?> = MutableStateFlow(null),
    val event: (NotificationUiEvent) -> Unit = {}
)

data class NotificationDataState(
    val showLoader: Boolean = false,
    val notifications: List<NotificationItem> = emptyList()
)

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val iconRes: Int,
    val isRead: Boolean = false
)

sealed interface NotificationUiEvent {
    data class GetContext(val context: Context) : NotificationUiEvent
    data object BackClick : NotificationUiEvent



}