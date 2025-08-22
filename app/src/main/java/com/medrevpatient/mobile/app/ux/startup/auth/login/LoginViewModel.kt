package com.medrevpatient.mobile.app.ux.startup.auth.login
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    getAuthUiStateUseCase: GetLoginUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: LoginUiState =
        getAuthUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}

