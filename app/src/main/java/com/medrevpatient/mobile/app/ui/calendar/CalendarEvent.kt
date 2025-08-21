package com.medrevpatient.mobile.app.ui.calendar

import androidx.compose.ui.graphics.vector.ImageVector
import com.medrevpatient.mobile.app.domain.response.Event
import java.time.LocalDate

data class CalendarEvent(
    val date: LocalDate,
    val completedWorkoutIcon: ImageVector,
    val pendingWorkoutIcon: ImageVector,
    val isCompleted: Boolean = false,
    val eventList: ArrayList<Event> = arrayListOf(),
    val isFutureDate: Boolean = false
)