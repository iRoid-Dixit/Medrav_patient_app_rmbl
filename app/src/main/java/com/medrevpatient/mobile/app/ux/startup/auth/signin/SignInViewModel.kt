package com.medrevpatient.mobile.app.ux.startup.auth.signin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getSignInUiStateUseCase: GetSignInUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: SignInUiState = getSignInUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }

}