package com.griotlegacy.mobile.app.ux.main.message

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MessageViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getMessageUiStateUseCase: GetMessageUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: MessageUiState = getMessageUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope
    ) { navigate(it) }
}