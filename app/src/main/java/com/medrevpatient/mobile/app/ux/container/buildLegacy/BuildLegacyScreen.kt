package com.medrevpatient.mobile.app.ux.container.buildLegacy

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.Media
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextFieldMultipleLine
import com.medrevpatient.mobile.app.ui.compose.common.CommunityGuidelinesText
import com.medrevpatient.mobile.app.ui.compose.common.DropdownField
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.dialog.CameraGalleryDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoaderWithMessage
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.compose.common.permission.PhotoPickerManager
import com.medrevpatient.mobile.app.ui.compose.common.permission.VideoPickerManager
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray3E
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun BuildLegacyScreen(
    navController: NavController,
    viewModel: BuildLegacyViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val createTribeOrInnerCircleUiState by uiState.buildLegacyDataFlow.collectAsStateWithLifecycle()
    uiState.event(BuildLegacyUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                onClick = { navController.popBackStack() },
                titleText = "BMI & Health Check"
            )
        },
        navBarData = null
    ) {
        BuildLegacyScreenContent(uiState, uiState.event)
    }
    if (createTribeOrInnerCircleUiState?.showLoader == true) {
        CustomLoader()
    }
    if (createTribeOrInnerCircleUiState?.isVideoCompressing == true) {
        CustomLoaderWithMessage(stringResource(R.string.compressing_video_please_wait))
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun BuildLegacyScreenContent(
    uiState: BuildLegacyUiState,
    event: (BuildLegacyUiEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val buildLegacyUiState by uiState.buildLegacyDataFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize() // Ensures the Column has bounded height
            .imePadding()
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        BuildLegacyInputField(event, buildLegacyUiState)
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            SelectPhotoVideoComponent(uiState, buildLegacyUiState)
            Spacer(modifier = Modifier.width(15.dp))
            if (!buildLegacyUiState?.selectedImages.isNullOrEmpty() ||
                !buildLegacyUiState?.selectedVideos.isNullOrEmpty()
            ) {
                SelectedMediaPreview(
                    selectedImages = buildLegacyUiState?.selectedImages ?: emptyList(),
                    selectedVideos = buildLegacyUiState?.selectedVideos ?: emptyList(),
                    onRemoveImage = {
                        event(BuildLegacyUiEvent.RemoveImage(it))
                    },
                    onRemoveVideo = {
                        event(BuildLegacyUiEvent.RemoveVideo(it))
                    },
                    uiState,
                    onReorder = { fromIndex, toIndex ->
                        val currentImages = buildLegacyUiState?.selectedImages ?: emptyList()
                        val currentVideos = buildLegacyUiState?.selectedVideos ?: emptyList()

                        val updatedImages = currentImages.toMutableList()
                        val updatedVideos = currentVideos.toMutableList()

                        val imageSize = currentImages.size

                        if (fromIndex < imageSize && toIndex < imageSize) {
                            // 🔁 Image to Image reorder
                            Collections.swap(updatedImages, fromIndex, toIndex)
                        } else if (fromIndex >= imageSize && toIndex >= imageSize) {
                            // 🔁 Video to Video reorder
                            val fromVideoIndex = fromIndex - imageSize
                            val toVideoIndex = toIndex - imageSize
                            Collections.swap(updatedVideos, fromVideoIndex, toVideoIndex)
                        } else if (fromIndex < imageSize) {
                            // 🔁 Image to Video position
                            val image = updatedImages.removeAt(fromIndex)
                            val insertVideoIndex =
                                (toIndex - updatedImages.size).coerceIn(0, updatedVideos.size)
                            val placeholderThumbnail =
                                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                            updatedVideos.add(insertVideoIndex, image to placeholderThumbnail)
                        } else {
                            // 🔁 Video to Image position
                            val fromVideoIndex = fromIndex - imageSize
                            val (video) = updatedVideos.removeAt(fromVideoIndex)
                            val insertImageIndex = toIndex.coerceIn(0, updatedImages.size)
                            updatedImages.add(
                                insertImageIndex,
                                video.copy(thumbnail = video.thumbnail ?: "")
                            )
                        }

                        //  Trigger state update and API call
                        uiState.event(
                            BuildLegacyUiEvent.UpdateMediaLists(
                                updatedImages = updatedImages,
                                updatedVideos = updatedVideos
                            )
                        )
                    }
                )
            }
        }
        if (buildLegacyUiState?.photoVideoErrorMsg?.isNotEmpty() == true) {
            Text(
                text = buildLegacyUiState?.photoVideoErrorMsg ?: "",
                color = MaterialTheme.colorScheme.error,
                fontFamily = WorkSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 15.dp, top = 10.dp)
            )
        }
        if (buildLegacyUiState?.selectLegacyType == stringResource(R.string.tribe) ||
            buildLegacyUiState?.selectLegacyType == stringResource(R.string.innercircle)
        ) {
            TribeListComponent(uiState, buildLegacyUiState ?: BuildLegacyDataState())

            /*if (buildLegacyUiState?.tribeAndInnerCircleErrorMsg?.isNotEmpty() == true) {
                Text(
                    text = buildLegacyUiState?.tribeAndInnerCircleErrorMsg?:"",
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp)
                )
            }*/
            //   if(buildLegacyUiState?.tribeAndInnerCircleErrorMsg?.isNotEmpty() == true) {
            Text(
                text = buildLegacyUiState?.tribeAndInnerCircleErrorMsg ?: "",
                color = MaterialTheme.colorScheme.error,
                fontFamily = WorkSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 15.dp, top = 10.dp)
            )
            //  }
        }
        Spacer(modifier = Modifier.height(20.dp))
        CommunityGuidelinesText(
            onTermsAndConditionsClick = {

            },
            onCheckChange = { checked ->
                event(BuildLegacyUiEvent.OnCheckedChange(checked))
            },
            errorMessage = buildLegacyUiState?.isCommunityGuidelinesErrorMsg
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppButtonComponent(
            onClick = {
                event(BuildLegacyUiEvent.PostClick)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.post),
        )
        Spacer(modifier = Modifier.height(20.dp))

    }
    if (buildLegacyUiState?.showPhotoVideoDialog == true) {
        VideoSelection(event, buildLegacyUiState ?: BuildLegacyDataState())
    }
}
@Composable
fun TribeListComponent(uiState: BuildLegacyUiState, buildLegacyUiState: BuildLegacyDataState) {
    val tribeInnerCircleList = uiState.tribeInnerCircleListFlow.collectAsLazyPagingItems()
    val selectedItems = buildLegacyUiState.tribeAndInnerCircleList
    uiState.event(BuildLegacyUiEvent.ListCount(tribeInnerCircleList.itemCount))
    Spacer(modifier = Modifier.height(18.dp))
    Text(
        text = if (buildLegacyUiState.selectLegacyType == stringResource(R.string.innercircle)) {
            stringResource(R.string.your_inner_circle_list)
        } else {
            stringResource(R.string.your_tribe_list)
        },
        fontWeight = W400,
        color = White,
        fontSize = 16.sp,
        fontFamily = WorkSans
    )
    Spacer(modifier = Modifier.height(8.dp))
    tribeInnerCircleList.loadState.refresh.apply {
        when (this) {
            is LoadState.Error -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TapHereRefreshContent(onClick = { tribeInnerCircleList.retry() })
                }
            }

            is LoadState.Loading -> {
                CustomLoader()
            }

            is LoadState.NotLoading -> {
                if (tribeInnerCircleList.itemCount == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (buildLegacyUiState.selectLegacyType == stringResource(R.string.innercircle)) "No InnerCircle List found" else "No Tribe List found",
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = White,
                            fontFamily = WorkSans,
                            fontWeight = W600
                        )
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(tribeInnerCircleList.itemCount) { index ->
                            tribeInnerCircleList[index]?.let { item ->
                                TribeListItem(
                                    item = item,
                                    isSelected = selectedItems.contains(item),
                                    onItemSelected = {
                                        val updatedList = if (selectedItems.contains(item)) {
                                            selectedItems - item // Deselect
                                        } else {
                                            selectedItems + item // Select
                                        }
                                        uiState.event(
                                            BuildLegacyUiEvent.OnTribeAndInnerCircleClick(
                                                updatedList
                                            )
                                        )
                                    },
                                    buildLegacyUiState
                                )
                            }
                        }
                        when (tribeInnerCircleList.loadState.append) {
                            is LoadState.Error -> {
                                item {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.something_went_wrong),
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                color = AppThemeColor,
                                                fontFamily = WorkSans,
                                            )
                                            Text(
                                                modifier = Modifier.clickable {
                                                    tribeInnerCircleList.retry()
                                                },
                                                text = stringResource(id = R.string.tap_here_to_refresh_it),
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                color = AppThemeColor,
                                                fontFamily = WorkSans,
                                            )
                                        }
                                    }
                                }
                            }

                            LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        CircularProgressIndicator(color = White)
                                    }
                                }
                            }

                            is LoadState.NotLoading -> Unit
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TribeListItem(
    item: LegacyPostResponse,
    isSelected: Boolean,
    onItemSelected: (LegacyPostResponse) -> Unit,
    buildLegacyUiState: BuildLegacyDataState
) {

    val errorColors = if (buildLegacyUiState.tribeAndInnerCircleErrorMsg?.isNotEmpty() == true) {
        MaterialTheme.colorScheme.error
    } else {
        Color.Transparent
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemSelected(item) }
            .height(150.dp)
            .background(Gray3E)
            .border(
                width = 1.dp,
                color = if (isSelected) White else errorColors,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        AsyncImage(
            model = item.profileImage,
            placeholder = painterResource(id = R.drawable.ic_app_icon),
            error = painterResource(id = R.drawable.ic_app_icon),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)

        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.name ?: "",
            color = White,
            fontSize = 14.sp,
            fontFamily = WorkSans,
            fontWeight = W400,
        )
    }
}

@Composable
fun SelectPhotoVideoComponent(
    uiState: BuildLegacyUiState,
    buildLegacyUiState: BuildLegacyDataState?
) {
    Column {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            onClick = {

            },
            border = BorderStroke(
                1.dp,
                color = if (buildLegacyUiState?.photoVideoErrorMsg?.isNotEmpty() == true) MaterialTheme.colorScheme.error else White
            ),
            modifier = Modifier
                .size(122.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        uiState.event(BuildLegacyUiEvent.ShowPhotoVideo(true))
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = null,
                    modifier = Modifier

                )
            }
        }

    }
}
@Composable
fun SelectedMediaPreview(
    selectedImages: List<Media>,
    selectedVideos: List<Pair<Media, Bitmap>>,
    onRemoveImage: (Media) -> Unit,
    onRemoveVideo: (Media) -> Unit,
    uiState: BuildLegacyUiState,
    onReorder: (fromIndex: Int, toIndex: Int) -> Unit
) {
    var draggedIndex by remember { mutableIntStateOf(-1) }
    var currentPosition by remember { mutableIntStateOf(-1) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Combine media lists
    val combinedMedia = remember(selectedImages, selectedVideos) {
        selectedImages.map { it to null } + selectedVideos
    }

    // Item width including spacing (122.dp + 15.dp)
    val itemWidthPx = with(density) { 137.dp.toPx() }


    LaunchedEffect(draggedIndex, dragOffset) {
        if (draggedIndex == -1) return@LaunchedEffect

        // Calculate current position during drag
        currentPosition = (dragOffset / itemWidthPx + draggedIndex)
            .roundToInt()
            .coerceIn(0, combinedMedia.size - 1)

        val firstVisible = scrollState.firstVisibleItemIndex
        val lastVisible = firstVisible + scrollState.layoutInfo.visibleItemsInfo.size - 1

        // Auto-scroll left if needed
        if (currentPosition < firstVisible + 1 && firstVisible > 0) {
            scope.launch {
                scrollState.animateScrollBy(-itemWidthPx * 0.5f)
            }
        }
        // Auto-scroll right if needed
        else if (currentPosition > lastVisible - 1 && lastVisible < combinedMedia.size - 1) {
            scope.launch {
                scrollState.animateScrollBy(itemWidthPx * 0.5f)
            }
        }
    }

    LazyRow(
        state = scrollState,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(
            items = combinedMedia,
            key = { index, (media, _) -> media.filename ?: index.toString() }
        ) { index, (media, thumbnail) ->
            val isVideo = media.filename?.endsWith(".mp4") == true
            val isDragged = index == draggedIndex

            // Calculate offset for animation
            val offset by animateFloatAsState(
                targetValue = when {
                    isDragged -> dragOffset
                    draggedIndex != -1 && index == currentPosition -> 0f
                    draggedIndex != -1 && index in min(draggedIndex, currentPosition)..max(
                        draggedIndex,
                        currentPosition
                    ) -> {
                        if (index < draggedIndex && index >= currentPosition) itemWidthPx
                        else if (index > draggedIndex && index <= currentPosition) -itemWidthPx
                        else 0f
                    }

                    else -> 0f
                },
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow), label = ""
            )
            DraggableMediaItem(
                media = media,
                thumbnail = thumbnail,
                isVideo = isVideo,
                isDragged = isDragged,
                offset = offset,
                onDragStart = {
                    draggedIndex = index
                    currentPosition = index
                    dragOffset = 0f
                },
                onDragEnd = {
                    if (draggedIndex != -1 && currentPosition != draggedIndex) {
                        onReorder(draggedIndex, currentPosition)
                    }
                    draggedIndex = -1
                    currentPosition = -1
                    dragOffset = 0f
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    change.consume()
                    dragOffset += dragAmount.x
                },
                onRemove = {
                    if (isVideo) onRemoveVideo(media) else onRemoveImage(media)
                },
                uiState = uiState
            )
        }
    }
}

@Composable
fun DraggableMediaItem(
    media: Media,
    thumbnail: Bitmap?,
    isVideo: Boolean,
    isDragged: Boolean,
    offset: Float,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (PointerInputChange, Offset) -> Unit,
    onRemove: () -> Unit,
    uiState: BuildLegacyUiState
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(x = offset.roundToInt(), y = 0) }
            .zIndex(if (isDragged) 1f else 0f)
    ) {
        MediaItem(
            media = media,
            thumbnail = thumbnail,
            modifier = Modifier
                .size(122.dp)
                .clip(RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { onDragStart() },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() },
                        onDrag = { change, dragAmount ->
                            onDrag(change, dragAmount)
                        }
                    )
                }
                .scale(if (isDragged) 1.05f else 1f)
                .shadow(
                    elevation = if (isDragged) 8.dp else 0.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = if (isDragged) 2.dp else 0.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ),
            onRemove = onRemove,
            isVideo = isVideo,
            uiState = uiState
        )
    }
}

@Composable
fun MediaItem(
    media: Media,
    thumbnail: Bitmap?,
    modifier: Modifier,
    onRemove: () -> Unit,
    isVideo: Boolean,
    uiState: BuildLegacyUiState
) {

    Box(
        modifier = modifier
            .size(122.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        if (!isVideo || media.thumbnail.isNullOrEmpty()) {
            AsyncImage(
                model = if (isVideo) thumbnail else media.filename,
                contentDescription = if (isVideo) "Video thumbnail" else "Image",

                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                model = media.thumbnail,
                contentDescription = "Video thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (isVideo) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = "Play video",
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable {
                        uiState.event(
                            BuildLegacyUiEvent.VideoPreviewClick(media.filename ?: "")
                        )
                    }
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_app_icon),
            contentDescription = "Remove",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .noRippleClickable { onRemove() }
                .padding(8.dp)
                .size(20.dp)
        )
    }
}
@Composable
fun VideoSelection(event: (BuildLegacyUiEvent) -> Unit, buildLegacyUiState: BuildLegacyDataState) {
    val showDialog = buildLegacyUiState.showPhotoVideoDialog
    val context = LocalContext.current
    val lifecycleOwner = LocalActivity.current
    val videoPickerManager = remember {
        VideoPickerManager(
            context = context,
            activityResultRegistry = (lifecycleOwner as ComponentActivity).activityResultRegistry,
            onVideoPicked = { isPhoto, videoPath, bitmap ->
                if (isPhoto) {
                    event(BuildLegacyUiEvent.OnImgPick(videoPath))
                } else {
                    event(BuildLegacyUiEvent.OnVideoPick(videoPath))
                }
                event(BuildLegacyUiEvent.PhotoVideoStatus(isPhoto))
            },
            onError = {
                // Handle error
            },
            onLoaderStateChange = { showLoader ->
                event(BuildLegacyUiEvent.ShowVideoCompressionLoader(showLoader))
            }
        )
    }
    val photoPickerManager = remember {
        PhotoPickerManager(
            context = context,
            activityResultRegistry = (lifecycleOwner as ComponentActivity).activityResultRegistry,
            onPhotoPicked = { photoPath ->
                event(BuildLegacyUiEvent.OnImgPick(photoPath))
            },
            onError = {

            }
        )
    }
    if (showDialog) {
        CameraGalleryDialog(
            onDismissRequest = {
                event(BuildLegacyUiEvent.ShowPhotoVideo(false))
                photoPickerManager.capturePhoto()
            },
            galleryText = stringResource(R.string.choose_from_gallery),
            cameraText = stringResource(R.string.take_photo),
            videoTextShow = true,
            videoText = stringResource(R.string.record_video),
            onGalleryClick = {
                event(BuildLegacyUiEvent.ShowPhotoVideo(false))
                videoPickerManager.pickMediaFromGallery()
            },
            onCameraClick = {
                event(BuildLegacyUiEvent.ShowPhotoVideo(false))
                photoPickerManager.capturePhoto()

            },
            videoRecordClick = {
                event(BuildLegacyUiEvent.ShowPhotoVideo(false))
                videoPickerManager.captureVideo()
            }

        )
        /*PhotoChooserDialog(
            isVideoOptionAvailable = true,
            onTakePhotoClick = {
                event(AddProductUiEvent.OnOpenORDismissVideoDialog(false))
                photoPickerManager.capturePhoto()
            },
            onGalleryClick = {
                event(AddProductUiEvent.OnOpenORDismissVideoDialog(false))
                videoPickerManager.pickMediaFromGallery()
            },
            onRecordVideoClick = {
                event(AddProductUiEvent.OnOpenORDismissVideoDialog(false))
                videoPickerManager.captureVideo()
            },
            onDismissRequest = {
                event(AddProductUiEvent.OnOpenORDismissVideoDialog(false))
            }
        )*/
    }
}

@Composable
fun BuildLegacyInputField(
    event: (BuildLegacyUiEvent) -> Unit,
    buildLegacyUiState: BuildLegacyDataState?
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        DropdownField(
            list = listOf(
                stringResource(R.string.tribe),
                stringResource(R.string.innercircle),
                stringResource(R.string.village),
                stringResource(R.string.justme)
            ),
            expanded = expanded,
            selectedRole = buildLegacyUiState?.selectLegacyType ?: "",
            onRoleDropDownExpanded = {
                expanded = it
            },
            placeholder = "Select build legacy type",
            errorMessage = buildLegacyUiState?.selectLegacyErrorMsg,
            onUserRoleValue = { event(BuildLegacyUiEvent.SelectLegacyType(it)) },
        )
        AppInputTextField(
            value = buildLegacyUiState?.albumName ?: "",
            onValueChange = { event(BuildLegacyUiEvent.AlbumNameValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            isTrailingIconVisible = true,
            errorMessage = buildLegacyUiState?.albumNameErrorMsg,
            header = "Enter Album Name",
        )
        AppInputTextFieldMultipleLine(
            value = buildLegacyUiState?.legacy ?: "",
            errorMessage = buildLegacyUiState?.legacyErrorMsg,
            onValueChange = { event(BuildLegacyUiEvent.LegacyValueChange(it)) },
            header = "Share Your Legacy",
        )
    }
}

@Preview
@Composable
fun CreateTribeOrInnerCircleScreenPreview() {
    val uiState = BuildLegacyUiState()
    BuildLegacyScreenContent(uiState = uiState, event = uiState.event)
}






