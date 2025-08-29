package com.medrevpatient.mobile.app.ux.main.appointment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetAppointmentUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,

) {
    private val searchUiDataFlow = MutableStateFlow(AppointmentsUiDataState())
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

            else -> {}
        }
    }



}