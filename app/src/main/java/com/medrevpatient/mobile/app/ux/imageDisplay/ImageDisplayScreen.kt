package com.medrevpatient.mobile.app.ux.imageDisplay

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.Media
import com.medrevpatient.mobile.app.ui.compose.common.HorizontalPagerIndicator
import com.medrevpatient.mobile.app.ui.compose.common.Zoomable
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageDisplayScreen(
    mediaListData: String,
    onBackClick: () -> Unit = {},
) {

    val mediaList: List<Media> =
        Gson().fromJson(mediaListData, object : TypeToken<List<Media>>() {}.type)
    val pagerState = rememberPagerState(pageCount = { mediaList.size })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeColor),
    ) {
        Zoomable(
            modifier = Modifier.fillMaxSize(),
            minZoom = 0.8f,
            maxZoom = 3f
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                // .height(300.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val mediaItem = mediaList[page]

                    // Wrap the image and icon in a Box to overlay the video icon
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = if (mediaItem.type == 1) mediaItem.filename else mediaItem.thumbnail,
                            contentDescription = stringResource(R.string.post_image),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {

                                }

                        )
                        if (mediaItem.type == 2) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_icon),
                                contentDescription = stringResource(R.string.video),
                                colorFilter = ColorFilter.tint(White),
                                modifier = Modifier
                                    .align(Alignment.Center)


                            )
                        }
                    }
                }
                if (mediaList.size > 1) {
                    HorizontalPagerIndicator(
                        count = mediaList.size,
                        selectedIndex = pagerState.currentPage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    )
                }
            }
        }
        TopBarContent(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun TopBarContent(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .requiredHeight(70.dp)
            .fillMaxWidth()
            .padding(top = 45.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .clickable { onBackClick() },
                tint = White
            )
        }
    }
}

