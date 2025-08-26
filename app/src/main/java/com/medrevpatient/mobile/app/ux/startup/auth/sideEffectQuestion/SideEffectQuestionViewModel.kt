package com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SideEffectQuestionViewModel
@Inject constructor(
    getSideEffectQuestionUiStateUseCase: GetSideEffectQuestionUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: SideEffectQuestionUiState =
        getSideEffectQuestionUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}
