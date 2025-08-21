package com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class OnBoardDataViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getOnBoardDataUiStateUseCase: GetOnBoardDataUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: OnBoardDataUiState =
        getOnBoardDataUiStateUseCase(coroutineScope = viewModelScope, context = context) { navigate(it) }
}
