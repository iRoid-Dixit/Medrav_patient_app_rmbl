package com.medrevpatient.mobile.app.ux.container.groupMember
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.dialog.ConfirmationDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.RedUnblock
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans


@ExperimentalMaterial3Api
@Composable
fun GroupMemberScreen(
    navController: NavController,
    viewModel: AddPeopleViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val groupMemberUiState by uiState.groupMemberDataFlow.collectAsStateWithLifecycle()
    val groupMemberList = uiState.groupMemberListFlow.collectAsLazyPagingItems()
    val isCurrentUserAdmin =
        groupMemberList.itemSnapshotList.any { it?.isAdmin == true && it.id == groupMemberUiState?.userId }
    val navBackStackEntry = navController.currentBackStackEntry
    val memberAdded =
        navBackStackEntry?.savedStateHandle?.get<String>(Constants.BundleKey.MEMBER_ADDED)
    uiState.event(GroupMemberUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.member),
                isBackVisible = true,
                onClick = {
                    uiState.event(GroupMemberUiEvent.BackClick)
                },
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                if (isCurrentUserAdmin) {
                    AppButtonComponent(
                        onClick = {
                            uiState.event(GroupMemberUiEvent.AddMemberButtonClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        text = stringResource(id = R.string.add_member)
                    )

                }

            }
        )
    ) {
        GroupMemberScreenContent(uiState, uiState.event, groupMemberUiState)
    }
    if (groupMemberUiState?.showLoader == true) {
        CustomLoader()
    }
    if (memberAdded == Constants.BundleKey.MEMBER_ADDED) {
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
        LaunchedEffect(lifecycleState) {
            when (lifecycleState) {
                Lifecycle.State.RESUMED -> {
                    uiState.event(GroupMemberUiEvent.GroupMemberAPICall)
                }

                else -> {}
            }
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun GroupMemberScreenContent(
    uiState: GroupMemberUiState,
    event: (GroupMemberUiEvent) -> Unit,
    groupMemberUiState: GroupMemberDataState?
) {
    val groupMemberList = uiState.groupMemberListFlow.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .clickable {

            }
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.size(15.dp))
        AppInputTextField(
            value = groupMemberUiState?.searchMember ?: "",
            onValueChange = { event(GroupMemberUiEvent.SearchMember(it)) },
            errorMessage = "",
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            header = stringResource(R.string.search_member),
            leadingIcon = R.drawable.ic_app_icon,
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Total member - ${groupMemberList.itemCount}",
            modifier = Modifier.padding(start = 10.dp),
            color = White,
            fontFamily = WorkSans,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        )
        GroupMemberComponent(uiState = uiState, event, groupMemberList, groupMemberUiState)
    }

}

@Composable
private fun GroupMemberComponent(
    uiState: GroupMemberUiState,
    event: (GroupMemberUiEvent) -> Unit,
    groupMemberList: LazyPagingItems<SearchPeopleResponse>,
    groupMemberUiState: GroupMemberDataState?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        val lazyColumnListState = rememberLazyListState()
        groupMemberList.loadState.refresh.apply {
            when (this) {
                is LoadState.Error -> {
                    TapHereRefreshContent()
                }

                is LoadState.Loading -> {
                    CustomLoader()
                }

                is LoadState.NotLoading -> {
                    if (groupMemberList.itemCount == 0) {
                        //  NoDataFoundContent("no member found")
                        if (groupMemberUiState?.searchMember?.isNotBlank() == true) {
                            NoDataFoundContent(stringResource(R.string.no_search_results_found))
                        } else {
                            NoDataFoundContent(stringResource(R.string.no_member_found))
                        }
                    } else {
                        LazyColumn(
                            state = lazyColumnListState,
                            modifier = Modifier.padding(vertical = 20.dp),


                            )
                        {
                            items(groupMemberList.itemCount) { index ->
                                val isCurrentUserAdmin =
                                    groupMemberList.itemSnapshotList.any { it?.isAdmin == true && it.id == groupMemberUiState?.userId }
                                groupMemberList[index]?.let { item ->
                                    ItemMemberPeople(
                                        item,
                                        uiState = uiState,
                                        isCurrentUserAdmin = isCurrentUserAdmin,


                                        )

                                }
                                if (index != groupMemberList.itemCount - 1) {
                                    Spacer(modifier = Modifier.size(10.dp))
                                    HorizontalDivider(thickness = 1.dp, color = MineShaft)
                                    Spacer(modifier = Modifier.size(10.dp))
                                }
                            }
                            when (groupMemberList.loadState.append) {
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
                                                    modifier = Modifier.clickable { groupMemberList.retry() },
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
    }
    if (groupMemberUiState?.removeMemberDialog == true) {
        ConfirmationDialog(
            onDismissRequest = {
                uiState.event(GroupMemberUiEvent.RemoveUserDialog(show = false, memberId = ""))
            },
            title = stringResource(R.string.remove_member),
            description = stringResource(R.string.are_you_sure_you_want_to_remove_this_member),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(id = R.string.remove),
            onPositiveClick = {
                event(GroupMemberUiEvent.RemoveMember)

            },
        )
    }
}

@Composable
fun ItemMemberPeople(
    member: SearchPeopleResponse,
    uiState: GroupMemberUiState,
    isCurrentUserAdmin: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = member.profileImage,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.ic_app_icon),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_app_icon),
            modifier = Modifier
                .size(45.dp)
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
                fontWeight = FontWeight.W600,
                fontSize = 16.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier

        ) {
            Text(
                text = when {
                    member.isAdmin == true -> stringResource(R.string.admin)
                    isCurrentUserAdmin -> stringResource(id = R.string.des_remove)
                    else -> ""
                },
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                fontFamily = WorkSans,
                color = when {
                    member.isAdmin == true -> White
                    isCurrentUserAdmin -> RedUnblock
                    else -> White
                },
                modifier = Modifier.clickable {
                    if (member.isAdmin == false && isCurrentUserAdmin) {
                        uiState.event(
                            GroupMemberUiEvent.RemoveUserDialog(
                                show = true,
                                memberId = member.id ?: ""
                            )
                        )

                    }
                }
            )

        }
    }
}

@Preview
@Composable
fun TribeListContentPreview() {
    val uiState = GroupMemberUiState()
    val groupMemberUiState by uiState.groupMemberDataFlow.collectAsStateWithLifecycle()
    GroupMemberScreenContent(
        uiState = uiState,
        event = uiState.event,
        groupMemberUiState = groupMemberUiState
    )

}






