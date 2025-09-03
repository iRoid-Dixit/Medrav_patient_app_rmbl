package com.medrevpatient.mobile.app.ux.container.notification
import android.content.Context
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.response.notification.NotificationResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetNotificationUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val blockListDataFlow = MutableStateFlow(NotificationDataState())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): NotificationUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        // Initialize with sample data
        blockListDataFlow.value = NotificationDataState(
            notifications = getSampleNotifications()
        )
        
        return NotificationUiState(
            notificationDataFlow = blockListDataFlow,
            event = { aboutUsEvent ->
                blockListUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                )
            }
        )
    }

    private fun blockListUiEvent(
        event: NotificationUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            NotificationUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }

            is NotificationUiEvent.GetContext -> {
                this.context = event.context
            }
        }
    }

    private fun getSampleNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = "1",
                title = "Schedule Change",
                description = "Your appointment has been rescheduled by your doctor. New appointment time: 04:00 PM.",
                timestamp = "09:13 AM",
                iconRes = com.medrevpatient.mobile.app.R.drawable.ic_place_holder
            ),
            NotificationItem(
                id = "2",
                title = "Reminder",
                description = "It's time to take your medication. Please don't skip your dose.",
                timestamp = "09:13 AM",
                iconRes = com.medrevpatient.mobile.app.R.drawable.ic_place_holder
            ),
            NotificationItem(
                id = "3",
                title = "Reminder",
                description = "It's time to take your medication. Please don't skip your dose.",
                timestamp = "09:13 AM",
                iconRes = com.medrevpatient.mobile.app.R.drawable.ic_place_holder
            ),
            NotificationItem(
                id = "4",
                title = "Side Effect Check-In",
                description = "Your Side Effect Check-In is still remainging. Please give your feedback regarding your medicine.",
                timestamp = "09:13 AM",
                iconRes = com.medrevpatient.mobile.app.R.drawable.ic_place_holder
            ),
            NotificationItem(
                id = "5",
                title = "Daily Diet Challenge",
                description = "your Daily Diet Challenge is remaining. Just 5 minutes to finish it.",
                timestamp = "09:13 AM",
                iconRes = com.medrevpatient.mobile.app.R.drawable.ic_daily_diet_challenge
            ),
            NotificationItem(
                id = "6",
                title = "Side Effect Check-In",
                description = "Your Side Effect Check-In is still remainging. Please give your feedback regarding your medicine.",
                timestamp = "09:13 AM",
                iconRes = com.medrevpatient.mobile.app.R.drawable.ic_place_holder
            ),
            NotificationItem(
                id = "7",
                title = "Appointment",
                description = "Your video call with your doctor is in 5 mins. Get ready.",
                timestamp = "09:13 AM",
                iconRes = com.medrevpatient.mobile.app.R.drawable.ic_place_holder
            )
        )
    }

}


