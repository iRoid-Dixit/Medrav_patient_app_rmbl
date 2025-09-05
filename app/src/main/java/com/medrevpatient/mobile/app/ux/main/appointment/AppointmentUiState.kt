package com.medrevpatient.mobile.app.ux.main.appointment

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class AppointmentsUiState(
    //data
    val appointmentsUiDataFlow: StateFlow<AppointmentsUiDataState?> = MutableStateFlow(null),

    val event: (AppointmentsUiEvent) -> Unit = {}
)


data class AppointmentsUiDataState(
    val isLoading: Boolean = false,
    val selectedTab: AppointmentTab = AppointmentTab.ALL,
    val appointments: List<AppointmentItem> = emptyList(),
)

data class AppointmentItem(
    val id: String,
    val day: String,
    val month: String,
    val time: String,
    val isToday: Boolean = false,
    val doctorName: String,
    val doctorSpecialization: String,
    val doctorProfileImage: String? = null,
    val status: AppointmentStatus,
    val canVideoCall: Boolean = true,
    val canMessage: Boolean = true,
)

enum class AppointmentStatus {
    UPCOMING,
    PAST,
    CANCELED
}

enum class AppointmentTab {
    ALL,
    UPCOMING,
    PAST
}





sealed interface AppointmentsUiEvent {
    data class OnTabSelected(val tab: AppointmentTab) : AppointmentsUiEvent
    data class OnAppointmentClick(val appointmentId: String) : AppointmentsUiEvent
    data class OnVideoCallClick(val appointmentId: String) : AppointmentsUiEvent
    data class OnMessageClick(val appointmentId: String) : AppointmentsUiEvent

    object ViewDetailsClick : AppointmentsUiEvent
    object BookAppointmentClick : AppointmentsUiEvent
    object OnBackClick : AppointmentsUiEvent
}