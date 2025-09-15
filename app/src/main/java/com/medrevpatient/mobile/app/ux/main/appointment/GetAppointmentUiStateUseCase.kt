package com.medrevpatient.mobile.app.ux.main.appointment
import android.content.Context
import android.content.Intent
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.response.appointment.AppointmentResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetAppointmentUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,

) {
    private val searchUiDataFlow = MutableStateFlow(AppointmentsUiDataState())

    private val appointmentList =
        MutableStateFlow<PagingData<AppointmentResponse>>(PagingData.empty())
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): AppointmentsUiState {
        // Load initial appointments for "All" tab
        getAppointment(coroutineScope, AppointmentTab.ALL)
        return AppointmentsUiState(
            appointmentsUiDataFlow = searchUiDataFlow,
            appointmentList = appointmentList,
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
                getAppointment(coroutineScope, event.tab)
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



    private fun getAppointment(coroutineScope: CoroutineScope, tab: AppointmentTab) {
        coroutineScope.launch {
            val status = when (tab) {
                AppointmentTab.ALL -> null
                AppointmentTab.UPCOMING -> 1
                AppointmentTab.PAST -> 2
            }
            apiRepository.getAppointmentData(status = status).cachedIn(this).collect { pagingData ->
                appointmentList.value = pagingData
            }
        }
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