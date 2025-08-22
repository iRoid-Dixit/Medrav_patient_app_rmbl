package com.griotlegacy.mobile.app.ux.startup.auth.splash
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    getSplashUiStateUseCase: GetSplashUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val splashUiState: SplashUiState = getSplashUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}