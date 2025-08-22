package com.griotlegacy.mobile.app.ux.container.groupMember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddPeopleViewModel
@Inject constructor(

    getAddPeopleUiStateUseCase: GetGroupMemberUiStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val groupId: String = savedStateHandle.get<String>(GroupMemberRoute.Arg.GROUP_ID) ?: ""
    val uiState: GroupMemberUiState = getAddPeopleUiStateUseCase(

        coroutineScope = viewModelScope,
        groupId = groupId
    ) { navigate(it) }
}