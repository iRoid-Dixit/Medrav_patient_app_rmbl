package com.griotlegacy.mobile.app.ux.container.myCircle

import android.content.Context
import androidx.paging.PagingData
import com.griotlegacy.mobile.app.model.domain.response.tribe.TribeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MyCircleUiState(
    //data
    val myCircleDataFlow: StateFlow<MyCircleDataState?> = MutableStateFlow(null),
    val tribeListFlow: StateFlow<PagingData<TribeResponse>> = MutableStateFlow(PagingData.empty()),
    val innerCircleListFlow: StateFlow<PagingData<TribeResponse>> = MutableStateFlow(PagingData.empty()),
    //event
    val event: (MyCircleUiEvent) -> Unit = {}
)

data class MyCircleDataState(
    val showLoader: Boolean = false,
    val fullName: String = "",
    val fullNameErrorMsg: String? = null,
    val message: String = "",
    val messageErrorMsg: String? = null,
)

sealed interface MyCircleUiEvent {
    data class GetContext(val context: Context) : MyCircleUiEvent
    data class FullNameValueChange(val fullName: String) : MyCircleUiEvent
    data class MessageValueChange(val message: String) : MyCircleUiEvent
    data object BackClick : MyCircleUiEvent
    data object MyCircleClick : MyCircleUiEvent
    data object OnGetTribeList : MyCircleUiEvent
    data object OnAddTribeClick : MyCircleUiEvent
    data class OnMemberClick(val tribeId: String,val tribeName:String) : MyCircleUiEvent

}