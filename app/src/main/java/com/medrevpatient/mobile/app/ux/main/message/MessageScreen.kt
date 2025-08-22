package com.medrevpatient.mobile.app.ux.main.message
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.chat.MessageTabResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.GrayA0
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils

@ExperimentalMaterial3Api
@Composable
fun MessageScreen(
    navController: NavController = rememberNavController(),
    viewModel: MessageViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    AppScaffold(
        containerColor = AppThemeColor,
        topAppBar = {
            Column(
                modifier = Modifier
                    .background(AppThemeColor)

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .requiredHeight(54.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)

                ) {
                    Text(
                        text = "Message",
                        fontSize = 20.sp,
                        fontFamily = WorkSans,
                        color = White,
                        fontWeight = FontWeight.W500,

                        )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            uiState.event(MessageUiEvent.NavigateToAddGroupMember)
                        }
                    )
                }
            }

        },
        navBarData = null
    ) {
        MessageScreenContent(uiState = uiState)
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.event(MessageUiEvent.MessageListAPICall)
            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageScreenContent(
    uiState: MessageUiState,
) {
    val messageUiDataState by uiState.messageUiDataState.collectAsStateWithLifecycle()
    val messageList = uiState.messageList.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            messageList.refresh()
            state.endRefresh()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(state.nestedScrollConnection)
        ) {
            messageList.loadState.refresh.apply {
                when (this) {
                    is LoadState.Error -> {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            TapHereRefreshContent(onClick = { messageList.retry() })
                        }
                    }

                    is LoadState.Loading -> {
                        CustomLoader()
                    }

                    is LoadState.NotLoading -> {
                        if (messageList.itemCount == 0) {
                            if (messageUiDataState?.isAPIStatus == true) {
                                NoDataFoundContent(text = "No chats available")

                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 15.dp),
                            ) {
                                items(messageList.itemCount) { index ->
                                    messageList[index]?.let { item ->
                                        MessageItem(item, navigateToChatScreen = {
                                            uiState.event(
                                                MessageUiEvent.NavigateToChatScreen(
                                                    messageTabResponse = item
                                                )
                                            )

                                        })
                                    }
                                }
                                when (messageList.loadState.append) {
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
                                                            messageList.retry()
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
fun MessageItem(item: MessageTabResponse, navigateToChatScreen: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), // Add space for red dot
            shape = RoundedCornerShape(12.dp),
            onClick = {
                navigateToChatScreen()
            },
            border = BorderStroke(1.dp, color = White),
            colors = CardDefaults.cardColors(containerColor = Black)
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = if (item.type == 1) item.receiverProfile ?: "" else item.groupImage
                        ?: "",
                    contentDescription = stringResource(id = R.string.profile_image),
                    placeholder = painterResource(id = R.drawable.ic_app_icon),
                    error = painterResource(id = R.drawable.ic_app_icon),
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(15.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (item.type == 1) item.receiverName ?: "" else item.groupName
                                ?: "",
                            fontSize = 16.sp,
                            fontWeight = W600,
                            color = White,
                            lineHeight = 18.sp,
                            fontFamily = WorkSans,
                        )

                        Text(
                            text = if (item.lastMessageTime != null) AppUtils.formatTimestamp(item.lastMessageTime) else "",
                            fontSize = 14.sp,
                            fontWeight = W400,
                            color = GrayA0,
                            lineHeight = 18.sp,
                            fontFamily = WorkSans,
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.lastMessage ?: "",
                        fontSize = 14.sp,
                        fontWeight = W400,
                        color = GrayA0,
                        fontFamily = WorkSans,
                    )
                }
            }
        }

        if ((item.unreadMessageCount ?: 0) > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (2).dp, y = 5.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.Red)

            )
        }
    }
}
@Preview
@Composable
private fun Preview() {
    val uiState = MessageUiState()
    Surface {
        MessageScreenContent(uiState)
    }
}