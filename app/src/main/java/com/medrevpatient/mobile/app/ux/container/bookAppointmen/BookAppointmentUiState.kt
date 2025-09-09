package com.medrevpatient.mobile.app.ux.container.bookAppointmen

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BookAppointmentUiState(
    val bookAppointmentUiDataFlow: StateFlow<BookAppointmentData?> = MutableStateFlow(null),
    val event: (BookAppointmentUiEvent) -> Unit = {}

)
data class BookAppointmentData(
    val selectedDate: String = "",
    val selectedTime: String = "",
    val selectedTimePeriod: String = "Morning",
    val additionalNotes: String = "",
    val showLoader: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val isTimePeriodDropdownExpanded: Boolean = false,
    val availableTimeSlots: List<String> = listOf(
        "08:00 AM", "08:15 AM", "08:30 AM", "08:45 AM",
        "09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM",
        "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM",
        "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM"
    ),
    val unavailableTimeSlots: List<String> = listOf("10:00 PM"),
    val timePeriods: List<String> = listOf("Morning", "Afternoon", "Evening")
)

sealed interface BookAppointmentUiEvent {
    data class GetContext(val context: Context) : BookAppointmentUiEvent
    data class SelectDate(val date: String) : BookAppointmentUiEvent
    data class SelectTime(val time: String) : BookAppointmentUiEvent
    data class SelectTimePeriod(val period: String) : BookAppointmentUiEvent
    data class UpdateNotes(val notes: String) : BookAppointmentUiEvent
    data class BookAppointmentSheetVisibility(val isVisible: Boolean) : BookAppointmentUiEvent
    data class ToggleTimePeriodDropdown(val isExpanded: Boolean) : BookAppointmentUiEvent
    object ConfirmBooking : BookAppointmentUiEvent

}