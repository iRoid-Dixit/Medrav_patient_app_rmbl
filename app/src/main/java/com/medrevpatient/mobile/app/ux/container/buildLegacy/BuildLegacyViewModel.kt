package com.medrevpatient.mobile.app.ux.container.buildLegacy

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
class BuildLegacyViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getBuildLegacyUiStateUseCase: GetBuildLegacyUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val legacyPostData: String =
        savedStateHandle.get<String>(BuildLegacyRoute.Arg.LEGACY_POST_DATA) ?: ""
    private val screenName: String =
        savedStateHandle.get<String>(BuildLegacyRoute.Arg.SCREEN_NAME) ?: ""
    val uiState: BuildLegacyUiState = getBuildLegacyUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope,
        legacyPostData = legacyPostData,
        screeName = screenName
    ) { navigate(it) }
}