package com.griotlegacy.mobile.app.ux.main.home

import android.content.Context
import androidx.paging.PagingData
import com.griotlegacy.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.griotlegacy.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    //data
    val homeUiDataFlow: StateFlow<HomeUiDataState?> = MutableStateFlow(null),
    val mainVillageList: StateFlow<PagingData<LegacyPostResponse>> = MutableStateFlow(
        PagingData.empty()
    ),
    val advertisementList: StateFlow<PagingData<AdvertisementResponse>> = MutableStateFlow(
        PagingData.empty()
    ),
    //event
    val event: (HomeUiEvent) -> Unit = {}
)
data class HomeUiDataState(
    val userName: String = "",
    val showLoader: Boolean = false,
    val userId: String = "",
    var isRefreshData: Boolean = false,
    val postId: String = ""

)
sealed interface HomeUiEvent {
    data class IsLikeDisLikeAPICall(val likeDislikeId: String) : HomeUiEvent
    data class ImageDisplay(val mediaList: List<com.griotlegacy.mobile.app.model.domain.response.container.legacyPost.Media>) :
        HomeUiEvent
    data class GetContext(val context: Context) : HomeUiEvent
    data class RefreshData(val refresh: Boolean) : HomeUiEvent
    data object PullToRefreshAPICall : HomeUiEvent
    data class NavigateToPostDetails(
        val postId: String,

    ) : HomeUiEvent
    data class VideoPreviewClick(val mediaList: List<com.griotlegacy.mobile.app.model.domain.response.container.legacyPost.Media>) :
        HomeUiEvent
    data class ReportPost(val postId: String) : HomeUiEvent
    data class ReportUser(val userId: String) : HomeUiEvent
    data object NavigateToNotification : HomeUiEvent
    data object UpdateVillageList : HomeUiEvent
    data class PostId(val postId: String) : HomeUiEvent
    data class AdvertisementClick(val advertisementId: String) : HomeUiEvent
    data object BackClick : HomeUiEvent

}