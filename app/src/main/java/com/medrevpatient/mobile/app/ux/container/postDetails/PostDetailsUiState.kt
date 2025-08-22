package com.medrevpatient.mobile.app.ux.container.postDetails

import android.content.Context
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.container.comment.CommentResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PostDetailsUiState(
    //data
    val postDetailsDataFlow: StateFlow<PostDetailsUsDataState?> = MutableStateFlow(null),
    val postDetailsData: StateFlow<LegacyPostResponse?> = MutableStateFlow(null),
    val commentList: StateFlow<PagingData<CommentResponse>> = MutableStateFlow(
        PagingData.empty()),

    //event
    val event: (PostDetailsUiEvent) -> Unit = {}
)

data class PostDetailsUsDataState(

    val showLoader: Boolean = false,
    val postId:String="",
    val message: String = "",
    var showDialog: Boolean = false,
    val commentCount: Int = 0,
    val likeCount: Int = 0,
    val ownLike: Boolean = false,
    val screenName: String = "",
    val userId: String = "",

    )

sealed interface PostDetailsUiEvent {
    data class GetContext(val context:Context): PostDetailsUiEvent
    data class PostId(val postId:String): PostDetailsUiEvent
    data class ScreenName(val screenName: String) : PostDetailsUiEvent
    data object BackClick : PostDetailsUiEvent
    data class VideoPreviewClick(val mediaList: List<Media>) : PostDetailsUiEvent
    data class EditPostClick(val legacyPostData: LegacyPostResponse) : PostDetailsUiEvent
    data object DeleteLegacyPostClick : PostDetailsUiEvent
    data class DeleteDialog(val show: Boolean) : PostDetailsUiEvent
    data object PullToRefreshAPICall: PostDetailsUiEvent
    data class SendMessageValueChange(val message:String): PostDetailsUiEvent
    //data object SendMessage: PostDetailsUiEvent
    data class SendMessage(val keyboard: SoftwareKeyboardController): PostDetailsUiEvent
    data class IsLikeDisLikeAPICall(val likeDislikeId:String): PostDetailsUiEvent
    data class CommentCount(val commentCount: Int) : PostDetailsUiEvent
    data class LikeCount(val likeCount: Int, val ownLike: Boolean) : PostDetailsUiEvent
    data class ImageDisplay(val mediaList: List<Media>) : PostDetailsUiEvent
    data class ReportPost(val postId: String) : PostDetailsUiEvent
    data class ReportUser(val userId: String) : PostDetailsUiEvent
}
