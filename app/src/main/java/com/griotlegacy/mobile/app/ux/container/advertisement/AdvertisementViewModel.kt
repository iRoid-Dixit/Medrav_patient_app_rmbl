package com.griotlegacy.mobile.app.ux.container.advertisement
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdvertisementViewModel
@Inject constructor(
    getAdvertisementUiStateUseCase: GetAdvertisementUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    val uiState: AdvertisementUiState = getAdvertisementUiStateUseCase(

        coroutineScope = viewModelScope,
    ) { navigate(it) }
}