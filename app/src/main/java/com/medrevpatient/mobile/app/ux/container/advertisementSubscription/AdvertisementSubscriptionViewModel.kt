package com.medrevpatient.mobile.app.ux.container.advertisementSubscription

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdvertisementSubscriptionViewModel
@Inject constructor(
    getStorageUiStateUseCase: GetAdvertisementSubscriptionUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val advertisementId: String = savedStateHandle.get<String>(AdvertisementSubscriptionRoute.Arg.ADVERTISEMENT_ID) ?: ""
    val uiState: AdvertisementUiState = getStorageUiStateUseCase(coroutineScope = viewModelScope, advertisementId = advertisementId) { navigate(it) }
}