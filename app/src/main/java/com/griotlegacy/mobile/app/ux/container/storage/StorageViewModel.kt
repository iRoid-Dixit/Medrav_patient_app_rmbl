package com.griotlegacy.mobile.app.ux.container.storage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StorageViewModel
@Inject constructor(
    getStorageUiStateUseCase: GetStorageUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: StorageUiState = getStorageUiStateUseCase(
        coroutineScope = viewModelScope
    ) { navigate(it) }
}