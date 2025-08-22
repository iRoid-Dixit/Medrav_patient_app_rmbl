package com.medrevpatient.mobile.app.ux.container.userProfile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.container.friendInfo.FriendInfoResponse
import com.medrevpatient.mobile.app.model.domain.response.container.friendInfo.Media
import com.medrevpatient.mobile.app.model.domain.response.container.friendInfo.Post
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.ChatTopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.CustomDropdownMenu
import com.medrevpatient.mobile.app.ui.compose.common.dialog.ConfirmationDialog
import com.medrevpatient.mobile.app.ui.compose.common.dialog.InviteFriendsDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray2F
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.ux.startup.auth.login.LoginUiEvent

@ExperimentalMaterial3Api
@Composable
fun FriendProfileScreen(
    navController: NavController,
    viewModel: FriendProfileViewModel = hiltViewModel(),
    userId: String,
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val friendInfoData = uiState.friendInfoList.collectAsStateWithLifecycle()
    val friendProfileUiState by uiState.friendProfileDataFlow.collectAsStateWithLifecycle()
    uiState.event(FriendProfileUiEvent.GetContext(context))
    LaunchedEffect(userId) {
        if (uiState.friendProfileDataFlow.value?.userId != userId) {
            uiState.event(FriendProfileUiEvent.GetUserId(userId))
        }
    }
    val menuItems = listOf(
        stringResource(R.string.block) to {
            uiState.event(FriendProfileUiEvent.BlockUserDialog(true))
        },
        )
    var expanded by remember { mutableStateOf(false) }
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            ChatTopBarComponent(
                isBackVisible = true,
                onClick = {
                    uiState.event(FriendProfileUiEvent.BackClick)
                },
                isTrailingIconVisible = true,
                trailingIcon = R.drawable.ic_app_icon,
                onTrailingIconClick = {
                    expanded = true
                },
                userProfile = friendInfoData.value?.profileImage ?: "",
                userName = friendInfoData.value?.name ?: ""
            )
            CustomDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                menuItems = menuItems,
                offsetY = 30.dp
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                if (friendProfileUiState?.isAPISuccess == true) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        AppButtonComponent(
                            onClick = {

                            },
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.sign_in),
                            isLoading = false,


                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.clickable {
                                uiState.event(FriendProfileUiEvent.OnSendInvitationDialog(true))
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_icon),
                                contentDescription = stringResource(R.string.share)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Send Invitation",
                                fontSize = 18.sp,
                                color = White,
                                fontFamily = WorkSans,
                                fontWeight = W400
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))

                    }

                }
            }
        )
    ) {
        FriendProfileScreenContent(uiState, friendInfoData, friendProfileUiState)
    }
    if (friendProfileUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun FriendProfileScreenContent(
    uiState: FriendProfileUiState,
    friendInfoData: State<FriendInfoResponse?>,
    friendProfileUiState: FriendProfileDataState?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyColumnListState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyColumnListState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 1 && totalItemsCount > 0
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && friendProfileUiState?.isLoading == false) {
            uiState.event(FriendProfileUiEvent.OnPostNextPage)
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
        if (friendProfileUiState?.noDataFound == true) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.currently_no_post),
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = White,
                    fontFamily = WorkSans,
                    fontWeight = W600
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            state = lazyColumnListState,
            modifier = Modifier
                .fillMaxWidth() // Ensure the LazyRow has a bounded width

        ) {
            items(friendInfoData.value?.posts ?: emptyList()) { item ->
                FriendProfileItem(item, uiState)
            }
            item {
                if (friendProfileUiState?.isLoading == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        CircularProgressIndicator(color = White)
                    }
                }
            }

        }
    }
    if (friendProfileUiState?.showDialog == true) {
        ConfirmationDialog(
            onDismissRequest = { uiState.event(FriendProfileUiEvent.BlockUserDialog(false)) },
            title = stringResource(R.string.block_member),
            description = stringResource(R.string.are_you_sure_you_want_to_block_this_member),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(id = R.string.block),
            onPositiveClick = {
                uiState.event(FriendProfileUiEvent.BlockUser)

            },
        )
    }
    if (friendProfileUiState?.showSendInvitationDialog == true) {
        InviteFriendsDialog(
            onDismiss = {
                uiState.event(FriendProfileUiEvent.OnSendInvitationDialog(false))
            },
            onSmsClick = {
                uiState.event(FriendProfileUiEvent.SmsClick)
            },
            onEmailClick = {
                uiState.event(FriendProfileUiEvent.EmailClick)
            }
        )
    }
}

@Composable
fun FriendProfileItem(item: Post, uiState: FriendProfileUiState) {
    Column(modifier = Modifier.clickable {}) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = item.albumName ?: "",
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.W500,
                    color = White,
                    lineHeight = 18.sp,
                    fontSize = 18.sp
                )
                Text(
                    text = AppUtils.formatDateTime(item.createdAt.toString()),
                    fontFamily = WorkSans,
                    fontWeight = W400,

                    lineHeight = 18.sp,
                    color = White50,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(Modifier.padding(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item.media.forEach { mediaItem ->
                FriendMediaItem(mediaItem, uiState, item.media)
            }
        }
    }
}

@Composable
fun FriendMediaItem(item: Media, uiState: FriendProfileUiState, media: List<Media>) {
    Row {
        var isLoading by remember { mutableStateOf(true) }
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
                onClick = {

                },
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = if (item.type == 1) item.filename else item.thumbnail,
                    contentDescription = null,
                    onState = {
                        isLoading = it is AsyncImagePainter.State.Loading
                    },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clickable {
                        if (item.type == 1) {
                            uiState.event(FriendProfileUiEvent.ImageDisplay(mediaList = media))
                        } else {
                            uiState.event(FriendProfileUiEvent.VideoPreviewClick(media))
                        }
                    }
                )
                if (isLoading) {
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
            if (item.type == 2) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = "Video",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clickable {
                            uiState.event(FriendProfileUiEvent.VideoPreviewClick(media))

                        }
                )
            }
        }

    }
}

@Preview
@Composable
fun FriendScreenContentPreview() {
    val uiState = FriendProfileUiState()
    val friendProfileUiState by uiState.friendProfileDataFlow.collectAsStateWithLifecycle()
    val friendInfoData = uiState.friendInfoList.collectAsStateWithLifecycle()
    FriendProfileScreenContent(uiState, friendInfoData, friendProfileUiState)
}





