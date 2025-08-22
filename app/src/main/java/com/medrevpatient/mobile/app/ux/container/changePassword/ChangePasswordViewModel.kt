package com.medrevpatient.mobile.app.ux.container.changePassword

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getAboutUiStateUseCase: GetChangePasswordUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: ChangePasswordUiState = getAboutUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}