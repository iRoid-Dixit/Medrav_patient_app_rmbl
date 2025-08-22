package com.medrevpatient.mobile.app.ux.container.postDetails
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.medrevpatient.mobile.app.model.domain.response.container.comment.CommentResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.Media
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.CustomDropdownMenu
import com.medrevpatient.mobile.app.ui.compose.common.HorizontalPagerIndicator
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.dialog.ConfirmationDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils

@ExperimentalMaterial3Api
@Composable
fun PostDetailsScreenScreen(
    navController: NavController,
    viewModel: PostDetailsViewModel = hiltViewModel(),
    postId: String,
    screen: String,
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val changePasswordUiState by uiState.postDetailsDataFlow.collectAsStateWithLifecycle()
    val postDetailsData by uiState.postDetailsData.collectAsStateWithLifecycle()
    LaunchedEffect(postId) {
        if (uiState.postDetailsDataFlow.value?.postId != postId) {
            uiState.event(PostDetailsUiEvent.GetContext(context))
            uiState.event(PostDetailsUiEvent.PostId(postId))
            uiState.event(PostDetailsUiEvent.ScreenName(screen))

        }
    }
    BackHandler(onBack = {
        uiState.event(PostDetailsUiEvent.BackClick)
    })
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = postDetailsData?.albumName ?: "",
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.event(PostDetailsUiEvent.BackClick)
                },
            )
        },
        navBarData = null
    ) {
        PostDetailsScreenScreenContent(uiState, uiState.event)
    }
    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun PostDetailsScreenScreenContent(
    uiState: PostDetailsUiState,
    event: (PostDetailsUiEvent) -> Unit
) {
    val postDetailsUiState by uiState.postDetailsDataFlow.collectAsStateWithLifecycle()
    val postDetailsData by uiState.postDetailsData.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable { keyboardController?.hide() }
            .background(AppThemeColor)
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            PostDetailsView(postDetailsData, uiState, postDetailsUiState)
        }
        SendCommentView(event, postDetailsUiState)
    }
}

@Composable
fun SendCommentView(
    event: (PostDetailsUiEvent) -> Unit,
    postDetailsUiState: PostDetailsUsDataState?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AppInputTextField(
            value = postDetailsUiState?.message ?: "",
            onValueChange = { event(PostDetailsUiEvent.SendMessageValueChange(it)) },
            isTrailingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            header = stringResource(R.string.add_new_comment),
            trailingIcon = R.drawable.ic_app_icon,
            onTogglePasswordVisibility = {
                keyboardController?.let { event(PostDetailsUiEvent.SendMessage(it)) }
            }

        )
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsView(
    postDetailsData: LegacyPostResponse?,
    uiState: PostDetailsUiState,
    postDetailsUiState: PostDetailsUsDataState?
) {
    val commentList = uiState.commentList.collectAsLazyPagingItems()
    uiState.event(PostDetailsUiEvent.CommentCount(commentList.itemSnapshotList.size))
    val menuItems = if (postDetailsData?.userId == postDetailsUiState?.userId) {
        listOf(
            stringResource(R.string.edit_post) to {
                uiState.event(
                    PostDetailsUiEvent.EditPostClick(
                        postDetailsData ?: LegacyPostResponse()
                    )
                )
            },
            stringResource(R.string.delete_post) to {
                uiState.event(PostDetailsUiEvent.DeleteDialog(true))
            }
        )
    } else {
        listOf(
            stringResource(R.string.report_this_post) to {
                uiState.event(
                    PostDetailsUiEvent.ReportPost(
                        postDetailsData?.id ?: ""
                    )
                )

            },
            stringResource(R.string.report_user) to {
                uiState.event(
                    PostDetailsUiEvent.ReportUser(
                        postDetailsData?.userId ?: ""
                    )
                )

            }
        )
    }
    var expanded by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState(positionalThreshold = 120.dp)
    val checkedStateItem = if (postDetailsData?.ownLike == true) Pair(
        R.drawable.ic_app_icon,
        stringResource(R.string.checked)
    ) else Pair(R.drawable.ic_app_icon, stringResource(R.string.unchecked))
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true, key2 = commentList) {
            uiState.event(PostDetailsUiEvent.PullToRefreshAPICall)
            pullRefreshState.endRefresh()
        }
    }
    Box(
        modifier = Modifier
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = postDetailsData?.profileImage,
                        contentDescription = stringResource(R.string.profile_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(55.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = postDetailsData?.name ?: "",
                            fontWeight = FontWeight.W500,
                            fontFamily = WorkSans,
                            lineHeight = 18.sp,
                            color = White,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppUtils.formatDateTime(postDetailsData?.createdAt.toString()),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.W400,
                            fontFamily = WorkSans,
                            color = White50
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = stringResource(
                            R.string.edit
                        ),
                        modifier = Modifier.clickable {
                            expanded = true
                        }
                    )
                    CustomDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        menuItems = menuItems,
                        offsetY = 30.dp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = postDetailsData?.legacyText ?: "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 18.sp,
                    fontFamily = WorkSans,
                    textAlign = TextAlign.Start,
                    color = White
                )
                Spacer(modifier = Modifier.height(15.dp))
                ItemView(postDetailsData?.media ?: emptyList(),
                    videoPreviewClick = {
                        uiState.event(
                            PostDetailsUiEvent.VideoPreviewClick(
                                postDetailsData?.media ?: emptyList()
                            )
                        )
                    },
                    imageDisplayClick = {
                        uiState.event(
                            PostDetailsUiEvent.ImageDisplay(
                                postDetailsData?.media ?: emptyList()
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            postDetailsData?.ownLike = postDetailsData?.ownLike != true
                            uiState.event(
                                PostDetailsUiEvent.IsLikeDisLikeAPICall(
                                    postDetailsData?.id ?: ""
                                )
                            )
                            postDetailsData?.likeCount =
                                if (postDetailsData?.ownLike == true) postDetailsData?.likeCount?.plus(
                                    1
                                ) else postDetailsData?.likeCount?.minus(
                                    1
                                )
                        }
                ) {
                    Image(
                        painter = painterResource(id = checkedStateItem.first),
                        contentDescription = checkedStateItem.second,
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                postDetailsData?.ownLike = postDetailsData?.ownLike != true
                                postDetailsData?.likeCount?.let {
                                    uiState.event(
                                        PostDetailsUiEvent.IsLikeDisLikeAPICall(
                                            postDetailsData.id ?: ""
                                        )
                                    )
                                }
                                postDetailsData?.likeCount =
                                    if (postDetailsData?.ownLike == true) postDetailsData?.likeCount?.plus(
                                        1
                                    ) else postDetailsData?.likeCount?.minus(
                                        1
                                    )
                            },
                    )
                    Spacer(modifier = Modifier.padding(3.dp))
                    Text(
                        text = if (postDetailsData?.likeCount == null) "" else postDetailsData.likeCount.toString(),
                        fontSize = 14.sp,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W400,
                        color = White
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Row {
                    Text(
                        text = stringResource(R.string.comments),
                        fontSize = 16.sp,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W700,
                        color = White
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${commentList.itemSnapshotList.size} comments",
                        fontSize = 12.sp,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W400,
                        color = White
                    )
                }
            }
            when (commentList.loadState.refresh) {
                is LoadState.Error -> item {
                    TapHereRefreshContent(onClick = { commentList.retry() })
                }

                is LoadState.Loading -> { /* Optional: show initial loading */

                }

                is LoadState.NotLoading -> {
                    if (commentList.itemCount == 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.no_comment_yet),
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    color = White,
                                    fontFamily = WorkSans,
                                    fontWeight = W600
                                )
                            }
                        }
                    } else {
                        items(commentList.itemCount) { index ->
                            commentList[index]?.let { item ->
                                CommentItem(item = item)
                            }
                            if (index != commentList.itemCount - 1) {

                                HorizontalDivider(thickness = 1.dp, color = MineShaft)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
            when (commentList.loadState.append) {
                is LoadState.Error -> item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.something_went_wrong),
                            fontSize = 16.sp,
                            color = AppThemeColor,
                            fontFamily = WorkSans,
                        )
                        Text(
                            modifier = Modifier.clickable { commentList.retry() },
                            text = stringResource(id = R.string.tap_here_to_refresh_it),
                            fontSize = 16.sp,
                            color = AppThemeColor,
                            fontFamily = WorkSans,
                        )
                    }
                }

                LoadState.Loading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = White)
                    }
                }

                is LoadState.NotLoading -> Unit
            }
        }

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = AppThemeColor,
            contentColor = White
        )

    }
    if (postDetailsUiState?.showDialog == true) {
        ConfirmationDialog(
            onDismissRequest = { uiState.event(PostDetailsUiEvent.DeleteDialog(false)) },
            title = stringResource(id = R.string.delete_post),
            description = stringResource(R.string.are_you_sure_you_want_to_delete_this_post_once_deleted_it_cannot_be_recovered),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(R.string.delete),
            onPositiveClick = {
                uiState.event(PostDetailsUiEvent.DeleteLegacyPostClick)
            },
        )

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemView(
    media: List<Media>,
    videoPreviewClick: (String) -> Unit = {},
    imageDisplayClick: (String) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { media.size })

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.Transparent, RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val mediaItem = media[page]
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
                                if (mediaItem.type == 1) {
                                    imageDisplayClick(mediaItem.filename ?: "")
                                } else {
                                    videoPreviewClick(mediaItem.filename ?: "")
                                }

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
            if (media.size > 1) {
                HorizontalPagerIndicator(
                    count = media.size,
                    selectedIndex = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }

        }
    }
}


@Composable
fun CommentItem(item: CommentResponse) {
    Column(modifier = Modifier.padding(top = 15.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = item.profileImage,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = item.name ?: "",
                    fontWeight = FontWeight.W700,
                    fontFamily = WorkSans,
                    color = White,
                    lineHeight = 18.sp,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = AppUtils.getTimeAgo(item.createdAt ?: 0L),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = WorkSans,
                    lineHeight = 18.sp,
                    color = White
                )
            }
        }
        Spacer(Modifier.padding(2.dp))
        Text(
            text = item.comment ?: "",
            fontSize = 10.sp,
            fontWeight = W600,
            lineHeight = 18.sp,
            fontFamily = WorkSans,
            color = Color.Gray
        )
        Spacer(Modifier.padding(5.dp))
    }

}


@Preview
@Composable
fun AboutScreenContentPreview(modifier: Modifier = Modifier) {
    val uiState = PostDetailsUiState()
    PostDetailsScreenScreenContent(uiState = uiState, event = uiState.event)

}






