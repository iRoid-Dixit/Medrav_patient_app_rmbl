package com.medrevpatient.mobile.app.ux.container.tribeList

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.tribe.MemberResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.CustomDropdownMenu
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.dialog.ConfirmationDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans


@ExperimentalMaterial3Api
@Composable
fun TribeListScreen(
    navController: NavController,
    viewModel: TribeListViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val tribeListUiState by uiState.tribeListDataFlow.collectAsStateWithLifecycle()
    uiState.event(TribeListUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                onClick = { navController.popBackStack() },
                titleText = "BMI & Health Check"
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                AppButtonComponent(
                    onClick = {
                        uiState.event(TribeListUiEvent.OnAddMemberClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    text = stringResource(R.string.add_member)
                )
            }
        )
    ) {
        TribeListScreenContent(uiState, uiState.event, tribeListUiState)
    }
    if (tribeListUiState?.showLoader == true) {
        CustomLoader()
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.event(TribeListUiEvent.OnGetTribeList)

            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TribeListScreenContent(
    uiState: TribeListUiState,
    event: (TribeListUiEvent) -> Unit,
    tribeListUiState: TribeListDataState?
) {
    val tribeMemberList = uiState.tribeMemberListFlow.collectAsLazyPagingItems()
    val lazyColumnListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            tribeMemberList.refresh()
            state.endRefresh()
        }
    }
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(state.nestedScrollConnection)
        ) {
            tribeMemberList.loadState.refresh.apply {
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
                                        tribeMemberList.retry()
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
                        if (tribeMemberList.itemCount == 0) {
                            if (tribeListUiState?.apiStatus == true) {
                                NoDataFoundContent(stringResource(R.string.no_members_found))

                            }
                        } else {
                            LazyColumn(
                                state = lazyColumnListState,
                                modifier = Modifier
                                    .padding(vertical = 20.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(25.dp)
                            )
                            {
                                items(tribeMemberList.itemCount) { index ->
                                    tribeMemberList[index]?.let { item ->
                                        ItemTribeMembers(item, event, onNavigateToChatScreen = {
                                            uiState.event(
                                                TribeListUiEvent.NavigateToChatScreen(item)
                                            )
                                        })
                                    }
                                }
                                when (tribeMemberList.loadState.append) {
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
                                                        modifier = Modifier.clickable { tribeMemberList.retry() },
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
    if (tribeListUiState?.showDialog == true) {
        ConfirmationDialog(
            onDismissRequest = { event(TribeListUiEvent.BlockUserDialog(false, userId = "")) },
            title = "Block Member",
            description = "Are you sure you want to block this member?",
            negativeText = stringResource(id = R.string.cancel),
            positiveText = "Block",
            onPositiveClick = {
                event(TribeListUiEvent.BlockUser)


            },
        )

    }
    if (tribeListUiState?.showRemoveDialog == true) {
        ConfirmationDialog(
            onDismissRequest = { event(TribeListUiEvent.RemoveUserDialog(false, userId = "")) },
            title = "Remove Member",
            description = "Are you sure you want to remove this member?",
            negativeText = stringResource(id = R.string.cancel),
            positiveText = "Remove",
            onPositiveClick = {
                event(TribeListUiEvent.RemoveUser)
            },
        )

    }
}

@Composable
fun ItemTribeMembers(
    member: MemberResponse,
    event: (TribeListUiEvent) -> Unit,
    onNavigateToChatScreen: () -> Unit = {}
) {

    val menuItems = listOf(
        stringResource(R.string.block) to {
            event(TribeListUiEvent.BlockUserDialog(true, userId = member.id ?: ""))

        },
        stringResource(R.string.remove) to {

            event(TribeListUiEvent.RemoveUserDialog(true, userId = member.id ?: ""))
        }
    )
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onNavigateToChatScreen()
            }

    ) {
        AsyncImage(
            model = member.profileImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_app_icon),
            error = painterResource(id = R.drawable.ic_app_icon),
            modifier = Modifier
                .size(63.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
                .weight(1f),
        ) {
            Text(
                text = member.name ?: "",
                modifier = Modifier.padding(start = 10.dp),
                color = White,
                fontFamily = WorkSans,
                fontWeight = FontWeight.W400,
                fontSize = 16.sp
            )
            Text(
                text = if (member.isOnline == true) stringResource(R.string.activate_now) else stringResource(
                    R.string.offline
                ),
                modifier = Modifier.padding(start = 10.dp),
                color = White,
                fontFamily = WorkSans,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp
            )
        }
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .align(Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_app_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable { expanded = true }

            )
            CustomDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                menuItems = menuItems,
                offsetY = 15.dp
            )
        }
    }

}








