package com.medrevpatient.mobile.app.ux.container.appointmentViewDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppointmentViewDetailsViewModel
@Inject constructor(
    getAppointmentViewDetailsUiStateUseCase: GetAppointmentViewDetailsUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: AppointmentViewDetailsUiState =
        getAppointmentViewDetailsUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}
