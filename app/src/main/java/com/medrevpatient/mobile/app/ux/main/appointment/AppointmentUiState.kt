package com.medrevpatient.mobile.app.ux.main.appointment

import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class AppointmentsUiState(
    //data
    val appointmentsUiDataFlow: StateFlow<AppointmentsUiDataState?> = MutableStateFlow(null),

    val event: (AppointmentsUiEvent) -> Unit = {}
)


data class AppointmentsUiDataState(
    val isLoading: Boolean = false,
)





sealed interface AppointmentsUiEvent {

}