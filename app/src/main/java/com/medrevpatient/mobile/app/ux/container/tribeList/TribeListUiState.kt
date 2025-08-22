package com.medrevpatient.mobile.app.ux.container.tribeList

import android.content.Context
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.tribe.MemberResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TribeListDataState(
    val showLoader: Boolean = false,
    val tribeName: String = "",
    val message: String = "",
    val messageErrorMsg: String? = null,
    var showDialog: Boolean = false,
    var showRemoveDialog: Boolean = false,
    val userId: String = "",
    val apiStatus: Boolean = false,
)

data class TribeListUiState(
    //data
    val tribeListDataFlow: StateFlow<TribeListDataState?> = MutableStateFlow(null),
    val tribeMemberListFlow: StateFlow<PagingData<MemberResponse>> = MutableStateFlow(PagingData.empty()),
    //event
    val event: (TribeListUiEvent) -> Unit = {}
)

sealed interface TribeListUiEvent {
    data class GetContext(val context: Context) : TribeListUiEvent
    data object BackClick : TribeListUiEvent
    data object TribeListClick : TribeListUiEvent
    data object OnGetTribeList : TribeListUiEvent
    data object OnAddMemberClick : TribeListUiEvent
    data object BlockUser : TribeListUiEvent
    data class NavigateToChatScreen(val chatData: MemberResponse) : TribeListUiEvent

    //  data object NavigateToChatScreen : TribeListUiEvent
    data object RemoveUser : TribeListUiEvent

    data class BlockUserDialog(val show: Boolean, val userId: String) : TribeListUiEvent
    data class RemoveUserDialog(val show: Boolean, val userId: String) : TribeListUiEvent

}