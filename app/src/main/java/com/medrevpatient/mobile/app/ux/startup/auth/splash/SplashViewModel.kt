package com.medrevpatient.mobile.app.ux.startup.auth.splash
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    getSplashUiStateUseCase: GetSplashUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val splashUiState: SplashUiState = getSplashUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}