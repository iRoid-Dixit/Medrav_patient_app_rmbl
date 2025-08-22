package com.griotlegacy.mobile.app.ux.container.faq
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FaqViewModel
@Inject constructor(
    getAboutUiStateUseCase: FaqUsUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: ContactUsUiState =
        getAboutUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}