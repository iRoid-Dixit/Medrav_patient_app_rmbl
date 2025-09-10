package com.medrevpatient.mobile.app.ux.container.buildLegacy

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.imagePostionReq.ImagePositionReq
import com.medrevpatient.mobile.app.model.domain.request.imagePostionReq.MediaPosition
import com.medrevpatient.mobile.app.model.domain.request.mainReq.deletePost.SinglePostReq
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.Media
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.createMultipartBody
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWarningMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.ux.main.videoLoad.ImageVideoPlayerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class GetBuildLegacyUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,

    ) {
    private val isOffline = MutableStateFlow(false)
    private val legacyPostId = MutableStateFlow("")
    private val legacyPostData = MutableStateFlow(LegacyPostResponse())
    private lateinit var context: Context
    private val buildLegacyDataFlow =
        MutableStateFlow(BuildLegacyDataState())
    private val tribeInnerCircleList =
        MutableStateFlow<PagingData<LegacyPostResponse>>(PagingData.empty())

    operator fun invoke(
        context: Context,
        legacyPostData: String,
        screeName: String,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): BuildLegacyUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        if (screeName == Constants.AppScreen.BUILD_LEGACY_SCREEN || screeName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) {
            val legacyPost: LegacyPostResponse? =
                Gson().fromJson(legacyPostData, LegacyPostResponse::class.java)
            Log.d("TAG", "legacyPost: $legacyPost")
            legacyPostId.value = legacyPost?.id ?: ""
            this.legacyPostData.value = legacyPost ?: LegacyPostResponse()
            val legacyType = when (legacyPost?.type) {
                1 -> {
                    // Always load tribe list when type is tribe (for both create and edit)
                    getInnerCircleTribe(coroutineScope, type = 1)
                    context.getString(R.string.tribe)
                }

                2 -> {

                    // Always load inner circle list when type is inner circle (for both create and edit)
                    getInnerCircleTribe(coroutineScope, type = 2)
                    context.getString(R.string.innercircle)
                }

                3 -> context.getString(R.string.village)
                else -> context.getString(R.string.justme)
            }
            // Convert media list to selected images and videos

            val (selectedImages, selectedVideos) = legacyPost?.media?.partition { it.type == 1 }
                ?.let { (images, videos) ->
                    Pair(
                        images.map {
                            Media(
                                filename = it.filename ?: "",
                                id = it.id
                            )
                        },
                        videos.mapNotNull { video ->
                            if (!video.thumbnail.isNullOrBlank()) {
                                Pair(
                                    Media(
                                        filename = video.filename ?: "",
                                        id = video.id,
                                        thumbnail = video.thumbnail // ✅ THIS FIX
                                    ),
                                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                                )
                            } else null
                        }
                    )
                } ?: Pair(emptyList(), emptyList())

            // Update the state on the main thread

            buildLegacyDataFlow.update { profileUiDataState ->
                val initialSelections = legacyPost?.tribeList?.map {
                    LegacyPostResponse(id = it)
                } ?: emptyList()
                profileUiDataState.copy(
                    albumName = legacyPost?.albumName ?: "",
                    legacy = legacyPost?.legacyText ?: "",
                    selectLegacyType = legacyType,
                    tribeAndInnerCircleList = initialSelections,
                    selectedImages = selectedImages,
                    selectedVideos = selectedVideos,
                )
            }
        }

        return BuildLegacyUiState(
            buildLegacyDataFlow = buildLegacyDataFlow,
            tribeInnerCircleListFlow = tribeInnerCircleList,
            event = { aboutUsEvent ->
                createTribeOrInnerCircleUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    screeName = screeName,

                    )
            }
        )
    }


    private fun saveThumbnailToFile(context: Context, bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        val file = File(context.cacheDir, "video_thumbnail_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file.absolutePath
    }

    private fun createTribeOrInnerCircleUiEvent(
        event: BuildLegacyUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
        screeName: String
    ) {
        when (event) {
            BuildLegacyUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }

            is BuildLegacyUiEvent.GetContext -> {
                this.context = event.context
            }

            is BuildLegacyUiEvent.AlbumNameValueChange -> {
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    albumName = event.albumName,
                    albumNameErrorMsg = validationUseCase.emptyFieldValidation(
                        event.albumName,
                        context.getString(R.string.please_enter_your_album_name)
                    ).errorMsg
                )

            }

            is BuildLegacyUiEvent.LegacyValueChange -> {
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    legacy = event.legacy,
                    legacyErrorMsg = validationUseCase.emptyFieldValidation(
                        event.legacy,
                        context.getString(R.string.please_enter_your_legacy)
                    ).errorMsg

                )
            }

            is BuildLegacyUiEvent.SelectLegacyType -> {
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    selectLegacyType = event.legacyType,
                    selectLegacyErrorMsg = groupValidation(
                        event.legacyType,
                        context = context
                    ).errorMsg
                )
                when (event.legacyType) {
                    context.getString(R.string.tribe) -> {
                        getInnerCircleTribe(coroutineScope, type = 1) // Tribe type
                    }

                    context.getString(R.string.innercircle) -> {
                        getInnerCircleTribe(coroutineScope, type = 2) // InnerCircle type
                    }
                }
            }

            is BuildLegacyUiEvent.ShowPhotoVideo -> {
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    showPhotoVideoDialog = event.show
                )
            }


            is BuildLegacyUiEvent.OnImgPick -> {
                val updatedImages = buildLegacyDataFlow.value.selectedImages + Media(
                    filename = event.photo,
                    isEdited = true,
                    id = null // Add other required fields if necessary
                )
                val updatedVideos = buildLegacyDataFlow.value.selectedVideos.map {
                    (it.first.filename ?: "") to it.second // Ensure filename is non-null
                }
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    selectedImages = updatedImages,
                    photoVideoErrorMsg = mediaSelectionValidation(
                        updatedImages.map { it.filename ?: "" }, // Map Media to List<String>
                        updatedVideos,
                        context
                    ).errorMsg
                )
                if (screeName == Constants.AppScreen.BUILD_LEGACY_SCREEN || screeName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) {
                    addImageToLegacyPost(coroutineScope)
                }
            }

            is BuildLegacyUiEvent.OnVideoPick -> {
                val newVideoMedia = Media(
                    filename = event.video,
                    isEdited = true,  // Set this flag as the video is being edited
                    id = null // New videos won't have an ID initially
                )

                // Generate thumbnail for the selected video
                val thumbnail = generateVideoThumbnail(event.video) ?: Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                )
                // Save the generated thumbnail to a file
                val savedThumbnailPath = saveThumbnailToFile(context, thumbnail)

                // Update the video media with the saved thumbnail path
                val updatedVideoMedia = newVideoMedia.copy(thumbnail = savedThumbnailPath)

                val updatedVideos =
                    buildLegacyDataFlow.value.selectedVideos + (updatedVideoMedia to thumbnail)
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    selectedVideos = updatedVideos,
                    photoVideoErrorMsg = mediaSelectionValidation(
                        buildLegacyDataFlow.value.selectedImages.map { it.filename ?: "" },
                        updatedVideos.map { (media, bitmap) -> (media.filename ?: "") to bitmap },
                        context
                    ).errorMsg
                )
                if (screeName == Constants.AppScreen.BUILD_LEGACY_SCREEN || screeName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) {
                    addImageToLegacyPost(coroutineScope)
                }
            }
            is BuildLegacyUiEvent.PhotoVideoStatus -> {
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    photoVideoStatus = event.photoVideoStatus
                )
            }
            is BuildLegacyUiEvent.RemoveImage -> {
                if (screeName == Constants.AppScreen.BUILD_LEGACY_SCREEN || screeName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) {
                    // Find the media item that matches the path
                    val mediaToDelete =
                        buildLegacyDataFlow.value.selectedImages.find { it.filename == event.photoMedia.filename }
                    mediaToDelete?.id?.let { mediaId ->
                        val singleImageDeleteReq = SinglePostReq(
                            postId = legacyPostData.value.id,
                            mediaId = mediaId
                        )
                        legacySingleImagePostDelete(
                            coroutineScope = coroutineScope,
                            event,
                            singlePostReq = singleImageDeleteReq
                        )
                    } ?: run {
                        // If no ID found (newly added image), just remove it
                        val updatedImages =
                            buildLegacyDataFlow.value.selectedImages.filterNot { it.filename == event.photoMedia.filename }
                        val updatedVideos = buildLegacyDataFlow.value.selectedVideos
                        buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                            selectedImages = updatedImages,
                            photoVideoErrorMsg = mediaSelectionValidation(
                                updatedImages.map {
                                    it.filename ?: ""
                                }, // Ensure non-null filenames
                                updatedVideos.map {
                                    (it.first.filename ?: "") to it.second
                                }, // Ensure non-null filenames
                                context
                            ).errorMsg
                        )
                    }
                } else {
                    val updatedImages =
                        buildLegacyDataFlow.value.selectedImages.filterNot { it.filename == event.photoMedia.filename }
                    val updatedVideos = buildLegacyDataFlow.value.selectedVideos
                    buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                        selectedImages = updatedImages,
                        photoVideoErrorMsg = mediaSelectionValidation(
                            updatedImages.map { it.filename ?: "" }, // Ensure non-null filenames
                            updatedVideos.map {
                                (it.first.filename ?: "") to it.second
                            }, // Ensure non-null filenames
                            context
                        ).errorMsg
                    )
                }
            }


            is BuildLegacyUiEvent.OnCheckedChange -> {
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    isCommunityGuidelinesChecked = event.isChecked,
                    isCommunityGuidelinesErrorMsg = communityGuidelinesValidation(
                        event.isChecked,
                        context
                    ).errorMsg
                )
            }

            BuildLegacyUiEvent.PostClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val selectLegacyTpeResult =
                            groupValidation(
                                buildLegacyDataFlow.value.selectLegacyType,
                                context = context
                            )
                        val albumNameValidationResult =
                            validationUseCase.emptyFieldValidation(
                                buildLegacyDataFlow.value.albumName,
                                context.getString(R.string.please_enter_your_album_name)
                            )

                        val legacyValidationResult = validationUseCase.emptyFieldValidation(
                            buildLegacyDataFlow.value.legacy,
                            context.getString(R.string.please_enter_your_legacy)
                        )


                        val mediaSelectionResult = mediaSelectionValidation(
                            buildLegacyDataFlow.value.selectedImages.map {
                                it.filename ?: ""
                            }, // Map Media to List<String>
                            buildLegacyDataFlow.value.selectedVideos.map {
                                (it.first.filename ?: "") to it.second
                            }, // Map Pair<Media, Bitmap> to Pair<String, Bitmap>
                            context
                        )
                        val communityGuidelinesResult = communityGuidelinesValidation(
                            buildLegacyDataFlow.value.isCommunityGuidelinesChecked,
                            context
                        )
                        /* val tribeSelectionResult = if (buildLegacyDataFlow.value.listCount != 0) {
                             tribeSelectionValidation(
                                 buildLegacyDataFlow.value.selectLegacyType,
                                 buildLegacyDataFlow.value.tribeAndInnerCircleList,
                                 context
                             )
                         } else {
                             ValidationResult(isSuccess = true, errorMsg = null)
                         }*/

                        val hasError = listOf(
                            selectLegacyTpeResult,
                            albumNameValidationResult,
                            legacyValidationResult,
                            mediaSelectionResult,
                            communityGuidelinesResult,
                            //tribeSelectionResult
                        ).any { !it.isSuccess }
                        // 🔹 **Update all error messages in one go**
                        buildLegacyDataFlow.update { state ->
                            state.copy(
                                selectLegacyErrorMsg = selectLegacyTpeResult.errorMsg,
                                albumNameErrorMsg = albumNameValidationResult.errorMsg,
                                legacyErrorMsg = legacyValidationResult.errorMsg,
                                photoVideoErrorMsg = mediaSelectionResult.errorMsg,
                                isCommunityGuidelinesErrorMsg = communityGuidelinesResult.errorMsg,
                                // tribeAndInnerCircleErrorMsg = tribeSelectionResult.errorMsg
                            )
                        }
                        if (hasError) return
                    }
                    if (screeName == Constants.AppScreen.BUILD_LEGACY_SCREEN || screeName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) {
                        updateLegacyPost(
                            coroutineScope = coroutineScope,
                            navigation = navigate,
                            screenName = screeName
                        )
                    } else {
                        crateLegacyPost(coroutineScope = coroutineScope, navigation = navigate)
                    }
                } else {
                    showWarningMessage(
                        this.context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }

            is BuildLegacyUiEvent.OnTribeAndInnerCircleClick -> {
                val newList = event.item
                val validationResult = tribeSelectionValidation(
                    buildLegacyDataFlow.value.selectLegacyType,
                    newList,
                    context
                )
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    tribeAndInnerCircleList = newList,
                    tribeAndInnerCircleErrorMsg = validationResult.errorMsg
                )
            }

            is BuildLegacyUiEvent.VideoPreviewClick -> {
                val intent = Intent(context, ImageVideoPlayerActivity::class.java).apply {
                    putExtra(Constants.Values.VIDEO_LINK, event.videoLink)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // ✅ this is the fix
                }
                context.startActivity(intent)
            }

            is BuildLegacyUiEvent.ListCount -> {
                buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                    listCount = event.listCount
                )

            }

            is BuildLegacyUiEvent.MoveMediaItem -> {
                buildLegacyDataFlow.update { state ->
                    val newImages = state.selectedImages.toMutableList()
                    val newVideos = state.selectedVideos.toMutableList()

                    if (event.fromIndex < newImages.size && event.toIndex < newImages.size) {
                        // Image to Image reorder
                        val item = newImages.removeAt(event.fromIndex)
                        newImages.add(event.toIndex, item)
                    } else if (event.fromIndex >= newImages.size && event.toIndex >= newImages.size) {
                        // Video to Video reorder
                        val videoIndex = event.fromIndex - newImages.size
                        val targetIndex = event.toIndex - newImages.size
                        val item = newVideos.removeAt(videoIndex)
                        newVideos.add(targetIndex, item)
                    } else if (event.fromIndex < newImages.size) {
                        // Image moved to video position
                        val image = newImages.removeAt(event.fromIndex)
                        val videoIndex = event.toIndex - newImages.size
                        val placeholderThumbnail =
                            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                        newVideos.add(videoIndex, image to placeholderThumbnail)
                    } else {
                        // Video moved to image position
                        val videoIndex = event.fromIndex - newImages.size
                        val (video, thumbnailBitmap) = newVideos.removeAt(videoIndex)

                        // Save thumbnail from Pair if needed
                        val thumbnailPath =
                            if (video.thumbnail.isNullOrEmpty()) {
                                saveThumbnailToFile(context, thumbnailBitmap)
                            } else {
                                video.thumbnail
                            }

                        newImages.add(
                            event.toIndex, Media(
                                filename = video.filename,
                                id = video.id,
                                thumbnail = thumbnailPath,
                                isEdited = video.isEdited
                            )
                        )
                    }
                    state.copy(
                        selectedImages = newImages,
                        selectedVideos = newVideos
                    )
                }
            }
            is BuildLegacyUiEvent.RemoveVideo -> {
                if (screeName == Constants.AppScreen.BUILD_LEGACY_SCREEN || screeName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) {
                    // Create SinglePostReq and pass it to the function
                    val mediaToDelete =
                        buildLegacyDataFlow.value.selectedVideos.find { it.first == event.videoMedia }

                    if (mediaToDelete != null) {
                        val updatedVideos =
                            buildLegacyDataFlow.value.selectedVideos.filterNot { it.first == event.videoMedia }

                        mediaToDelete.first.id?.let { mediaId ->
                            // If the video has an ID, make an API call to delete it
                            val singleVideoDeleteReq = SinglePostReq(
                                postId = legacyPostData.value.id,
                                mediaId = mediaId
                            )
                            legacySingleVideoPostDelete(
                                coroutineScope = coroutineScope,
                                event = event,
                                singlePostReq = singleVideoDeleteReq
                            )
                        } ?: run {
                            // If the video does not have an ID, just update the local state
                            buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                                selectedVideos = updatedVideos,
                                photoVideoErrorMsg = mediaSelectionValidation(
                                    buildLegacyDataFlow.value.selectedImages.map {
                                        it.filename ?: ""
                                    },
                                    updatedVideos.map { (it.first.filename ?: "") to it.second },
                                    context
                                ).errorMsg
                            )
                        }
                    }
                } else {
                    // Remove video locally
                    val updatedVideos =
                        buildLegacyDataFlow.value.selectedVideos.filterNot { it.first == event.videoMedia }
                    val updatedImages = buildLegacyDataFlow.value.selectedImages
                    buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                        selectedVideos = updatedVideos,
                        photoVideoErrorMsg = mediaSelectionValidation(
                            updatedImages.map { it.filename ?: "" }, // Map Media to List<String>
                            updatedVideos.map {
                                (it.first.filename ?: "") to it.second
                            }, // Map Pair<Media, Bitmap> to Pair<String, Bitmap>
                            context
                        ).errorMsg
                    )
                }
            }

            is BuildLegacyUiEvent.UpdateMediaLists -> {
                buildLegacyDataFlow.update { state ->
                    // Remove duplicates from the selectedImages list
                    val uniqueImages = event.updatedImages.distinctBy { it.id ?: it.filename }
                    val uniqueVideos =
                        event.updatedVideos.distinctBy { it.first.id ?: it.first.filename }

                    state.copy(
                        selectedImages = uniqueImages,
                        selectedVideos = uniqueVideos
                    )
                }
                /* if (screeName == Constants.AppScreen.BUILD_LEGACY_SCREEN || screeName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) {
                     val positionArray = preparePositionArray()
                     // Call the API to update image positions
                     imagePositionChangeAPI(
                         coroutineScope,
                         ImagePositionReq(postId = legacyPostId.value, position = positionArray)
                     )
                 }*/

            }

            is BuildLegacyUiEvent.ShowVideoCompressionLoader -> {
                buildLegacyDataFlow.update { state ->
                    state.copy(isVideoCompressing = event.show)
                }
            }
        }
    }

    private fun legacySingleImagePostDelete(
        coroutineScope: CoroutineScope,
        event: BuildLegacyUiEvent.RemoveImage,
        singlePostReq: SinglePostReq
    ) {
        coroutineScope.launch {
            apiRepository.singlePostDelete(singlePostReq = singlePostReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        val updatedImages = buildLegacyDataFlow.value.selectedImages
                            .filterNot { it1 ->
                                it1.filename == event.photoMedia.filename
                            } // Compare filenames
                        val updatedVideos = buildLegacyDataFlow.value.selectedVideos

                        buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                            selectedImages = updatedImages,
                            photoVideoErrorMsg = mediaSelectionValidation(
                                updatedImages.map { it1 ->
                                    it1.filename ?: ""
                                }, // Map Media to List<String>
                                updatedVideos.map { it1 ->
                                    (it1.first.filename ?: "") to it1.second
                                }, // Map Pair<Media, Bitmap> to Pair<String, Bitmap>
                                context
                            ).errorMsg
                        )
                        showSuccessMessage(context = context, it.data?.message ?: "")
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                    }
                }
            }
        }
    }

    private fun imagePositionChangeAPI(
        coroutineScope: CoroutineScope,
        imagePositionReq: ImagePositionReq
    ) {
        coroutineScope.launch {
            apiRepository.imagePositionChange(imagePositionReq = imagePositionReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        // showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        //  showSuccessMessage(context = context, it.data?.message ?: "")

                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                    }
                }
            }
        }
    }

    private fun generateVideoThumbnail(videoPath: String): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoPath)
            // Extract a frame at the 1-second mark (or the closest available frame)
            val bitmap = retriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST)
            retriever.release() // Always release the retriever to avoid memory leaks
            bitmap ?: Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Fallback to a blank bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Fallback to a blank bitmap
        }
    }

    private fun legacySingleVideoPostDelete(
        coroutineScope: CoroutineScope,
        event: BuildLegacyUiEvent.RemoveVideo,
        singlePostReq: SinglePostReq
    ) {
        coroutineScope.launch {
            apiRepository.singlePostDelete(singlePostReq = singlePostReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        // Filter out the video from the local state
                        val updatedVideos = buildLegacyDataFlow.value.selectedVideos
                            .filterNot { video -> video.first.id == event.videoMedia.id } // Compare by ID
                        val updatedImages = buildLegacyDataFlow.value.selectedImages

                        // Update the state
                        buildLegacyDataFlow.value = buildLegacyDataFlow.value.copy(
                            selectedVideos = updatedVideos,
                            photoVideoErrorMsg = mediaSelectionValidation(
                                updatedImages.map { it1 ->
                                    it1.filename ?: ""
                                }, // Map Media to List<String>
                                updatedVideos.map { it1 ->
                                    (it1.first.filename ?: "") to it1.second
                                }, // Map Pair<Media, Bitmap> to Pair<String, Bitmap>
                                context
                            ).errorMsg
                        )
                        showSuccessMessage(context = context, it.data?.message ?: "")
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                    }
                }
            }
        }
    }

    private fun communityGuidelinesValidation(
        isChecked: Boolean,
        context: Context
    ): ValidationResult {
        return ValidationResult(
            isSuccess = isChecked,
            errorMsg = if (!isChecked) context.getString(R.string.you_must_accept_the_community_guidelines_to_continue) else null
        )
    }

    private fun getInnerCircleTribe(coroutineScope: CoroutineScope, type: Int) {
        coroutineScope.launch {
            apiRepository.getInnerCircleTribe(type).cachedIn(coroutineScope).collect { pagingData ->
                tribeInnerCircleList.value = pagingData
            }
        }
    }

    private fun tribeSelectionValidation(
        type: String?,
        selectedList: List<LegacyPostResponse?>,
        context: Context
    ): ValidationResult {
        val isTribeType = type == context.getString(R.string.tribe)
        val isInnerCircleType = type == context.getString(R.string.innercircle)

        return when {
            isTribeType && selectedList.isEmpty() -> ValidationResult(
                isSuccess = false,
                errorMsg = context.getString(R.string.please_select_at_least_one_tribe)
            )

            isInnerCircleType && selectedList.isEmpty() -> ValidationResult(
                isSuccess = false,
                errorMsg = context.getString(R.string.please_select_at_least_one_innercircle)
            )

            else -> ValidationResult(isSuccess = true)
        }
    }

    private fun mediaSelectionValidation(
        selectedImages: List<String>,
        selectedVideos: List<Pair<String, Bitmap>>,
        context: Context
    ): ValidationResult {
        val hasSelectedMedia = selectedImages.isNotEmpty() || selectedVideos.isNotEmpty()
        return ValidationResult(
            isSuccess = hasSelectedMedia,
            errorMsg = if (!hasSelectedMedia) context.getString(R.string.please_select_image_or_video) else null
        )
    }

    private fun crateLegacyPost(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit,

        ) {
        val legacyTypeValue = when (buildLegacyDataFlow.value.selectLegacyType) {
            context.getString(R.string.tribe) -> "1"
            context.getString(R.string.innercircle) -> "2"
            context.getString(R.string.village) -> "3"
            else -> "4"
        }
        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.CreateLegacyPost.TYPE] =
            legacyTypeValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.CreateLegacyPost.ALBUM_NAME] =
            buildLegacyDataFlow.value.albumName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.CreateLegacyPost.LEGACY_TEXT] =
            buildLegacyDataFlow.value.legacy.toRequestBody("multipart/form-data".toMediaTypeOrNull())


        map[Constants.CreateLegacyPost.LEGACY_TEXT] =
            buildLegacyDataFlow.value.legacy.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (buildLegacyDataFlow.value.selectLegacyType == context.getString(R.string.tribe) || buildLegacyDataFlow.value.selectLegacyType == context.getString(
                R.string.innercircle
            )
        ) {
            val tribeIds = buildLegacyDataFlow.value.tribeAndInnerCircleList
                .joinToString(",") { it.id.toString() } // Assuming `id` is the property for the tribe ID

            if (tribeIds.isNotEmpty()) map[Constants.CreateLegacyPost.TRIBE_LIST] =
                tribeIds.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        val mediaFiles: ArrayList<File?> = arrayListOf()
        buildLegacyDataFlow.value.selectedImages.forEach { mediaItem ->
            if (mediaItem.filename?.contains("https") == false) {
                mediaFiles.add(File(mediaItem.filename.toString()))
            }
        }
        buildLegacyDataFlow.value.selectedVideos.forEach { mediaItem ->
            if (mediaItem.first.filename?.contains("https") == false) {
                mediaFiles.add(File(mediaItem.first.filename.toString()))
            }
        }
        coroutineScope.launch {
            apiRepository.createLegacyPost(
                map,
                media = createMultipartBody(mediaFiles, Constants.CreateLegacyPost.MEDIA)
            ).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        showOrHideLoader(false)
                        navigateToMainScreens(
                            context = context,
                            navigate = navigation,
                            screenName = Constants.AppScreen.BUILD_LEGACY_SCREEN
                        )
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }
                }
            }
        }
    }
    private fun updateLegacyPost(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit,
        screenName: String,
    ) {
        val legacyTypeValue = when (buildLegacyDataFlow.value.selectLegacyType) {
            context.getString(R.string.tribe) -> "1"
            context.getString(R.string.innercircle) -> "2"
            context.getString(R.string.village) -> "3"
            else -> "4"
        }
        val map: HashMap<String, RequestBody> = hashMapOf()

        map[Constants.CreateLegacyPost.POST_ID] =
            legacyPostId.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.CreateLegacyPost.TYPE] =
            legacyTypeValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.CreateLegacyPost.ALBUM_NAME] =
            buildLegacyDataFlow.value.albumName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.CreateLegacyPost.LEGACY_TEXT] =
            buildLegacyDataFlow.value.legacy.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.CreateLegacyPost.LEGACY_TEXT] =
            buildLegacyDataFlow.value.legacy.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (buildLegacyDataFlow.value.selectLegacyType == context.getString(R.string.tribe) || buildLegacyDataFlow.value.selectLegacyType == context.getString(
                R.string.innercircle
            )
        ) {
            val tribeIds = buildLegacyDataFlow.value.tribeAndInnerCircleList
                .joinToString(",") { it.id.toString() } // Assuming `id` is the property for the tribe ID
            if (tribeIds.isNotEmpty()) map[Constants.CreateLegacyPost.TRIBE_LIST] =
                tribeIds.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }

        val mediaFiles: ArrayList<File?> = arrayListOf()
        buildLegacyDataFlow.value.selectedImages.forEach { imagePath ->
            if (imagePath.filename?.contains("https") == false) {
                val file = File(imagePath.filename)
                if (file.exists()) {
                    mediaFiles.add(file)
                } else {
                    Log.e("TAG", "Image file does not exist: ${imagePath.filename}")
                }
            }
        }

        buildLegacyDataFlow.value.selectedVideos.forEach { (videoPath, _) ->
            if (videoPath.filename?.contains("https") == false) {
                val file = File(videoPath.filename)
                if (file.exists()) {
                    mediaFiles.add(file)
                } else {
                    Log.e("TAG", "Video file does not exist: ${videoPath.filename}")
                }
            }
        }
        Log.d(
            "TAG",
            "crateLegacyPost: ${buildLegacyDataFlow.value.selectedImages},mediaList:$mediaFiles"
        )
        Log.d("TAG", "updateLegacyPost: ${buildLegacyDataFlow.value.selectedImages}")
        coroutineScope.launch {
            apiRepository.updateLegacyPost(
                map
            ).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        Log.d("TAG", "updateLegacyPost: ${it.message}")
                        Log.d("TAG", "updateLegacyPost: ${legacyPostId.value}")
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        showOrHideLoader(false)
                        navigateToMainScreens(
                            context = context,
                            navigate = navigation,
                            screenName = if (screenName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) Constants.AppScreen.MAIN_VILLAGE_SCREEN else Constants.AppScreen.BUILD_LEGACY_SCREEN
                        )

                        if (screenName == Constants.AppScreen.BUILD_LEGACY_SCREEN || screenName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) {
                            val positionArray = preparePositionArray()
                            // Call the API to update image positions
                            imagePositionChangeAPI(
                                coroutineScope,
                                ImagePositionReq(
                                    postId = legacyPostId.value,
                                    position = positionArray
                                )
                            )
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }
                }
            }
        }
    }

    private fun preparePositionArray(): List<MediaPosition> {
        val currentState = buildLegacyDataFlow.value
        val positions = mutableListOf<MediaPosition>()

        // Add image positions (only those with IDs)
        currentState.selectedImages.forEachIndexed { index, media ->
            media.id?.let { positions.add(MediaPosition(it, index)) }
        }

        // Add video positions (only those with IDs)
        currentState.selectedVideos.forEachIndexed { index, (media, _) ->
            media.id?.let {
                positions.add(
                    MediaPosition(
                        it,
                        currentState.selectedImages.size + index
                    )
                )
            }
        }

        return positions
    }

    private fun addImageToLegacyPost(
        coroutineScope: CoroutineScope,

        ) {
        val map: HashMap<String, RequestBody> = hashMapOf()
        val mediaFiles: ArrayList<File?> = arrayListOf()
        map[Constants.CreateLegacyPost.POST_ID] =
            legacyPostId.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        buildLegacyDataFlow.value.selectedImages
            .filter { it.id.isNullOrEmpty() && it.filename?.startsWith("http") == false }
            .forEach { mediaFiles.add(File(it.filename!!)) }

        buildLegacyDataFlow.value.selectedVideos
            .filter { it.first.id.isNullOrEmpty() && it.first.filename?.startsWith("http") == false }
            .forEach { mediaFiles.add(File(it.first.filename!!)) }
        coroutineScope.launch {
            apiRepository.addImageToLegacyPost(
                map,
                media = createMultipartBody(mediaFiles, Constants.CreateLegacyPost.MEDIA)
            ).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        Log.d("TAG", "updateLegacyPost: ${it.message}")
                        Log.d("TAG", "updateLegacyPost: ${legacyPostId.value}")
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        showOrHideLoader(false)
                        val updatedImages = buildLegacyDataFlow.value.selectedImages.map { image ->
                            it.data?.data?.find { apiMedia -> apiMedia.filename == image.filename }
                                ?.let { apiMedia ->
                                    image.copy(id = apiMedia.id)
                                } ?: image
                        }

                        val updatedVideos =
                            buildLegacyDataFlow.value.selectedVideos.map { (video, bitmap) ->
                                it.data?.data?.find { apiMedia -> apiMedia.filename == video.filename }
                                    ?.let { apiMedia ->
                                        video.copy(id = apiMedia.id) to bitmap
                                    } ?: (video to bitmap)
                            }

                        buildLegacyDataFlow.update { state ->
                            state.copy(
                                selectedImages = updatedImages,
                                selectedVideos = updatedVideos
                            )
                        }

                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }
                }
            }

        }
    }


    private fun navigateToMainScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }


    private fun groupValidation(selectLegacy: String?, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = !selectLegacy.isNullOrBlank(),
            errorMsg = if (selectLegacy.isNullOrBlank()) context.getString(R.string.please_select_your_legacy_type) else null
        )
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        buildLegacyDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

}


