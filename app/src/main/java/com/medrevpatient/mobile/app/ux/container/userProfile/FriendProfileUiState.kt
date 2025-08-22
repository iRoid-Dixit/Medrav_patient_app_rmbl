package com.medrevpatient.mobile.app.ux.container.userProfile
import android.content.Context
import com.medrevpatient.mobile.app.model.domain.response.container.friendInfo.FriendInfoResponse
import com.medrevpatient.mobile.app.model.domain.response.container.friendInfo.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FriendProfileUiState(
    //data
    val friendProfileDataFlow: StateFlow<FriendProfileDataState?> = MutableStateFlow(null),

    val friendInfoList: StateFlow<FriendInfoResponse?> = MutableStateFlow(null),

    val event: (FriendProfileUiEvent) -> Unit = {},

    )

data class FriendProfileDataState(
    val showLoader: Boolean = false,
    val isLoading: Boolean = false,
    val noDataFound: Boolean = false,
    val userId: String = "",
    val isAPISuccess: Boolean = false,
    var showDialog: Boolean = false,
    val showSendInvitationDialog: Boolean = false,

    )

sealed interface FriendProfileUiEvent {
    data class GetContext(val context: Context) : FriendProfileUiEvent
    data object BackClick : FriendProfileUiEvent
    data class ImageDisplay(val mediaList: List<Media>) : FriendProfileUiEvent

    //data class VideoPreviewClick(val videoLink: String) : FriendProfileUiEvent
    data class VideoPreviewClick(val mediaList: List<Media>) : FriendProfileUiEvent

    // data class ImageDisplay(val image: String) : FriendProfileUiEvent
    data class GetUserId(val userId: String) : FriendProfileUiEvent
    data object OnPostNextPage : FriendProfileUiEvent
    data object AddInnerCircle : FriendProfileUiEvent
    data class BlockUserDialog(val show: Boolean) : FriendProfileUiEvent
    data object BlockUser : FriendProfileUiEvent
    data object SmsClick : FriendProfileUiEvent
    data object EmailClick : FriendProfileUiEvent
    data class OnSendInvitationDialog(val invitationDialog: Boolean) : FriendProfileUiEvent

}