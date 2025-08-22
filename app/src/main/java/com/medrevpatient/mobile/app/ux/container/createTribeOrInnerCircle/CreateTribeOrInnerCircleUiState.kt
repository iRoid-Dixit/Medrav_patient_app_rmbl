package com.medrevpatient.mobile.app.ux.container.createTribeOrInnerCircle

import android.content.Context
import com.medrevpatient.mobile.app.model.domain.response.chat.MessageTabResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CreateTribeOrInnerCircleUiState(
    //data
    val createTribeOrInnerCircleDataFlow: StateFlow<CreateTribeOrInnerCircleDataState?> = MutableStateFlow(null),
    //event
    val event: (CreateTribeOrInnerCircleUiEvent) -> Unit = {}
)

data class CreateTribeOrInnerCircleDataState(
    val showLoader: Boolean = false,
    val circleName: String = "",
    val circleErrorMsg: String? = null,

    val groupSelect: String = "",
    val groupSelectErrorMsg: String? = null,

    var memberSelected: String = "",
    val memberValidationMsg: String? = null,

    val memberList: String = "",
    var profileImage:String="",
    val showDialog:Boolean=false,
    val showPermissionDialog:Boolean=false,
    val screen: String = "",

    val messageResponse: MessageTabResponse? = null,
)

sealed interface CreateTribeOrInnerCircleUiEvent {
    data class GetContext(val context:Context): CreateTribeOrInnerCircleUiEvent
    data object BackClick : CreateTribeOrInnerCircleUiEvent
    data object OnAddMemberClick : CreateTribeOrInnerCircleUiEvent
    data class CircleNameValueChange(val circleName: String) : CreateTribeOrInnerCircleUiEvent
    data class GroupTypeDropDownExpanded(val groupType:String): CreateTribeOrInnerCircleUiEvent
    data object SubmitClick : CreateTribeOrInnerCircleUiEvent
    data class ProfileValueChange(val profile:String): CreateTribeOrInnerCircleUiEvent
    data class ShowDialog(val show:Boolean): CreateTribeOrInnerCircleUiEvent
    data class ShowPermissionDialog(val show:Boolean): CreateTribeOrInnerCircleUiEvent
    data class MemberList(val member: String, val shouldValidate: Boolean = true) :
        CreateTribeOrInnerCircleUiEvent
}