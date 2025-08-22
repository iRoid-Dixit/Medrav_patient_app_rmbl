package com.medrevpatient.mobile.app.ux.main.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(

    getHomeUiStateUseCase: GetHomeUiStateUseCase

) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: HomeUiState =
        getHomeUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}