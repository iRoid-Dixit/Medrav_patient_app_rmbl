package com.medrevpatient.mobile.app.ux.container.block

import android.content.Context
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.block.BlockUserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BlockListUiState(
    //data
    val blockListDataFlow: StateFlow<BlockListDataState?> = MutableStateFlow(null),
    val blockListFlow: StateFlow<PagingData<BlockUserResponse>> = MutableStateFlow(PagingData.empty()),
    //event
    val event: (BlockListUiEvent) -> Unit = {}
)

data class BlockListDataState(
    val showLoader: Boolean = false,
    val tribeName: String = "",
    val message: String = "",
    val messageErrorMsg: String? = null,
    var showUnblockDialog: Boolean = false,
    val userId: String = "",
    val tribeId: String = "",
)

sealed interface BlockListUiEvent {
    data class GetContext(val context: Context) : BlockListUiEvent
    data object BackClick : BlockListUiEvent
    data object BlockListClick : BlockListUiEvent
    data class UnblockUserDialog(val show: Boolean, val userId: String) : BlockListUiEvent
    data object OnAddMemberClick : BlockListUiEvent
    data object UnblockUser : BlockListUiEvent
}