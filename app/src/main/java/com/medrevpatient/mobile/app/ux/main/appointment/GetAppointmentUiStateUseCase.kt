package com.medrevpatient.mobile.app.ux.main.appointment
import android.content.Context
import android.content.Intent
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetAppointmentUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,

) {
    private val searchUiDataFlow = MutableStateFlow(AppointmentsUiDataState().copy(
        appointments = getSampleAppointments()
    ))
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): AppointmentsUiState {

        return AppointmentsUiState(
            appointmentsUiDataFlow = searchUiDataFlow,
            event = { archiveUiEvent ->
                appointmentsUiEvent(
                    context = context,
                    event = archiveUiEvent,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }
        )
    }

    private fun appointmentsUiEvent(
        context: Context,
        event: AppointmentsUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {
            is AppointmentsUiEvent.OnTabSelected -> {
                searchUiDataFlow.value = searchUiDataFlow.value.copy(selectedTab = event.tab)
            }
            is AppointmentsUiEvent.OnAppointmentClick -> {

            }
            is AppointmentsUiEvent.OnVideoCallClick -> {
                // Handle video call
            }
            is AppointmentsUiEvent.OnMessageClick -> {
                // Handle message
            }

            AppointmentsUiEvent.BookAppointmentClick -> {
                navigateToContainerScreens(
                    context,
                    navigate,
                    Constants.AppScreen.BOOK_APPOINTMENT_SCREEN
                )

            }

            AppointmentsUiEvent.OnBackClick -> {

            }

            AppointmentsUiEvent.ViewDetailsClick -> {
                navigateToContainerScreens(
                    context,
                    navigate,
                    Constants.AppScreen.APPOINTMENT_DETAILS_SCREEN
                )
            }
        }
    }

    private fun getSampleAppointments(): List<AppointmentItem> {
        return listOf(
            AppointmentItem(
                id = "1",
                day = "15",
                month = "July 2023",
                time = "Today, 10:00 AM",
                isToday = true,
                doctorName = "Dr. Mark Wilson",
                doctorSpecialization = "Weight Management Specialist",
                status = AppointmentStatus.UPCOMING
            ),
            AppointmentItem(
                id = "2",
                day = "28",
                month = "June 2023",
                time = "10:00 AM",
                doctorName = "Dr. Mark Wilson",
                doctorSpecialization = "Weight Management Specialist",
                status = AppointmentStatus.PAST
            ),
            AppointmentItem(
                id = "3",
                day = "28",
                month = "June 2023",
                time = "10:00 AM",
                doctorName = "Dr. Mark Wilson",
                doctorSpecialization = "Weight Management Specialist",
                status = AppointmentStatus.CANCELED
            ),
            AppointmentItem(
                id = "4",
                day = "15",
                month = "July 2023",
                time = "Today, 11:30 AM",
                isToday = true,
                doctorName = "Dr. Mark Wilson",
                doctorSpecialization = "Weight Management Specialist",
                status = AppointmentStatus.UPCOMING
            )
        )
    }

    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }



}