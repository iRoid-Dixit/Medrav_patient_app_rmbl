package com.medrevpatient.mobile.app.ux.container.appointmentViewDetails

import android.content.Context
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetAppointmentViewDetailsUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
) {
    private val appointmentDetailsDataFlow = MutableStateFlow(AppointmentViewDetailsData(
        appointment = AppointmentDetails()
    ))
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): AppointmentViewDetailsUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return AppointmentViewDetailsUiState(
            appointmentViewDetailsDataFlow = appointmentDetailsDataFlow,
            event = { bmiUiEvent ->
                bmiEvent(
                    event = bmiUiEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,

                )
            }
        )
    }

    private fun bmiEvent(
        event: AppointmentViewDetailsUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
    ) {
        when (event) {
            is AppointmentViewDetailsUiEvent.GetContext -> {
                this.context = event.context
            }
            is AppointmentViewDetailsUiEvent.JoinVideoCall -> {
                // Handle join video call
            }
            is AppointmentViewDetailsUiEvent.RescheduleAppointment -> {
                // Handle reschedule appointment
            }
            is AppointmentViewDetailsUiEvent.CancelAppointment -> {
                // Handle cancel appointment
            }
            is AppointmentViewDetailsUiEvent.ContactSupport -> {
                // Handle contact support
            }

            AppointmentViewDetailsUiEvent.OnBackClick -> {
                navigate(NavigationAction.PopIntent)
            }
        }
    }
}
