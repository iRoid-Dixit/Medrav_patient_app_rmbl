package com.medrevpatient.mobile.app.ux.container.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel
@Inject constructor(
    getNotificationUiStateUseCase: GetNotificationUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    val uiState: NotificationUiState = getNotificationUiStateUseCase(
        coroutineScope = viewModelScope,

        ) { navigate(it) }
}