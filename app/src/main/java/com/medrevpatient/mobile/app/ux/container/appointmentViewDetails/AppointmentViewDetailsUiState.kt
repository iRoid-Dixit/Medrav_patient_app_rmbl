package com.medrevpatient.mobile.app.ux.container.appointmentViewDetails

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AppointmentViewDetailsUiState(
    val appointmentViewDetailsDataFlow: StateFlow<AppointmentViewDetailsData?> = MutableStateFlow(null),
    val event: (AppointmentViewDetailsUiEvent) -> Unit = {}
)

data class AppointmentViewDetailsData(
    val showLoader: Boolean = false,
    val appointment: AppointmentDetails? = null
)

data class AppointmentDetails(
    val id: String = "APT2024001",
    val status: AppointmentStatus = AppointmentStatus.UPCOMING,
    val date: String = "January 15, 2024",
    val time: String = "2:30 PM",
    val type: String = "Video Consultation",
    val purpose: String = "Weight Management Follow-up",
    val doctor: DoctorInfo = DoctorInfo(),
    val preparationInstructions: List<String> = listOf(
        "Have your current weight measurement ready",
        "Bring any questions about your medication",
        "Review your food diary from the past week"
    ),
    val reminder: String = "Please join the call 5 minutes early to test your connection.",
    val postAppointmentNote: String = "Feedback and notes will be available after your appointment."
)

data class DoctorInfo(
    val name: String = "Dr. Sarah Wilson",
    val specialization: String = "Endocrinologist",
    val isVerified: Boolean = true,
    val experience: String = "Specializes in weight management and metabolic disorders with 12+ years of experience.",
    val profileImage: String? = null
)

enum class AppointmentStatus {
    UPCOMING,
    PAST,
    CANCELED
}

sealed interface AppointmentViewDetailsUiEvent {
    data class GetContext(val context : Context) : AppointmentViewDetailsUiEvent
    object JoinVideoCall : AppointmentViewDetailsUiEvent
    object RescheduleAppointment : AppointmentViewDetailsUiEvent
    object CancelAppointment : AppointmentViewDetailsUiEvent
    object ContactSupport : AppointmentViewDetailsUiEvent
    object OnBackClick : AppointmentViewDetailsUiEvent
}
