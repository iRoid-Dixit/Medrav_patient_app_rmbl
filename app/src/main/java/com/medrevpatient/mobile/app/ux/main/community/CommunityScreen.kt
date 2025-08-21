package com.medrevpatient.mobile.app.ux.main.community

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.androidisland.ezpermission.EzPermission
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.response.CommunityPosts
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.common.BasicBottomSheet
import com.medrevpatient.mobile.app.ui.common.ExpandableText
import com.medrevpatient.mobile.app.ui.common.PermissionDialog
import com.medrevpatient.mobile.app.ui.common.TitleDualFont
import com.medrevpatient.mobile.app.ui.theme.ColorOsloGray
import com.medrevpatient.mobile.app.ui.theme.ColorSilverSand
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MineShaft3
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.black
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.black25A25
import com.medrevpatient.mobile.app.ui.theme.black50
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ui.theme.white10
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.ux.main.component.IconTextHStack
import com.medrevpatient.mobile.app.ux.main.component.OutlineTextFieldWithTrailing
import com.medrevpatient.mobile.app.ux.main.component.RoundedCarouselImageWithDescription
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import es.dmoral.toasty.Toasty
import java.io.ByteArrayOutputStream

@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = viewModel::event
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.getAcceptedGuidelineValue()
    }
    if (uiState.isCommunityGuidelinesAccepted) {
        Scaffold(
            modifier = modifier
                .statusBarsPadding()
                .fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                TopBarCenterAlignTextBack(
                    title = stringResource(R.string.community),
                    onBackPress = {},
                    isTrailingIconVisible = !uiState.isUserBlockedByAdmin,
                    isBackIconVisible = false,
                    onTrailIconPress = {
                        event(CommunityUiEvent.ShowMenuDialog(true))
                    }
                )
            },
        ) { innerPadding ->
            if (uiState.isLoading) DialogLoader()
            CommunityMainContent(
                uiState = uiState,
                event = event,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                viewModel = viewModel
            )

        }
    } else {
        MedrevPatientTheme {
            CommunityScreenContent(PaddingValues(), viewModel::event)
        }
    }
}

@Composable
private fun CommunityScreenContent(innerPadding: PaddingValues, event: (CommunityUiEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .padding(innerPadding)
    ) {
        Image(
            painter = painterResource(id = R.drawable.on_board_one), contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = List(22) { Color.Transparent } + white.copy(alpha = 0.11f)
                    )
                ),
            content = {}
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 25.dp, end = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TitleDualFont(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 130.dp),

                color = white,
                fontWeightBold = Bold,
                fontWeightRegular = Light,
                fontSize = 20,
                fontFamilyBold = outFit,
                fontFamilyRegular = outFit,
                titlePart1 = stringResource(id = R.string.skai),
                titlePart2 = stringResource(id = R.string.fitness)
            )

            Text(
                text = stringResource(R.string.community).uppercase(),
                fontSize = 22.sp,
                fontWeight = Light,
                color = white,
                fontFamily = outFit,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    letterSpacing = 0.7.em
                )
            )
            Text(
                text = stringResource(R.string.community_txt),
                fontSize = 12.sp,
                fontWeight = Light,
                color = white,
                fontFamily = outFit,
                textAlign = TextAlign.Center
            )

            SkaiButton(
                text = stringResource(R.string.lets_explore),
                makeUpperCase = true,
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 16.dp),
                onClick = {
                    event(CommunityUiEvent.PerformExploreClick)
                },
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                color = white,
                textStyle = TextStyle(
                    color = MineShaft,
                    fontWeight = FontWeight.W800,
                    fontFamily = outFit,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
private fun CommunityMainContent(
    uiState: CommunityUiState,
    modifier: Modifier,
    event: (CommunityUiEvent) -> Unit,
    viewModel: CommunityViewModel
) {
    val posts = viewModel.communityPosts.collectAsLazyPagingItems()
    val coachData = posts.itemSnapshotList.firstOrNull() ?: CommunityPosts()
    val loadState = posts.loadState

    when {
        loadState.refresh is LoadState.Error -> {
            val error = (loadState.refresh as LoadState.Error).error
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = error.message.toString(),
                    fontSize = 16.sp,
                    fontFamily = outFit,
                    fontWeight = SemiBold,
                    color = MineShaft,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp)
                )
            }
            event(CommunityUiEvent.IsUserBlockedByAdmin(true))
        }

        posts.itemCount == 0 && posts.loadState.refresh !is LoadState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(id = R.string.no_posts_found),
                    fontSize = 16.sp,
                    fontFamily = outFit,
                    fontWeight = SemiBold,
                    color = MineShaft,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            event(CommunityUiEvent.IsUserBlockedByAdmin(false))
        }

        else -> {
            VStack(
                15.dp, modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                event(CommunityUiEvent.IsUserBlockedByAdmin(false))
                if (coachData.id != null && coachData.isVerified) {
                    TopMainPostContent(
                        modifier = Modifier
                            .height(330.dp)
                            .padding(horizontal = 18.dp)
                            .clip(RoundedCornerShape(11)),
                        data = coachData,
                        uiState = uiState,
                        event = event
                    )
                }
                Text(
                    text = stringResource(R.string.all_posts).uppercase(),
                    color = black,
                    fontSize = 14.sp,
                    fontWeight = SemiBold,
                    fontFamily = outFit,
                    style = TextStyle(
                        letterSpacing = 0.3.em
                    )
                )
                PagingResultHandler(posts) { pagingState ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        pagingState.itemSnapshotList.drop(1).forEachIndexed { index, _ ->
                            val post = posts[index + 1] ?: return@forEachIndexed
                            AllPostsItem(data = post, onClickComment = { uiState.showCommentsSheet }, event = event, uiState = uiState)
                        }
                    }
                }
            }
        }
    }

    CommentsSheet(
        commentList = viewModel.getComments.collectAsLazyPagingItems(),
        shouldShowSheet = uiState.showCommentsSheet,
        postId = uiState.postId,
        commentValue = uiState.commentToPost,
        onCommentValueChange = { event(CommunityUiEvent.OnCommentPostValueChange(it)) },
        sendCommentClick = { postId ->
            event(CommunityUiEvent.PerformSendCommentClick(postId))
        },
    ) {
        event(CommunityUiEvent.IsCommentsDialogOpen(false, ""))
    }

    if (uiState.showMenuOptionsSheet) {
        CommunityOptionMenuDialog(uiState, event = event, modifier = Modifier)
    }
    if (uiState.showCreatePostSheet) {
        CreatePostDialog(uiState, event)
    }
    if (uiState.showPostPreviewSheet) {
        PreviewPostDialog(uiState, event)
    }
    if (uiState.showReportPostSheet) {
        ReportPostDialog(uiState, event)
    }
}

@Composable
private fun AllPostsItem(
    data: CommunityPosts,
    onClickComment: () -> Unit = {},
    event: (CommunityUiEvent) -> Unit,
    uiState: CommunityUiState
) {
    if (!data.isVerified) {
        RoundedCarouselImageWithDescription(
            isImageVisible = data.images.isNotEmpty(),
            isBackgroundGradient = true,
            clipPercent = 11,
            images = data.images.ifEmpty { emptyList() },
            modifier = Modifier
                .fillMaxWidth()
                .then(if (data.images.isNotEmpty()) Modifier.height(450.dp) else Modifier)
                .padding(horizontal = 18.dp, vertical = 7.dp)
                .clip(RoundedCornerShape(11)),
            content = {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 20.dp)
                        .background(Color.Transparent)
                ) {
                    HStack(2.dp, modifier = Modifier.height(30.dp)) {
                        AsyncImage(
                            model = data.user?.profileImage,
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
                            text = data.user?.fullName ?: "",
                            fontWeight = SemiBold,
                            fontSize = 14.sp,
                            fontFamily = outFit,
                            color = MineShaft,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = AppUtils.formatTimestampForPostCreation(data.createdAt ?: 0),
                            fontWeight = Normal,
                            fontSize = 12.sp,
                            fontFamily = outFit,
                            color = ColorOsloGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    ExpandableText(
                        text = data.content ?: "",
                        modifier = Modifier.padding(top = 7.dp),
                        fontSize = 12.sp,
                        showMoreText = stringResource(R.string.more),
                        showMoreStyle = SpanStyle(
                            color = MineShaft,
                            fontWeight = Medium,
                            textDecoration = TextDecoration.Underline
                        ),
                        style = TextStyle(
                            color = MineShaft,
                            fontSize = 12.sp,
                            fontFamily = outFit,
                            fontWeight = W300
                        ),
                        textAlign = TextAlign.Justify
                    )
                    Row(modifier = Modifier.padding(top = 15.dp)) {
                        Row(modifier = Modifier.weight(1f)) {
                            IconTextHStack(
                                icon = if (data.isLike) R.drawable.ic_like_post else R.drawable.ic_empty_like,
                                text = if (data.likes == null) "0" else data.likes.toString(),
                                tint = if (data.isLike) Color.Red else MineShaft,
                                style = TextStyle(color = MineShaft, fontSize = 14.sp),
                                spaceBy = 4.dp,
                                iconModifier = Modifier.size(19.dp),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .clip(RoundedCornerShape(35))
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                white,
                                                ColorSwansDown
                                            )
                                        )
                                    )
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                                    .noRippleClickable {
                                        data.isLike = data.isLike != true
                                        data.likes = if (data.isLike) data.likes?.plus(1) else data.likes?.minus(1)
                                        event(CommunityUiEvent.PerformLikePostClick(data.id.toString()))
                                    }
                            )
                            Spacer(modifier = Modifier.padding(start = 12.dp))
                            IconTextHStack(
                                icon = R.drawable.ic_comment,
                                text = data.comments.toString(),
                                tint = MineShaft,
                                style = TextStyle(color = MineShaft, fontSize = 14.sp),
                                spaceBy = 4.dp,
                                iconModifier = Modifier.size(19.dp),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .clip(RoundedCornerShape(35))
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(white, ColorSwansDown)
                                        )
                                    )
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                                    .noRippleClickable {
                                        event(CommunityUiEvent.IsCommentsDialogOpen(true, data.id.toString()))
                                        if (uiState.showCommentsSheet) onClickComment()
                                    }
                            )
                        }
                        if (data.user?.id != uiState.userData.id)
                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_report_post),
                                contentDescription = "report",
                                modifier = Modifier.noRippleClickable { event(CommunityUiEvent.ShowReportPostDialog(true, data.id.toString())) }
                            )
                    }
                }
            }
        )
    }
}

@Composable
private fun TopMainPostContent(modifier: Modifier = Modifier, data: CommunityPosts, uiState: CommunityUiState, event: (CommunityUiEvent) -> Unit) {
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            model = data.images.firstOrNull()?.url,
            placeholder = painterResource(R.drawable.community_dummy_img),
            error = painterResource(R.drawable.community_dummy_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            black50,
                            black
                        )
                    )
                ),
        ) {
            Text(
                text = stringResource(R.string.coach_broadcast),
                color = white,
                fontSize = 12.sp,
                fontWeight = Normal,
                fontFamily = outFit,
                style = TextStyle(
                    letterSpacing = 0.5.em
                ),
                modifier = Modifier
                    .padding(15.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 20.dp)
            ) {
                HStack(2.dp, modifier = Modifier.height(30.dp)) {
                    AsyncImage(
                        model = data.user?.profileImage,
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
                        text = data.user?.fullName ?: "",
                        fontWeight = SemiBold,
                        fontSize = 14.sp,
                        fontFamily = outFit,
                        color = Color.White
                    )
                    Image(
                        painter = painterResource(R.drawable.golden_verify_badge),
                        contentDescription = "verified",
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                ExpandableText(
                    text = data.content ?: "",
                    modifier = Modifier.padding(top = 5.dp),
                    fontSize = 12.sp,
                    showMoreText = stringResource(R.string.more),
                    showMoreStyle = SpanStyle(
                        color = white,
                        fontWeight = Medium,
                        textDecoration = TextDecoration.Underline
                    ),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = outFit,
                        fontWeight = W300
                    )
                )
                Row(modifier = Modifier.padding(top = 15.dp)) {
                    IconTextHStack(
                        icon = if (data.isLike) R.drawable.ic_like_post else R.drawable.ic_empty_like,
                        text = if (data.likes == null) "0" else data.likes.toString(),
                        tint = if (data.isLike) Color.Red else Color.White,
                        style = TextStyle(color = Color.White, fontSize = 14.sp),
                        spaceBy = 4.dp,
                        iconModifier = Modifier.size(19.dp),
                        modifier = Modifier
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(35))
                            .background(white10)
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                            .noRippleClickable {
                                data.isLike = data.isLike != true
                                data.likes = if (data.isLike) data.likes?.plus(1) else data.likes?.minus(1)
                                //AppUtils.Toast(LocalContext.current, "Success" ?: "Something went wrong!").show()
                                event(CommunityUiEvent.PerformLikePostClick(data.id.toString()))
                            }
                    )
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    IconTextHStack(
                        icon = R.drawable.ic_comment,
                        text = if (data.comments == null) "0" else data.comments.toString(),
                        tint = Color.White,
                        style = TextStyle(color = Color.White, fontSize = 14.sp),
                        spaceBy = 4.dp,
                        iconModifier = Modifier.size(19.dp),
                        modifier = Modifier
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(35))
                            .background(white10)
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                            .noRippleClickable {
                                event(CommunityUiEvent.IsCommentsDialogOpen(true, data.id.toString()))
                                if (uiState.showCommentsSheet) uiState.showCommentsSheet
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunityOptionMenuDialog(
    uiState: CommunityUiState,
    event: (CommunityUiEvent) -> Unit,
    modifier: Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(CommunityUiEvent.ShowMenuDialog(false)) },
            isSheetVisible = { uiState.showMenuOptionsSheet }
        ) {
            CommunityOptionsMenu(
                modifier = modifier, onClickCreatePost = {
                    event(CommunityUiEvent.ShowCreatePostDialog(true))
                }, onClickMyPosts = {
                    event(CommunityUiEvent.ShowMenuDialog(false))
                    event(CommunityUiEvent.NavigateToMyPosts)
                }
            )
        }
    }
}

@Composable
fun CommunityOptionsMenu(
    modifier: Modifier = Modifier,
    onClickCreatePost: () -> Unit,
    onClickMyPosts: () -> Unit
) {
    VStack(spaceBy = 0.dp, modifier = modifier.padding(20.dp)) {
        IconTextHStack(
            icon = R.drawable.create_post,
            text = stringResource(R.string.create_post),
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
                    onClickCreatePost()
                }
        )
        Spacer(modifier = Modifier.padding(top = 20.dp))
        IconTextHStack(
            icon = R.drawable.posts,
            text = stringResource(R.string.my_posts),
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
                .noRippleClickable { onClickMyPosts() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePostDialog(
    uiState: CommunityUiState,
    event: (CommunityUiEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(CommunityUiEvent.ShowCreatePostDialog(false)) },
            isSheetVisible = { uiState.showCreatePostSheet },
            title = stringResource(R.string.create_post)
        ) {
            CreatePost(uiState, event)
        }
    }
}

@SuppressLint("InlinedApi")
@Composable
private fun CreatePost(uiState: CommunityUiState, event: (CommunityUiEvent) -> Unit) {
    val context = LocalContext.current

    val android13PermissionList: ArrayList<String> = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )
    val permissionList: ArrayList<String> = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val showPermissionDialog by uiState.showPermissionDialog.collectAsStateWithLifecycle()

    var imageUri by remember { mutableStateOf<List<Uri?>>(emptyList()) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            if (uris.size > 3) {
                Toasty.info(context, context.getString(R.string.max_3_images)).show()
                return@rememberLauncherForActivityResult
            }
            val selectedUris = uris.take(3)
            imageUri = imageUri + selectedUris
            event(CommunityUiEvent.PostImages(imageUri as List<Uri>))
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val uri = uriFromBitmap(context, it)
                imageUri = imageUri + uri
                event(CommunityUiEvent.PostImages(imageUri as List<Uri>))
            }
        }

    val startForCameraPermissionResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _: ActivityResult -> }

    if (showPermissionDialog) {
        PermissionDialog(
            onDismissRequest = { uiState.onShowPermissionDialog(false) },
            title = stringResource(R.string.app_name),
            description = stringResource(R.string.camera_permission_txt),
            negativeText = stringResource(R.string.cancel),
            positiveText = stringResource(R.string.open_setting),
            onPositiveClick = {
                uiState.onShowPermissionDialog(false)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uriIntent = Uri.fromParts("package", context.packageName, null)
                intent.data = uriIntent
                startForCameraPermissionResult.launch(intent)
            },
        )
    }
    VStack(
        spaceBy = 0.dp, modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .background(white)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.upload_photos),
            fontSize = 16.sp,
            fontFamily = outFit,
            fontWeight = SemiBold,
            color = MineShaft
        )
        Text(
            text = stringResource(R.string.you_can_add_max_3_photos),
            fontSize = 12.sp,
            fontFamily = outFit,
            fontWeight = Medium,
            color = ColorSilverSand,
            modifier = Modifier.padding(top = 5.dp)
        )
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .background(
                    color = MineShaft3,
                    shape = RoundedCornerShape(25)
                )
                .padding(horizontal = 12.dp, vertical = 15.dp)
                .noRippleClickable {
                    permissionCheck(
                        context = context,
                        android13PermissionList = android13PermissionList,
                        permissionList = permissionList,
                        galleryLauncher = launcher,
                        cameraLauncher = cameraLauncher,
                        uiState = uiState
                    )
                }

        ) {
            Text(
                text = stringResource(R.string.upload_photo),
                fontSize = 14.sp,
                fontFamily = outFit,
                fontWeight = Light,
                color = MineShaft,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.camera),
                contentDescription = ""
            )
        }
        if (imageUri.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 15.dp),
            ) {
                imageUri.forEach {
                    PhotoItem(imageData = it ?: Uri.EMPTY) {
                        imageUri = imageUri.filterNot { it1 -> it1 == it } // Remove selected image
                        event(CommunityUiEvent.PostImages(imageUri as List<Uri>))
                    }
                }
            }
        }
        Text(
            text = stringResource(R.string.write_about_post),
            fontSize = 16.sp,
            fontFamily = outFit,
            fontWeight = Bold,
            color = MineShaft,
            modifier = Modifier.padding(top = 15.dp)
        )
        Text(
            text = stringResource(R.string.share_details_of_your_post),
            fontSize = 12.sp,
            fontFamily = outFit,
            fontWeight = Medium,
            color = ColorSilverSand,
            modifier = Modifier.padding(top = 5.dp)
        )
        OutlineTextFieldWithTrailing(
            value = uiState.postContentValue,
            onValueChange = {
                if (it.length <= 200) {
                    event(CommunityUiEvent.OnPostContentValueChange(it))
                }
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.write_here),
                    fontWeight = W300,
                    color = black25,
                    fontSize = 14.sp
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            maxLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(top = 5.dp),
            trailingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_description),
                    contentDescription = "",
                    modifier = Modifier
                        .offset(y = (-25).dp)
                        .size(20.dp)
                )
            },
            shape = RoundedCornerShape(20)
        )
        Spacer(Modifier.padding(top = 50.dp))
        HStack(8.dp) {
            SkaiButton(
                text = stringResource(R.string.cancel),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 15.dp),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                elevation = 0.dp,
                onClick = {
                    event(CommunityUiEvent.ShowCreatePostDialog(false))
                }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.preview),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 15.dp),
                elevation = 3.dp,
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp)
            ) {
                if (uiState.postContentValue.isNotEmpty() || uiState.postImages.isNotEmpty()) {
                    event(CommunityUiEvent.ShowPostPreviewDialog(true))
                } else {
                    Toasty.info(context, context.getString(R.string.post_preview_error)).show()
                }
            }
        }
    }
}

@Composable
fun PhotoItem(
    modifier: Modifier = Modifier, imageData: Uri, onDelete: () -> Unit
) {
    Box(
        modifier = modifier
            .width(100.dp)
            .height(90.dp)
            .padding(end = 12.dp)
            .clip(RoundedCornerShape(20))
            .background(Color.Transparent)
    ) {
        AsyncImage(
            model = imageData.toString(),
            placeholder = painterResource(R.drawable.img_portrait_placeholder),
            error = painterResource(R.drawable.img_portrait_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.delete_white_with_bg),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .size(30.dp)
                .noRippleClickable {
                    onDelete()
                }
        )
    }
}

fun permissionCheck(
    context: Context,
    android13PermissionList: ArrayList<String>,
    permissionList: ArrayList<String>,
    galleryLauncher: ManagedActivityResultLauncher<String, List<Uri>>,
    cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    uiState: CommunityUiState
) {
    var isGranted = false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        EzPermission.with(context)
            .permissions(android13PermissionList)
            .request { granted, denied, _ ->
                android13PermissionList.forEach {
                    if (granted.contains(it)) {
                        isGranted = true
                    } else if (denied.contains(it)) {
                        isGranted = false
                    }
                }
                if (isGranted) {
                    showImagePickerOptions(context, galleryLauncher, cameraLauncher)
                } else {
                    uiState.onShowPermissionDialog(true)
                }
            }
    } else {
        EzPermission.with(context)
            .permissions(permissionList)
            .request { granted, denied, _ ->
                permissionList.forEach {
                    if (granted.contains(it)) {
                        isGranted = true
                    } else if (denied.contains(it)) {
                        isGranted = false
                    }
                }
                if (isGranted) {
                    showImagePickerOptions(context, galleryLauncher, cameraLauncher)
                } else {
                    uiState.onShowPermissionDialog(true)
                }
            }
    }
}

private fun uriFromBitmap(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    return Uri.parse(path)
}

private fun showImagePickerOptions(
    context: Context,
    galleryLauncher: ActivityResultLauncher<String>,
    cameraLauncher: ActivityResultLauncher<Void?>
) {
    val options = arrayOf(
        context.getString(R.string.select_from_gallery),
        context.getString(R.string.open_camera)
    )
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.choose_an_option))
        .setItems(options) { _, which ->
            when (which) {
                0 -> galleryLauncher.launch("image/*")
                1 -> cameraLauncher.launch(null)
            }
        }
        .create().apply {
            window?.setBackgroundDrawableResource(R.drawable.bg_permissions)
        }
        .show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewPostDialog(
    uiState: CommunityUiState,
    event: (CommunityUiEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(CommunityUiEvent.ShowPostPreviewDialog(false)) },
            isSheetVisible = { uiState.showPostPreviewSheet },
            title = stringResource(R.string.preview)
        ) {
            PreviewPost(uiState = uiState, event = event)
        }
    }
}

@Composable
private fun PreviewPost(uiState: CommunityUiState, event: (CommunityUiEvent) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { uiState.postImages.size })
    VStack(
        spaceBy = 0.dp, modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .background(white),
        horizontalAlignment = Alignment.Start
    ) {
        if (uiState.postImages.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                pageSpacing = 16.dp,
                modifier = Modifier
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(10))
                        .background(Color.Transparent)
                ) {
                    AsyncImage(
                        model = uiState.postImages[page].toString(),
                        placeholder = painterResource(R.drawable.img_portrait_placeholder),
                        error = painterResource(R.drawable.img_portrait_placeholder),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) black25 else black25A25
                    val width = if (pagerState.currentPage == iteration) 20.dp else 10.dp

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .size(width = width, height = 8.dp)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                }

            }
        }
        if (uiState.postContentValue.isNotEmpty()) {
            Text(
                text = stringResource(R.string.about_your_post),
                color = MineShaft,
                fontWeight = Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.Start)
            )

            Text(
                text = uiState.postContentValue,
                color = MineShaft,
                fontWeight = Light,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 7.dp)
                    .align(Alignment.Start)
                    .clip(RoundedCornerShape(22))
                    .background(MineShaft3)
                    .padding(15.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 50.dp))
        HStack(8.dp) {
            SkaiButton(
                text = stringResource(R.string.cancel),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier.weight(1f),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                elevation = 0.dp,
                onClick = {
                    event(CommunityUiEvent.ShowPostPreviewDialog(false))
                }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.post),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier.weight(1f),
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp)
            ) {
                event(CommunityUiEvent.PerformCreatePostClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportPostDialog(
    uiState: CommunityUiState,
    event: (CommunityUiEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(CommunityUiEvent.ShowReportPostDialog(false, "")) },
            isSheetVisible = { uiState.showReportPostSheet },
            title = stringResource(R.string.report_post)
        ) {
            ReportPostView(uiState = uiState, event = event)
        }
    }
}

@Composable
private fun ReportPostView(uiState: CommunityUiState, event: (CommunityUiEvent) -> Unit) {
    val reportPostArray = stringArrayResource(R.array.report_post_array)
    var selectedOption by remember { mutableIntStateOf(-1) }

    VStack(
        spaceBy = 0.dp, modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .background(white)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ){
        reportPostArray.forEachIndexed { index, text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .noRippleClickable {
                        selectedOption = index
                        event(CommunityUiEvent.OnReportPostReasonSelection(index.plus(1)))
                    }
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(
                        id = if (selectedOption == index) R.drawable.readio_button__selected_ else R.drawable.readio_button__unselected_
                    ),
                    contentDescription = null,
                )
                Text(
                    text = text,
                    color = MineShaft,
                    fontWeight = Medium,
                    fontSize = 14.sp,
                    fontFamily = outFit,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
        if (selectedOption == 8) {//if "Other" option selected
            OutlineTextFieldWithTrailing(
                value = uiState.reportPostContentValue,
                onValueChange = { event(CommunityUiEvent.OnReportPostValueChange(it)) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.write_here),
                        fontWeight = W300,
                        color = black25,
                        fontSize = 14.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(20)
            )
        }
        Spacer(Modifier.padding(top = 12.dp))
        HStack(8.dp) {
            SkaiButton(
                text = stringResource(R.string.cancel),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 15.dp),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                elevation = 0.dp,
                onClick = {
                    event(CommunityUiEvent.ShowReportPostDialog(false, ""))
                }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.report),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 15.dp),
                elevation = 3.dp,
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp)
            ) {
                event(CommunityUiEvent.PerformReportPostClick)
            }
        }
    }
}