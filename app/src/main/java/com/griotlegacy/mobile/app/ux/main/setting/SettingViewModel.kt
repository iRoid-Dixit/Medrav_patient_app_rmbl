package com.griotlegacy.mobile.app.ux.main.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getSettingUiStateUseCase: GetSettingUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: SettingUiState = getSettingUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}