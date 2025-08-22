package com.griotlegacy.mobile.app.ux.startup.auth.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getRegisterUiStateUseCase: GetRegisterUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val registerUiState: RegisterUiState = getRegisterUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}