package com.medrevpatient.mobile.app.ux.startup.auth.weightTracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeightTrackerViewModel
@Inject constructor(
    getWeightTrackerUiStateUseCase: GetWeightTrackerUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: WeightTrackerUiState =
        getWeightTrackerUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}
