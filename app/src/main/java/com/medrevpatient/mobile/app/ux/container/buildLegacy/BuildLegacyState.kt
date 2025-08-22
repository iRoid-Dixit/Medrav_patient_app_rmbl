package com.medrevpatient.mobile.app.ux.container.buildLegacy

import android.content.Context
import android.graphics.Bitmap
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BuildLegacyUiState(
    //data
    val buildLegacyDataFlow: StateFlow<BuildLegacyDataState?> = MutableStateFlow(null),

    val tribeInnerCircleListFlow: StateFlow<PagingData<LegacyPostResponse>> = MutableStateFlow(
        PagingData.empty()
    ),
    //event
    val event: (BuildLegacyUiEvent) -> Unit = {}


)

data class BuildLegacyDataState(
    val showLoader: Boolean = false,
    val isVideoCompressing: Boolean = false, // Add video compression state
    val selectLegacyType: String = "",
    val selectLegacyErrorMsg: String? = null,
    val albumName: String = "",
    val albumNameErrorMsg: String? = null,
    val showPhotoVideoDialog: Boolean = false,
    val openPickVideoDialog: Boolean = false,
    val legacy: String = "",
    val photo: String = "",
    val video: String = "",
    val thumbnail: String = "",
    val legacyErrorMsg: String? = null,
    val selectedImages: List<Media> = emptyList(),
    val photoVideoErrorMsg: String? = null,
    val listCount: Int = 0,
    val tribeAndInnerCircleList: List<LegacyPostResponse> = emptyList(),
    val tribeAndInnerCircleErrorMsg: String? = null,
    val selectedVideos: List<Pair<Media, Bitmap>> = emptyList(),
    val oldImagePaths: List<String> = emptyList(),
    val oldVideoPaths: List<String> = emptyList(),
    val photoVideoStatus: Boolean = false,
    val isCommunityGuidelinesChecked: Boolean = false,
    val isCommunityGuidelinesErrorMsg: String? = null,


    )

sealed interface BuildLegacyUiEvent {
    data class GetContext(val context: Context) : BuildLegacyUiEvent
    data object BackClick : BuildLegacyUiEvent
    data class SelectLegacyType(val legacyType: String) : BuildLegacyUiEvent
    data class AlbumNameValueChange(val albumName: String) : BuildLegacyUiEvent
    data class ShowPhotoVideo(val show: Boolean) : BuildLegacyUiEvent
    data class OnImgPick(val photo: String) : BuildLegacyUiEvent
    data class OnVideoPick(val video: String) : BuildLegacyUiEvent
    data class LegacyValueChange(val legacy: String) : BuildLegacyUiEvent
    data class PhotoVideoStatus(val photoVideoStatus: Boolean) : BuildLegacyUiEvent
    data class RemoveImage(val photoMedia: Media) : BuildLegacyUiEvent
    data class ListCount(val listCount: Int) : BuildLegacyUiEvent
    data class RemoveVideo(val videoMedia: Media) : BuildLegacyUiEvent
    data class OnCheckedChange(val isChecked: Boolean) : BuildLegacyUiEvent
    data object PostClick : BuildLegacyUiEvent
    data class OnTribeAndInnerCircleClick(val item: List<LegacyPostResponse>) : BuildLegacyUiEvent
    data class VideoPreviewClick(val videoLink: String) : BuildLegacyUiEvent
    data class MoveMediaItem(val fromIndex: Int, val toIndex: Int) : BuildLegacyUiEvent
    data class UpdateMediaLists(
        val updatedImages: List<Media>,
        val updatedVideos: List<Pair<Media, Bitmap>>
    ) : BuildLegacyUiEvent
    data class ShowVideoCompressionLoader(val show: Boolean) : BuildLegacyUiEvent
}