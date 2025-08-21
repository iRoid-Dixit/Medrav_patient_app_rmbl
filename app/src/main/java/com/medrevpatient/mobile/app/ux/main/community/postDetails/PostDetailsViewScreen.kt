package com.medrevpatient.mobile.app.ux.main.community.postDetails

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.response.LocalPostImages
import com.medrevpatient.mobile.app.navigation.PopResultKeyValue
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.common.BasicBottomSheet
import com.medrevpatient.mobile.app.ui.common.BottomButtonComponent
import com.medrevpatient.mobile.app.ui.common.ExpandableText
import com.medrevpatient.mobile.app.ui.theme.ColorOsloGray
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.black
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ui.theme.white10
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.ux.main.community.CommentsSheet
import com.medrevpatient.mobile.app.ux.main.community.myPosts.EditPost
import com.medrevpatient.mobile.app.ux.main.component.CarousalEffectCommunity
import com.medrevpatient.mobile.app.ux.main.component.IconTextHStack
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault

@Composable
fun PostDetailsViewScreen(
    viewModel: PostDetailsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        containerColor = black,
        topBar = {
            TopBarCenterAlignTextBack(
                modifier = Modifier.statusBarsPadding(),
                title = AppUtils.formatTimestampForMyPosts(uiState.postData.createdAt ?: 0),
                leadingIcon = R.drawable.close,
                onBackPress = {
                    if (uiState.isAnythingChanged) viewModel.popBackStackWithResult(resultValues = listOf(PopResultKeyValue(RouteMaker.Keys.REFRESH, true))) else viewModel.popBackStack()
                },
                isTrailingIconVisible = true,
                tint = white,
                textColor = white,
                onTrailIconPress = {
                    viewModel.event(PostDetailsUiEvent.ShowMenuDialog(true))
                }
            )
        },
        bottomBar = {
            BottomViewContent(uiState = uiState, event = viewModel::event)
        }
    ) { innerPadding ->
        PostDetailsViewContent(
            modifier = Modifier
                .fillMaxSize(),
            paddingValues = innerPadding,
            uiState = uiState,
            event = viewModel::event,
            viewModel = viewModel
        )
        if (uiState.isLoading) DialogLoader()
    }

    BackHandler {
        if (uiState.isAnythingChanged) {
            viewModel.popBackStackWithResult(resultValues = listOf(PopResultKeyValue(RouteMaker.Keys.REFRESH, true)))
        } else {
            viewModel.popBackStack()
        }
    }
}

@Composable
fun PostDetailsViewContent(
    modifier: Modifier = Modifier, paddingValues: PaddingValues, uiState: PostDetailsUiState, event: (PostDetailsUiEvent) -> Unit,
    viewModel: PostDetailsViewModel
) {
    Box(modifier = modifier.padding(top = paddingValues.calculateTopPadding(), bottom = paddingValues.calculateBottomPadding())) {
        if (uiState.postData.images.isNotEmpty()) {
            CarousalEffectCommunity(
                images = uiState.postData.images,
                autoScrollDuration = 0L,
                clipPercent = 0,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = uiState.postData.content ?: "",
                fontFamily = outFit,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                color = white,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .background(white10)
                    .padding(15.dp)
            )
        }
    }

    if (uiState.showMenuOptionsSheet) {
        PostDetailsOptionMenuDialog(uiState = uiState, event = event, modifier = Modifier)
    }

    if (uiState.showEditPostDialog) {
        EditPostDialog(uiState = uiState, event = event, viewModel = viewModel)
    }

    if (uiState.showDeletePostDialog) {
        DeletePostDialog(event = event, uiState = uiState)
    }

    if (uiState.showDeletePostSuccessDialog) {
        DeletePostSuccessDialog(event = event, uiState = uiState)
    }

    CommentsSheet(
        commentList = viewModel.getComments.collectAsLazyPagingItems(),
        shouldShowSheet = uiState.showCommentsSheet,
        commentValue = uiState.commentToPost,
        onCommentValueChange = { event(PostDetailsUiEvent.OnCommentPostValueChange(it)) },
        sendCommentClick = { event(PostDetailsUiEvent.PerformSendCommentClick(it)) },
        postId = uiState.postId,
    ) {
        event(PostDetailsUiEvent.IsCommentsDialogOpen(false, ""))
    }
}

@Composable
fun BottomViewContent(uiState: PostDetailsUiState, event: (PostDetailsUiEvent) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 20.dp)
            .background(Color.Transparent)
    ) {
        HStack(2.dp, modifier = Modifier.height(30.dp)) {
            AsyncImage(
                model = uiState.postData.user?.profileImage,
                placeholder = painterResource(R.drawable.img_portrait_placeholder),
                error = painterResource(R.drawable.img_portrait_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                clipToBounds = true,
                modifier = Modifier
                    .clip(RoundedCornerShape(28))
                    .size(32.dp)
            )
            Spacer(Modifier.padding(2.dp))
            Text(
                text = uiState.postData.user?.fullName ?: "",
                fontWeight = SemiBold,
                fontSize = 14.sp,
                fontFamily = outFit,
                color = white,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = AppUtils.formatTimestampForMyPosts(uiState.postData.createdAt ?: 0),
                fontWeight = Normal,
                fontSize = 12.sp,
                fontFamily = outFit,
                color = ColorOsloGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (uiState.postData.images.isNotEmpty() && uiState.postData.content?.isNotEmpty() == true) {
            ExpandableText(
                text = uiState.postData.content,
                modifier = Modifier.padding(top = 7.dp),
                fontSize = 12.sp,
                showMoreText = stringResource(R.string.more),
                showMoreStyle = SpanStyle(
                    color = white,
                    fontWeight = Medium,
                    textDecoration = TextDecoration.Underline
                ),
                style = TextStyle(
                    color = white,
                    fontSize = 12.sp,
                    fontFamily = outFit,
                    fontWeight = W300
                ),
                textAlign = TextAlign.Justify
            )
        }
        Row(modifier = Modifier.padding(top = 15.dp)) {
            IconTextHStack(
                icon = if (uiState.postData.isLike) R.drawable.ic_like_post else R.drawable.ic_empty_like,
                text = uiState.postData.likes.toString(),
                tint = if (uiState.postData.isLike) Color.Red else white,
                style = TextStyle(color = white, fontSize = 14.sp),
                spaceBy = 4.dp,
                iconModifier = Modifier.size(19.dp),
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(35))
                    .background(white10)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .noRippleClickable { event(PostDetailsUiEvent.PerformLikePostClick(uiState.postData.id ?: "")) }
            )
            Spacer(modifier = Modifier.padding(start = 12.dp))
            IconTextHStack(
                icon = R.drawable.ic_comment,
                text = uiState.postData.comments.toString(),
                tint = white,
                style = TextStyle(color = white, fontSize = 14.sp),
                spaceBy = 4.dp,
                iconModifier = Modifier.size(19.dp),
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(35))
                    .background(white10)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .noRippleClickable { event(PostDetailsUiEvent.IsCommentsDialogOpen(true, uiState.postData.id ?: "")) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostDetailsOptionMenuDialog(
    uiState: PostDetailsUiState,
    event: (PostDetailsUiEvent) -> Unit,
    modifier: Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(PostDetailsUiEvent.ShowMenuDialog(false)) },
            isSheetVisible = { uiState.showMenuOptionsSheet }
        ) {
            PostDetailsOptionsMenu(
                modifier = modifier, onClickEditPost = {
                    event(PostDetailsUiEvent.ClearImageList)
                    if (uiState.postData.images.isNotEmpty()) event(PostDetailsUiEvent.PostImages(uiState.postData.images.map { it1 -> LocalPostImages(id = it1.id, url = Uri.parse(it1.url)) }))
                    if (uiState.postData.content?.isNotEmpty() == true) event(PostDetailsUiEvent.OnPostContentValueChange(uiState.postData.content))
                    event(PostDetailsUiEvent.StoreAPIContentValue(uiState.postData.content ?: ""))
                    event(PostDetailsUiEvent.ShowEditPostDialog(true, uiState.postData.id ?: ""))
                },
                onClickDeletePost = {
                    event(PostDetailsUiEvent.ShowDeletePostDialog(true, uiState.postData.id ?: ""))
                }
            )
        }
    }
}

@Composable
fun PostDetailsOptionsMenu(
    modifier: Modifier = Modifier,
    onClickEditPost: () -> Unit,
    onClickDeletePost: () -> Unit
) {
    VStack(spaceBy = 0.dp, modifier = modifier.padding(20.dp)) {
        IconTextHStack(
            icon = R.drawable.edit,
            text = stringResource(R.string.edit_post),
            spaceBy = 10.dp,
            tint = MineShaft,
            style = TextStyle(
                fontWeight = Medium,
                fontSize = 14.sp,
                color = MineShaft
            ),
            iconSize = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    onClickEditPost()
                }
        )
        Spacer(modifier = Modifier.padding(top = 20.dp))
        IconTextHStack(
            icon = R.drawable.delete,
            text = stringResource(R.string.delete_post),
            spaceBy = 10.dp,
            tint = MineShaft,
            style = TextStyle(
                fontWeight = Medium,
                fontSize = 14.sp,
                color = MineShaft
            ),
            iconSize = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onClickDeletePost() }
        )
    }
}

@Composable
fun PostDeleteSheet(onYesClick: () -> Unit, onNoClick: () -> Unit) {
    VStack(
        spaceBy = 0.dp, modifier = Modifier
            .padding(20.dp)
            .background(white)
    ) {
        Image(
            painter = painterResource(R.drawable.delete_post), contentDescription = "",
            modifier = Modifier
                .padding(top = 20.dp)
                .size(70.dp)
        )

        Text(
            text = stringResource(R.string.are_you_sure_you_want_to_delete_this_post),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = stringResource(R.string.this_post_will_be_removed_permanently_after_deletion),
            fontFamily = outFit,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
        Spacer(modifier = Modifier.padding(top = 40.dp))
        HStack(8.dp) {
            SkaiButton(
                text = stringResource(R.string.no),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier.weight(1f),
                elevation = 0.dp,
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                onClick = {
                    onNoClick()
                }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.yes),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier.weight(1f),
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp)
            ) {
                onYesClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletePostDialog(
    event: (PostDetailsUiEvent) -> Unit,
    uiState: PostDetailsUiState
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = {
                event(
                    PostDetailsUiEvent.ShowDeletePostDialog(
                        false,
                        uiState.postId
                    )
                )
            },
            isSheetVisible = { uiState.showDeletePostDialog }
        ) {
            PostDeleteSheet(
                onYesClick = {
                    event(PostDetailsUiEvent.DeletePost(uiState.postId))
                },
                onNoClick = {
                    event(PostDetailsUiEvent.ShowDeletePostDialog(false, uiState.postId))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletePostSuccessDialog(
    event: (PostDetailsUiEvent) -> Unit,
    uiState: PostDetailsUiState
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = {
                event(PostDetailsUiEvent.ShowDeletePostSuccessDialog(false))
            },
            isSheetVisible = { uiState.showDeletePostSuccessDialog }
        ) {
            DeletedPostSuccessSheet(
                onClickOfGetStarted = {
                    event(PostDetailsUiEvent.PerformSeeMyPostsClick)
                }
            )
        }
    }
}

@Composable
fun DeletedPostSuccessSheet(
    onClickOfGetStarted: () -> Unit
) {

    VStack(
        spaceBy = 0.dp, modifier = Modifier
            .padding(20.dp)
            .background(white)
    ) {
        Image(
            painter = painterResource(R.drawable.delete_post), contentDescription = "",
            modifier = Modifier
                .padding(top = 20.dp)
                .size(70.dp)
        )

        Text(
            text = stringResource(R.string.post_has_been_deleted),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = stringResource(R.string.post_is_no_more),
            fontFamily = outFit,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
        Spacer(modifier = Modifier.padding(top = 40.dp))
        BottomButtonComponent(
            onClick = {
                onClickOfGetStarted()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .padding(horizontal = 30.dp),
            text = stringResource(R.string.see_my_posts).uppercase(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPostDialog(
    uiState: PostDetailsUiState,
    event: (PostDetailsUiEvent) -> Unit,
    viewModel: PostDetailsViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(PostDetailsUiEvent.ShowEditPostDialog(false, uiState.postId)) },
            isSheetVisible = { uiState.showEditPostDialog },
            title = stringResource(R.string.edit_post)
        ) {
            EditPost(viewModel = viewModel, isFrom = Constants.Keywords.POST_DETAILS)
        }
    }
}