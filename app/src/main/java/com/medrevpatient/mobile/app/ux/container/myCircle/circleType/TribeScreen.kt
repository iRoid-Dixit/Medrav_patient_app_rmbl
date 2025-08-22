package com.medrevpatient.mobile.app.ux.container.myCircle.circleType

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.tribe.TribeResponse
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ux.container.myCircle.MyCircleUiEvent
import com.medrevpatient.mobile.app.ux.container.myCircle.MyCircleUiState

@ExperimentalMaterial3Api
@Composable
fun TribeScreen(
    uiState: MyCircleUiState
) {
    val context = LocalContext.current
    uiState.event(MyCircleUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = null,
        navBarData = null
    ) {
        MyCircleScreenContent(uiState, uiState.event)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyCircleScreenContent(
    uiState: MyCircleUiState,
    event: (MyCircleUiEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val tribeList = uiState.tribeListFlow.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            tribeList.refresh()
            state.endRefresh()
        }
    }
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize()
            .background(Black),
    ) {

        val lazyColumnListState = rememberLazyListState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(state.nestedScrollConnection)
        ) {
            tribeList.loadState.refresh.apply {
                when (this) {
                    is LoadState.Error -> {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.tribe),
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    color = Black,
                                    fontFamily = WorkSans,
                                )
                                Text(
                                    modifier = Modifier.clickable {
                                        tribeList.retry()
                                    },
                                    text = stringResource(id = R.string.tap_here_to_refresh_it),
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    color = Black,
                                    fontFamily = WorkSans,
                                )
                            }
                        }
                    }

                    is LoadState.Loading -> {
                        CustomLoader()
                    }

                    is LoadState.NotLoading -> {
                        if (tribeList.itemCount == 0) {
                            NoDataFoundContent(text = "No Tribe List found")
                        } else {
                            LazyColumn(
                                state = lazyColumnListState,
                                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp)
                            )
                            {
                                items(tribeList.itemCount) { index ->
                                    tribeList[index]?.let { item ->
                                        ItemTribe(item, uiState = uiState)
                                    }
                                }
                                when (tribeList.loadState.append) {
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
                                                        fontSize = 15.sp,
                                                        maxLines = 1,
                                                        color = Black,
                                                        fontFamily = WorkSans
                                                    )

                                                    Text(
                                                        modifier = Modifier.clickable { tribeList.retry() },
                                                        text = stringResource(id = R.string.tap_here_to_refresh_it),
                                                        fontSize = 15.sp,
                                                        maxLines = 1,
                                                        color = Black,
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
            PullToRefreshContainer(
                state = state,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = AppThemeColor,
                contentColor = White
            )
        }
    }
}

@Composable
fun ItemTribe(tribeResponse: TribeResponse, uiState: MyCircleUiState) {
    val menuItems = listOf(
        stringResource(R.string.tribe_info) to { },
        stringResource(R.string.leave_tribe) to { },
        stringResource(R.string.block) to { }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp)
            .clickable {
                uiState.event(
                    MyCircleUiEvent.OnMemberClick(
                        tribeId = tribeResponse.id.toString(),
                        tribeName = tribeResponse.name ?: ""
                    )
                )
            }
    ) {
        AsyncImage(
            model = tribeResponse.image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_app_icon),
            error = painterResource(id = R.drawable.ic_app_icon),
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
                .weight(1f),
        ) {
            Text(
                text = tribeResponse.name ?: "",
                modifier = Modifier.padding(start = 10.dp),
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = WorkSans,
                fontWeight = FontWeight.W400,
                fontSize = 19.sp
            )
            Text(
                text = (tribeResponse.totalMembers?.toString()
                    ?: "0") + stringResource(R.string.members),
                modifier = Modifier.padding(start = 10.dp),
                color = White,
                fontFamily = WorkSans,
                fontWeight = FontWeight.W300,
                fontSize = 14.sp
            )
        }


    }
}

@Preview
@Composable
fun TribeScreenContentPreview(modifier: Modifier = Modifier) {
    val uiState = MyCircleUiState()
    MyCircleScreenContent(uiState = uiState, event = uiState.event)
    //ItemTribe(TribeResponse())
}






