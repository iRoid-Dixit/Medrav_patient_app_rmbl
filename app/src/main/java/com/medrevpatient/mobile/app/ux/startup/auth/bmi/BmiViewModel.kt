package com.medrevpatient.mobile.app.ux.startup.auth.bmi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BmiViewModel
@Inject constructor(
    getBmiUiStateUseCase: GetBmiUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: BmiUiState =
        getBmiUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}
