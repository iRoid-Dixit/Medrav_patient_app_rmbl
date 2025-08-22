package com.medrevpatient.mobile.app.ux.container.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
@Inject constructor(
    getBlockListUiStateUseCase: GetChatUiStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val chatData: String =
        savedStateHandle.get<String>(ChatRoute.Arg.CHAT_DATA) ?: ""
    private val screen: String =
        savedStateHandle.get<String>(ChatRoute.Arg.SCREEN_NAME) ?: ""
    val uiState: ChatUiState = getBlockListUiStateUseCase(
        coroutineScope = viewModelScope,
        chatData = chatData,
        screen = screen
    ) { navigate(it) }
}