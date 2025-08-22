package com.medrevpatient.mobile.app.ux.container.userProfile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendProfileViewModel
@Inject constructor(
    getFriendProfileUiStateUseCase: GetFriendProfileUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    val uiState: FriendProfileUiState = getFriendProfileUiStateUseCase(
        coroutineScope = viewModelScope,

        ) { navigate(it) }
}