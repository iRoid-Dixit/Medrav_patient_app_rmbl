package com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DietChallengeViewModel
@Inject constructor(
    getDietChallengeUiStateUseCase: GetDietChallengeUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: DietChallengeUiState =
        getDietChallengeUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}
