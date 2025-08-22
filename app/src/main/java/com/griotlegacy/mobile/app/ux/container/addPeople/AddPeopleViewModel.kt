package com.griotlegacy.mobile.app.ux.container.addPeople

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
class AddPeopleViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getAddPeopleUiStateUseCase: GetAddPeopleUiStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val tribeId: String = savedStateHandle.get<String>(AddPeopleRoute.Arg.TRIBE_ID) ?: ""
    private val groupId: String = savedStateHandle.get<String>(AddPeopleRoute.Arg.GROUP_ID) ?: ""
    val uiState: AddPeopleUiState = getAddPeopleUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope,
        tribeId = tribeId,
        groupId = groupId
    ) { navigate(it) }
}