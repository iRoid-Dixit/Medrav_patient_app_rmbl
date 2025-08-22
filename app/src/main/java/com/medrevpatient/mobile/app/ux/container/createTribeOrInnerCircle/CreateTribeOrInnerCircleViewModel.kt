package com.medrevpatient.mobile.app.ux.container.createTribeOrInnerCircle

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
class CreateTribeOrInnerCircleViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getCreateTribeOrInnerCircleUiStateUseCase: GetCreateTribeOrInnerCircleUiStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val screen: String =
        savedStateHandle.get<String>(CreateTribeOrInnerCircleRoute.Arg.SCREEN) ?: ""

    private val messageData: String =
        savedStateHandle.get<String>(CreateTribeOrInnerCircleRoute.Arg.MESSAGE_DATA) ?: ""

    val uiState: CreateTribeOrInnerCircleUiState = getCreateTribeOrInnerCircleUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope,
        screen = screen,
        messageData = messageData
    ) { navigate(it) }
}