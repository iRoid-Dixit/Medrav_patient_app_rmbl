package com.medrevpatient.mobile.app.ux.main.griotLegacy

import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class GriotLegacyUiState(
    //data
    val griotLegacyUiDataFlow: StateFlow<GriotLegacyUiDataState?> = MutableStateFlow(null),
    val allLegacyPostListFlow: StateFlow<PagingData<LegacyPostResponse>> = MutableStateFlow(
        PagingData.empty()),
    //event
    val event: (GriotLegacyUiEvent) -> Unit = {}
)
data class GriotLegacyUiDataState(
    val tabIndex: Int = 0,
)
sealed interface GriotLegacyUiEvent {
    data object PullToRefreshAPICall: GriotLegacyUiEvent
    data class TabClick(val type:Int): GriotLegacyUiEvent
    data class PostDetailsClick(val postId:String): GriotLegacyUiEvent
    data object BuildLegacyClick : GriotLegacyUiEvent
    data class VideoPreviewClick(val mediaList: List<Media>) : GriotLegacyUiEvent
    data class ImageDisplay(val mediaList: List<Media>) : GriotLegacyUiEvent
    data object NavigateToNotification : GriotLegacyUiEvent

}