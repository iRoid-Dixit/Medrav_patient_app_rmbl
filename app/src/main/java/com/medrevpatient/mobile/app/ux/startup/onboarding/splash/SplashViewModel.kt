package com.medrevpatient.mobile.app.ux.startup.onboarding.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getSplashUiStateUseCase: GetSplashUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val splashUiState: SplashUiState = getSplashUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}