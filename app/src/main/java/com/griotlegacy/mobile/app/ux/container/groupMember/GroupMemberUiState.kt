package com.griotlegacy.mobile.app.ux.container.groupMember

import android.content.Context
import androidx.paging.PagingData
import com.griotlegacy.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class GroupMemberUiState(
    //data
    val groupMemberDataFlow: StateFlow<GroupMemberDataState?> = MutableStateFlow(null),
    val groupMemberListFlow: StateFlow<PagingData<SearchPeopleResponse>> = MutableStateFlow(
        PagingData.empty()
    ),
    //event
    val event: (GroupMemberUiEvent) -> Unit = {}
)

data class GroupMemberDataState(
    val showLoader: Boolean = false,
    val searchMember: String = "",
    val tribeName: String = "",
    val selectedMembers: List<String> = emptyList(),
    val message: String = "",
    val messageErrorMsg: String? = null,
    val userId: String = "",
    val memberId: String = "",
    var removeMemberDialog: Boolean = false
)

sealed interface GroupMemberUiEvent {
    data class GetContext(val context: Context) : GroupMemberUiEvent
    data object BackClick : GroupMemberUiEvent
    data class SelectedMember(val memberId: String) : GroupMemberUiEvent
    data class SearchMember(val search: String) : GroupMemberUiEvent
    data object OnGetAddPeople : GroupMemberUiEvent
    data object AddMemberButtonClick : GroupMemberUiEvent
    data object GroupMemberAPICall : GroupMemberUiEvent
    data object RemoveMember : GroupMemberUiEvent

    data class RemoveUserDialog(val show: Boolean, val memberId: String) : GroupMemberUiEvent

}