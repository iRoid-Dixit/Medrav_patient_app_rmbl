package com.griotlegacy.mobile.app.ux.container.tribeList

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class TribeListViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getTribeListUiStateUseCase: GetTribeListUiStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val tribeId: String = savedStateHandle.get<String>(TribeListRoute.Arg.TRIBE_ID) ?: ""
    private val tribeName: String = savedStateHandle.get<String>(TribeListRoute.Arg.TRIBE_NAME) ?: ""
    val uiState: TribeListUiState = getTribeListUiStateUseCase(context = context, coroutineScope = viewModelScope,tribeId=tribeId,tribeName=tribeName) { navigate(it) }
}