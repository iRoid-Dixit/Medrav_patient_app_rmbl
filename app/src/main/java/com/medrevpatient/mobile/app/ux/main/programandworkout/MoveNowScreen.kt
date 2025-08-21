package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses
import com.medrevpatient.mobile.app.ui.FullSizeCircularLoader
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import com.medrevpatient.mobile.app.ux.main.component.OnDemandClassesItem
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault.textStyle
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.CenterAlignContentWrapper

@Preview
@Composable
private fun MoveNowPreview(modifier: Modifier = Modifier) {
    MoveNowScreen(
        hiltViewModel()
    )
}

@Composable
fun MoveNowScreen(
    viewModel: MoveNowViewModel,
    modifier: Modifier = Modifier
) {

    val onDemandClasses = viewModel.demandClassesPagingSource.collectAsLazyPagingItems()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = "Move Now",
                onBackPress = { viewModel.popBackStack() }
            )
        },
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) { innerPadding ->

        MoveNowComponent(
            pagingItems = onDemandClasses,
            event = viewModel::event,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )

    }
}


@Composable
fun MoveNowComponent(
    pagingItems: LazyPagingItems<OnDemandClasses>,
    modifier: Modifier = Modifier,
    isOnDemandClasses: Boolean = false,
    event: (MoveNowUiEvent) -> Unit,
) {

    val ctx = LocalContext.current

    PagingResultHandler(pagingItems) { pagingState ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            isOnDemandClasses.not().ifTrue {
                /* todo Remove this : - item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                    Header()}*/
                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    CenterAlignContentWrapper(
                        title = "ON DEMAND CLASSES",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            items(pagingItems.itemCount) { index ->
                val item = pagingItems[index] ?: return@items

                OnDemandClassesItem(
                    title = item.videoTitle,
                    time = item.duration,
                    level = item.levelDescription,
                    url = item.thumbnail,
                ) {
                    event(MoveNowUiEvent.StartPlayer(onDemandClasses = item, ctx = ctx))
                }
            }

            when (val appendState = pagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        }
                    ) {
                        FullSizeCircularLoader(modifier = Modifier.size(56.dp))
                    }
                }

                is LoadState.Error -> {
                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        }
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = textStyle.toSpanStyle()) {
                                    append(
                                        appendState.error.localizedMessage
                                            ?: "Something went wrong!"
                                    )
                                }
                                withStyle(
                                    style = textStyle.toSpanStyle()
                                        .copy(fontWeight = FontWeight.SemiBold)
                                ) {
                                    append("\nRetry")
                                }
                            },
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    pagingItems.retry()
                                }
                        )
                    }
                }

                is LoadState.NotLoading -> {}
            }
        }
    }
}

@Preview
@Composable
private fun Header(
    modifier: Modifier = Modifier
) {

    HStack(
        spaceBy = 8.dp,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .height(98.dp)
            .clip(RoundedCornerShape(25))
            .background(
                brush = Brush.horizontalGradient(listOf(Color.White, aliceBlue))
            )
    ) {
        VStack(
            spaceBy = 4.dp,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            Text(
                text = "Short on time?",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Press play and complete a quick guided workout with your coach!",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.W300,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        AsyncImage(
            model = "",
            contentDescription = null,
            placeholder = painterResource(R.drawable.img_portrait_placeholder_transparent),
            error = painterResource(R.drawable.img_portrait_placeholder_transparent),
            modifier = Modifier.weight(0.4f)
        )
    }
}





