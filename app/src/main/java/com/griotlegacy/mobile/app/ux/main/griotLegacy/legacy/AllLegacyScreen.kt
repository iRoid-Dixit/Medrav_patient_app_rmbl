package com.griotlegacy.mobile.app.ux.main.griotLegacy.legacy

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.griotlegacy.mobile.app.model.domain.response.container.legacyPost.Media
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.AppButtonComponent
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.MineShaft
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.White50
import com.griotlegacy.mobile.app.ui.theme.WorkSans
import com.griotlegacy.mobile.app.utils.AppUtils
import com.griotlegacy.mobile.app.utils.AppUtils.noRippleClickable
import com.griotlegacy.mobile.app.ux.main.griotLegacy.GriotLegacyUiEvent
import com.griotlegacy.mobile.app.ux.main.griotLegacy.GriotLegacyUiState

@Composable
fun AllLegacyScreen(uiState: GriotLegacyUiState) {
    AppScaffold {
        AllLegacyScreenContent(uiState)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllLegacyScreenContent(uiState: GriotLegacyUiState) {
    val allLegacyPostList = uiState.allLegacyPostListFlow.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            uiState.event(GriotLegacyUiEvent.PullToRefreshAPICall)
            state.endRefresh()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(state.nestedScrollConnection)
            .noRippleClickable {

            }
    ) {
        Column {
            // Button displayed outside the LazyColumn
            Spacer(modifier = Modifier.height(20.dp))
            AppButtonComponent(
                onClick = {
                    uiState.event(GriotLegacyUiEvent.BuildLegacyClick)
                },
                text = "Build your Legacy",
            )
            Spacer(modifier = Modifier.height(20.dp))

            allLegacyPostList.loadState.refresh.apply {
                when (this) {
                    is LoadState.Error -> {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            TapHereRefreshContent(onClick = { allLegacyPostList.retry() })
                        }
                    }

                    is LoadState.Loading -> {
                        CustomLoader()
                    }

                    is LoadState.NotLoading -> {
                        if (allLegacyPostList.itemCount == 0) {
                            NoDataFoundContent(text = "No data found")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                items(allLegacyPostList.itemCount) { index ->
                                    allLegacyPostList[index]?.let { item ->
                                        AllLegacyItem(
                                            item,
                                            onPostDetailsClick = {
                                                uiState.event(
                                                    GriotLegacyUiEvent.PostDetailsClick(
                                                        postId = item.id ?: ""
                                                    )
                                                )
                                            },
                                            uiState
                                        )
                                    }
                                    if (index != allLegacyPostList.itemCount - 1) {
                                        Spacer(modifier = Modifier.height(20.dp))
                                        HorizontalDivider(thickness = 1.dp, color = MineShaft)
                                        Spacer(modifier = Modifier.height(25.dp))
                                    }
                                }
                                when (allLegacyPostList.loadState.append) {
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
                                                            allLegacyPostList.retry()
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
        PullToRefreshContainer(
            state = state,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = AppThemeColor,
            contentColor = White
        )
    }
}
@Composable
fun AllLegacyItem(
    item: LegacyPostResponse?,
    onPostDetailsClick: () -> Unit = {},
    uiState: GriotLegacyUiState
) {
    Column(modifier = Modifier.clickable {}) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item?.albumName ?: "",
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.W500,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp)
                )
                Text(
                    text = AppUtils.formatDateTime(item?.createdAt.toString()),
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.W400,
                    lineHeight = 18.sp,
                    color = White50,
                    fontSize = 12.sp
                )
            }
            Image(painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = null,
                modifier = Modifier.clickable {
                    onPostDetailsClick()
                })
        }
        Log.d("TAG", "AllLegacyItem: ${item?.createdAt}")
        Spacer(modifier = Modifier.height(15.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(item?.media ?: emptyList()) { mediaItem ->
                LegacyPostVideoPhotoItem(
                    mediaItem,
                    videoPreviewClick = {
                        uiState.event(
                            GriotLegacyUiEvent.VideoPreviewClick(item?.media ?: emptyList()),
                        )
                    },
                    imageDisplayClick = {
                        uiState.event(GriotLegacyUiEvent.ImageDisplay(item?.media ?: emptyList()))
                    }
                )
            }
        }
    }
}

@Composable
fun LegacyPostVideoPhotoItem(
    item: Media,
    videoPreviewClick: () -> Unit = {},
    imageDisplayClick: () -> Unit = {}
) {
    val imageUrl = if (item.type == 1) item.filename else item.thumbnail
    val painter = rememberAsyncImagePainter(model = imageUrl)
    val painterState = painter.state

    Box(
        modifier = Modifier
            .height(210.dp)
            .width(160.dp)
            .clickable { }
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.Transparent),
            color = Color.Transparent,
            onClick = {},
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if (item.type == 1) {
                                imageDisplayClick()
                            } else {
                                videoPreviewClick()
                            }
                        }
                )

                if (painterState is AsyncImagePainter.State.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
        if (item.type == 2) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = "Video",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clickable {
                        videoPreviewClick()
                    }
            )
        }
    }
}

@Preview
@Composable
fun LegacyPostVideoPhotoItemPreview() {

    val uiState = GriotLegacyUiState()
    val item = LegacyPostResponse(albumName = "Album ")
    AllLegacyItem(item = item, onPostDetailsClick = {}, uiState)

}

