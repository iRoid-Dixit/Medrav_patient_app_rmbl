package com.griotlegacy.mobile.app.ux.container.addAdvertisement

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AddAdvertisementViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getAddAdvertisementUiStateUseCase: GetAddAdvertisementUiStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val screen: String =
        savedStateHandle.get<String>(AddAdvertisementRoute.Arg.SCREEN) ?: ""
    private val advertisementData: String =
        savedStateHandle.get<String>(AddAdvertisementRoute.Arg.ADVERTISEMENT_DATA) ?: ""
    val uiState: AddAdvertisementUiState = getAddAdvertisementUiStateUseCase(context = context, coroutineScope = viewModelScope, screen = screen, advertisementData = advertisementData) { navigate(it) }
}