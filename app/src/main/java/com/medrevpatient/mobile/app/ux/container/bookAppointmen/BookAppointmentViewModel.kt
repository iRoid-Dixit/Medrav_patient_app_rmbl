package com.medrevpatient.mobile.app.ux.container.bookAppointmen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookAppointmentViewModel
@Inject constructor(
    getBookAppointmentUiStateUseCase: GetBookAppointmentUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: BookAppointmentUiState =
        getBookAppointmentUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}
