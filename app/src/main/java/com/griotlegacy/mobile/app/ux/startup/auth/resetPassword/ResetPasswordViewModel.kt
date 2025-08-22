package com.griotlegacy.mobile.app.ux.startup.auth.resetPassword

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import com.griotlegacy.mobile.app.ux.startup.auth.verifyOtp.VerifyOtpRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    getResetPasswordUiStateUseCase: GetResetPasswordUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val email: String = savedStateHandle.get<String>(VerifyOtpRoute.Arg.EMAIL) ?: ""
    val resetUiState: ForgetPasswordUiState = getResetPasswordUiStateUseCase(context = context, coroutineScope = viewModelScope, email = email) { navigate(it) }
}