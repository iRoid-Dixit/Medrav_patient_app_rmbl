package com.medrevpatient.mobile.app.ux.main.griotLegacy

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class GriotLegacyViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getGriotLegacyUiStateUseCase: GetGriotLegacyUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: GriotLegacyUiState = getGriotLegacyUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}