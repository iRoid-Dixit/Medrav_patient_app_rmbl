package com.medrevpatient.mobile.app.ux.main.medication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MedicationViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getMessageUiStateUseCase: GetMedicationUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: MedicationUiState = getMessageUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope
    ) { navigate(it) }
}