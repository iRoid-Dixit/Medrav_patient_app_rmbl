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
    val selectedDateErrorFlow: String? = null,
    val selectedTime: String = "",
    val selectedTimePeriod: String = "Morning",
    val additionalNotes: String = "",
    val showLoader: Boolean = false,
    val timeSelectErrorFlow: String? = null,
    val selectCategory:String="",
    val selectCategoryErrorMsg: String? = null,
    val isDatePickerVisible: Boolean = false,
    val isTimePeriodDropdownExpanded: Boolean = false,
    var availableTimeSlots: List<String> = listOf(

    ),
    val unavailableTimeSlots: List<String> = listOf("10:00 PM"),
    val timePeriods: List<String> = listOf("Morning", "Afternoon", "Evening"),
    val isLoadingSlots: Boolean = false,
    val slotsError: String? = null
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
    data class RoleDropDownExpanded(val selectGender:String):BookAppointmentUiEvent


}