package com.medrevpatient.mobile.app.ux.container.block

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.medrevpatient.mobile.app.model.domain.response.block.BlockUserResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.dialog.ConfirmationDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black50
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans


@ExperimentalMaterial3Api
@Composable
fun BLockListScreen(
    navController: NavController,
    viewModel: BlockListViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val blockUiState by uiState.blockListDataFlow.collectAsStateWithLifecycle()
    uiState.event(BlockListUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.block_list),
                isBackVisible = true,
                onClick = {
                    uiState.event(BlockListUiEvent.BackClick)
                },
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
            }
        )
    ) {
        BlockListScreenContent(uiState, uiState.event, blockUiState)
    }
    if (blockUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlockListScreenContent(
    uiState: BlockListUiState,
    event: (BlockListUiEvent) -> Unit,
    blockUiState: BlockListDataState?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val blockList = uiState.blockListFlow.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            blockList.refresh() // Explicitly refresh the LazyPagingItems
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
            blockList.loadState.refresh.apply {
                when (this) {
                    is LoadState.Error -> {
                        TapHereRefreshContent()
                    }

                    is LoadState.Loading -> {
                        CustomLoader()
                    }

                    is LoadState.NotLoading -> {
                        if (blockList.itemCount == 0) {
                            NoDataFoundContent(stringResource(R.string.no_blocked_users_found))
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier
                                    .padding(top = 30.dp)

                            )
                            {
                                items(blockList.itemCount) { index ->
                                    blockList[index]?.let { item ->
                                        ItemBlockMembers(item, uiState = uiState)
                                        if (index != blockList.itemCount - 1) HorizontalDivider(
                                            thickness = 0.5.dp,
                                            color = Black50,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }
                                when (blockList.loadState.append) {
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
                                                        modifier = Modifier.clickable { blockList.retry() },
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
    if (blockUiState?.showUnblockDialog == true) {
        ConfirmationDialog(
            onDismissRequest = { event(BlockListUiEvent.UnblockUserDialog(false, userId = "")) },
            title = stringResource(R.string.unblock_member),
            description = stringResource(R.string.are_you_sure_you_want_to_unblock_this_member),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(id = R.string.unblock),
            onPositiveClick = {
                event(BlockListUiEvent.UnblockUser)

            },
        )

    }
}

@Composable
fun ItemBlockMembers(member: BlockUserResponse, uiState: BlockListUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = member.profileImage,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.ic_app_icon),
            error = painterResource(id = R.drawable.ic_app_icon),
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(
            text = member.name ?: "",
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
            color = White,
            fontFamily = WorkSans,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        )

        Row(modifier = Modifier
            .background(White, RoundedCornerShape(8.dp))
            .clickable {
                uiState.event(
                    BlockListUiEvent.UnblockUserDialog(
                        show = true,
                        userId = member.id ?: ""
                    )
                )
            }) {
            Text(
                text = stringResource(R.string.unblock),
                color = Black,
                fontFamily = WorkSans,
                fontWeight = FontWeight.W500,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview
@Composable
fun TribeListContentPreview() {
    val uiState = BlockListUiState()
    ItemBlockMembers(uiState = uiState, member = BlockUserResponse(name = "John   hello world"))

}






