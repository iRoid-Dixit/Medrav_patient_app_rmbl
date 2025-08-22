package com.medrevpatient.mobile.app.ux.container.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.notification.NotificationResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.GrayA0
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils


@ExperimentalMaterial3Api
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val blockUiState by uiState.notificationDataFlow.collectAsStateWithLifecycle()
    uiState.event(NotificationUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.notifications),
                isBackVisible = true,
                onClick = {
                    uiState.event(NotificationUiEvent.BackClick)
                },
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
            }
        )
    ) {
        NotificationScreenContent(uiState)
    }
    if (blockUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationScreenContent(uiState: NotificationUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val notificationList = uiState.notificationList.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            notificationList.refresh() // Explicitly refresh the LazyPagingItems
            state.endRefresh()
        }
    }
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(state.nestedScrollConnection)
        ) {
            notificationList.loadState.refresh.apply {
                when (this) {
                    is LoadState.Error -> {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            TapHereRefreshContent(onClick = { notificationList.retry() })
                        }
                    }

                    is LoadState.Loading -> {
                        CustomLoader()
                    }

                    is LoadState.NotLoading -> {
                        if (notificationList.itemCount == 0) {
                            NoDataFoundContent(text = stringResource(R.string.there_is_no_notification_on_your_app))
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 15.dp),
                            ) {
                                items(notificationList.itemCount) { index ->
                                    notificationList[index]?.let { item ->
                                        NotificationItem(item)
                                    }
                                }
                                when (notificationList.loadState.append) {
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
                                                            notificationList.retry()
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
fun NotificationItem(
    item: NotificationResponse,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color = White),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // Profile Image
                AsyncImage(
                    model = item.userDetails?.profileImage,
                    contentDescription = stringResource(id = R.string.profile_image),
                    placeholder = painterResource(id = R.drawable.ic_app_icon),
                    error = painterResource(id = R.drawable.ic_app_icon),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = item.userDetails?.name ?: "",
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = W400,
                        color = Color.White,
                        fontFamily = WorkSans
                    )
                    Text(
                        text = item.body ?: "",
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = W400,
                        color = Color.White,
                        fontFamily = WorkSans
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppUtils.formatTimestamp(item.createdAt),
                    fontSize = 14.sp,
                    fontFamily = WorkSans,
                    fontWeight = W400,
                    color = GrayA0
                )
            }
        }
    }
}

@Preview
@Composable
fun NotificationScreenContentPreview() {
    val uiState = NotificationUiState()
    NotificationScreenContent(uiState)
}






