package com.medrevpatient.mobile.app.ux.container.about

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel
@Inject constructor(
    getAboutUiStateUseCase: GetAboutUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val url: String = savedStateHandle.get<String>(AboutRoute.Arg.URL) ?: ""
    val uiState: AboutUiState = getAboutUiStateUseCase(url = url) { navigate(it) }
}