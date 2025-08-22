package com.griotlegacy.mobile.app.ux.container.block

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
class BlockListViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getBlockListUiStateUseCase: GetBlockListUiStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val tribeId: String = savedStateHandle.get<String>(BlockListRoute.Arg.TRIBE_ID) ?: ""
    private val tribeName: String =
        savedStateHandle.get<String>(BlockListRoute.Arg.TRIBE_NAME) ?: ""
    val uiState: BlockListUiState = getBlockListUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope,
        tribeId = tribeId,
        tribeName = tribeName
    ) { navigate(it) }
}