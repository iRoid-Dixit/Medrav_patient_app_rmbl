package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.response.PostImages
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RoundedCarouselImageWithDescription(
    modifier: Modifier = Modifier,
    isImageVisible: Boolean = true,
    isOnlyCaptionPost: Boolean = false,
    isBackgroundGradient: Boolean = false,
    clipPercent: Int = 18,
    images: List<PostImages>? = null,
    captionContent: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(clipPercent))
            .background(
                brush = if (isBackgroundGradient) Brush.linearGradient(
                    listOf(white, ColorSwansDown)
                ) else Brush.linearGradient(listOf(black25, black25))
            ),
        contentAlignment = Alignment.Center
    ) {
        VStack(
            0.dp,
        ) {
            if (isImageVisible) {
                CarousalEffectCommunity(
                    autoScrollDuration = 0L,
                    images = images ?: arrayListOf(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else if (isOnlyCaptionPost) {
                Text(
                    text = captionContent ?: "",
                    fontWeight = FontWeight.W300,
                    fontSize = 10.sp,
                    fontFamily = outFit,
                    color = MineShaft,
                    maxLines = 12,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(10.dp)
                )
            }

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.CenterStart
            ) {
                content()
            }
        }
    }
}

@Composable
fun CarousalEffectCommunity(
    images: List<PostImages>, // List of image URLs
    autoScrollDuration: Long = 3000L, // Auto-scroll interval
    clipPercent: Int = 12,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { images.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    Card(
        shape = RoundedCornerShape(clipPercent),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(clipPercent))
            .background(Color.Gray) // Placeholder background
    ) {
        Box {
            // Scrollable images inside the Card
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = images[page].url,
                    placeholder = painterResource(R.drawable.img_portrait_placeholder),
                    error = painterResource(R.drawable.img_portrait_placeholder),
                    contentDescription = "Carousel Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Pagination Dots
            if (images.size > 1) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(white.copy(alpha = 0.2f))
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) white else white.copy(alpha = 0.2f)
                        val width = if (pagerState.currentPage == iteration) 16.dp else 9.dp
                        Box(
                            modifier = Modifier
                                .padding(vertical = 3.dp, horizontal = 3.dp)
                                .clip(RoundedCornerShape(50))
                                .size(width = width, height = 6.dp)
                                .background(color)
                        )
                    }
                }
            }
        }
    }

    // Auto-scroll effect for images
    if (!isDragged && autoScrollDuration > 0L) {
        LaunchedEffect(key1 = pagerState.currentPage) {
            delay(autoScrollDuration)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            coroutineScope.launch { pagerState.animateScrollToPage(nextPage) }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
private fun RoundedImageWithRowDescriptionPreview(
    modifier: Modifier = Modifier
) {
    val list = listOf(
        "https://imgs.search.brave.com/qj5f3cqagXI9p-P9b6ku6fXJBka_TAyN5oePgetSZaQ/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90My5m/dGNkbi5uZXQvanBn/LzA2LzEyLzY2Lzc2/LzM2MF9GXzYxMjY2/NzYyNl8yQlpJeVNj/QTlnVzVVQlcwV1pW/TUdZWmtvUDkwZXps/dC5qcGc",
        "https://imgs.search.brave.com/qj5f3cqagXI9p-P9b6ku6fXJBka_TAyN5oePgetSZaQ/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90My5m/dGNkbi5uZXQvanBn/LzA2LzEyLzY2Lzc2/LzM2MF9GXzYxMjY2/NzYyNl8yQlpJeVNj/QTlnVzVVQlcwV1pW/TUdZWmtvUDkwZXps/dC5qcGc",
        "https://imgs.search.brave.com/qj5f3cqagXI9p-P9b6ku6fXJBka_TAyN5oePgetSZaQ/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90My5m/dGNkbi5uZXQvanBn/LzA2LzEyLzY2Lzc2/LzM2MF9GXzYxMjY2/NzYyNl8yQlpJeVNj/QTlnVzVVQlcwV1pW/TUdZWmtvUDkwZXps/dC5qcGc"
    )
    RoundedCarouselImageWithDescription(
        modifier = Modifier.fillMaxWidth(),
        images = list,
    ) {
        Text(
            text = "Description",
        )
    }
}*/

/*@Composable
fun CarousalEffectCommunity(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    autoScrollDuration: Long = 0L,
    programs: List<CategoryItem>,
    clipPercent: Int = 18,
    onClick: () -> Unit
) {
    VStack(spaceBy = 0.dp, modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            modifier = Modifier,
        ) { page ->
            Box {
                Card(
                    shape = RoundedCornerShape(clipPercent),
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val pageOffset =
                                ((pagerState.currentPage - page) +
                                        pagerState.currentPageOffsetFraction).absoluteValue

                            val transformation =
                                lerp(
                                    start = 0.8f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )


                            alpha = lerp(
                                start = 1f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                            scaleY = transformation
                        }
                ) {

                    AsyncImage(
                        model = programs[pagerState.currentPage].imageUrl,
                        contentDescription = "popular programs",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(white.copy(alpha = 0.2f))
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) white else white.copy(alpha = 0.2f)
                        val width = if (pagerState.currentPage == iteration) 16.dp else 9.dp
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                                .clip(RoundedCornerShape(50))
                                .size(width = width, height = 6.dp)
                                .background(color)
                        )
                    }
                }
            }

        }
    }

    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    if (isDragged.not() && autoScrollDuration > 0L) {
        with(pagerState) {
            var currentPageKey by remember { mutableIntStateOf(0) }
            LaunchedEffect(key1 = currentPageKey) {
                launch {
                    delay(timeMillis = autoScrollDuration)
                    val nextPage = (currentPage + 1).mod(pageCount)
                    animateScrollToPage(page = nextPage)
                    currentPageKey = nextPage
                }
            }
        }
    }
}*/