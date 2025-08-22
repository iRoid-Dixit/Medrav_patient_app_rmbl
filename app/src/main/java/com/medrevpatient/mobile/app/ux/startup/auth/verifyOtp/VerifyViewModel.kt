package com.medrevpatient.mobile.app.ux.startup.auth.verifyOtp

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getVerifyUiStateUseCase: GetVerifyUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val email: String = savedStateHandle.get<String>(VerifyOtpRoute.Arg.EMAIL) ?: ""
    private val screenName: String = savedStateHandle.get<String>(VerifyOtpRoute.Arg.SCREEN_NAME) ?: ""
    val splashUiState: VerifyOtpUiState = getVerifyUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope,
        email = email,
        screenName = screenName
    ) { navigate(it) }
}