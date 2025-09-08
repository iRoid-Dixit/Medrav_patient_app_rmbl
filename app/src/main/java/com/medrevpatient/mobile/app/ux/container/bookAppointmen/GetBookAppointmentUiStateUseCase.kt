package com.medrevpatient.mobile.app.ux.container.bookAppointmen

import android.content.Context
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetBookAppointmentUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val profileUiDataFlow = MutableStateFlow(BookAppointmentData())

    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): BookAppointmentUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return BookAppointmentUiState(
            bookAppointmentUiDataFlow = profileUiDataFlow,
            event = { appointmentEvent ->
                handleEvent(
                    event = appointmentEvent,
                    navigate = navigate
                )
            }
        )
    }

    private fun handleEvent(
        event: BookAppointmentUiEvent,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {
            is BookAppointmentUiEvent.GetContext -> {
                this.context = event.context
            }

            is BookAppointmentUiEvent.SelectDate -> {
                profileUiDataFlow.update { it.copy(selectedDate = event.date) }
            }
            is BookAppointmentUiEvent.SelectTime -> {
                profileUiDataFlow.update { it.copy(selectedTime = event.time) }
            }
            is BookAppointmentUiEvent.SelectTimePeriod -> {
                profileUiDataFlow.update { it.copy(selectedTimePeriod = event.period) }
            }
            is BookAppointmentUiEvent.UpdateNotes -> {
                profileUiDataFlow.update { it.copy(additionalNotes = event.notes) }
            }
            is BookAppointmentUiEvent.BookAppointmentSheetVisibility -> {
                profileUiDataFlow.update { state ->
                    state.copy(isDatePickerVisible = event.isVisible)
                }
            }
            is BookAppointmentUiEvent.ToggleTimePeriodDropdown -> {
                profileUiDataFlow.update { it.copy(isTimePeriodDropdownExpanded = event.isExpanded) }
            }

            is BookAppointmentUiEvent.ConfirmBooking -> {
                navigate(PopIntent)
            }
        }
    }
}
