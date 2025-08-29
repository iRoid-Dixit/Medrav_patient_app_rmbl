package com.medrevpatient.mobile.app.ux.main.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getProfileUiStateUseCase: GetProfileUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: ProfileUiState = getProfileUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}