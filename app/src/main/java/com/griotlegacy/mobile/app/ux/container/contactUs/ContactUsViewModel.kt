package com.griotlegacy.mobile.app.ux.container.contactUs

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getAboutUiStateUseCase: GetContactUsUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: ContactUsUiState = getAboutUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}