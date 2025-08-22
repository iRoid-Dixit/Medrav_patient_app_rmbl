package com.griotlegacy.mobile.app.ux.main.home
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.griotlegacy.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.griotlegacy.mobile.app.model.domain.response.container.legacyPost.Media
import com.griotlegacy.mobile.app.navigation.HandleNavigation
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.CustomDropdownMenu
import com.griotlegacy.mobile.app.ui.compose.common.HorizontalPagerIndicator
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.White20
import com.griotlegacy.mobile.app.ui.theme.White50
import com.griotlegacy.mobile.app.ui.theme.WorkSans
import com.griotlegacy.mobile.app.utils.AppUtils
import com.griotlegacy.mobile.app.utils.AppUtils.noRippleClickable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    uiState.event(HomeUiEvent.GetContext(context))
    val homeDetailsData by uiState.homeUiDataFlow.collectAsStateWithLifecycle()
    BackHandler(onBack = {
        uiState.event(HomeUiEvent.BackClick)
    })
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
                    Image(
                        painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            uiState.event(HomeUiEvent.NavigateToNotification)

                        }
                    )
                }
            }
        }
    ) {
        HomeScreenContent(uiState, homeDetailsData)
    }
    if (homeDetailsData?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(uiState: HomeUiState, homeDetailsData: HomeUiDataState?) {
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    val mainVillageList = uiState.mainVillageList.collectAsLazyPagingItems()
    val advertisementList = uiState.advertisementList.collectAsLazyPagingItems()
    if (state.isRefreshing) {
        LaunchedEffect(Unit) {
            uiState.event(HomeUiEvent.PullToRefreshAPICall)
            state.endRefresh()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(state.nestedScrollConnection)
    ) {
        mainVillageList.loadState.refresh.apply {
            when (this) {
                is LoadState.Error -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        TapHereRefreshContent(onClick = { mainVillageList.retry() })
                    }
                }
                is LoadState.Loading -> {
                    CustomLoader()
                }
                is LoadState.NotLoading -> {
                    if (mainVillageList.itemCount == 0) {
                        NoDataFoundContent(text = "No data found")
                    } else {
                        //Spacer(modifier = Modifier.height(15.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, end = 16.dp, top = 15.dp)
                        ) {
                            // Add demoView as the first item
                            if (advertisementList.itemCount != 0) {
                                item {
                                    //if (advertisementList.itemCount!=0){
                                    AdvertisementItem(advertisementList, onAdvertisementClick = {
                                        uiState.event(HomeUiEvent.AdvertisementClick(it))
                                    })
                                    // }
                                    // demoView()
                                    //  Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                            items(
                                mainVillageList.itemCount,
                                key = { index ->
                                    mainVillageList[index]?.id ?: index
                                }) { index ->
                                mainVillageList[index]?.let { item ->
                                    MainVillageItem(item, uiState, homeDetailsData)
                                }
                            }
                            when (mainVillageList.loadState.append) {
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
                                                        mainVillageList.retry()
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

@Composable
fun MainVillageItem(
    item: LegacyPostResponse,
    uiState: HomeUiState,
    homeDetailsData: HomeUiDataState?,
) {
    var expanded by remember { mutableStateOf(false) }
    val checkedStateItem = if (item.ownLike == true) {
        Pair(R.drawable.ic_app_icon, "Checked")
    } else {
        Pair(R.drawable.ic_app_icon, "Unchecked")
    }
    val menuItems = listOf(
        stringResource(R.string.report_this_post) to {
            uiState.event(
                HomeUiEvent.ReportPost(item.id ?: "")
            )
        },
        stringResource(R.string.report_user) to {
            uiState.event(
                HomeUiEvent.ReportUser(item.userId ?: "")
            )
        }
    )
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(width = 1.dp, color = White20),
        onClick = {
            uiState.event(HomeUiEvent.NavigateToPostDetails(item.id ?: ""))
        },
        colors = CardDefaults.cardColors(
            containerColor = AppThemeColor
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)) {
            //Spacer(modifier = Modifier.height(15.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = item.profileImage,
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_app_icon),
                    placeholder = painterResource(id = R.drawable.ic_app_icon),
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.name ?: "",
                        fontWeight = FontWeight.W500,
                        fontFamily = WorkSans,
                        lineHeight = 18.sp,
                        color = White,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = AppUtils.formatDateTime(item.createdAt.toString()),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = WorkSans,
                        color = White50
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (item.userId != homeDetailsData?.userId) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = stringResource(
                            R.string.edit
                        ),
                        modifier = Modifier.clickable {
                            expanded = true
                        }
                    )
                }
                CustomDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    menuItems = menuItems,
                    offsetY = 30.dp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.legacyText ?: "",
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                lineHeight = 18.sp,
                fontFamily = WorkSans,
                textAlign = TextAlign.Start,
                color = White
            )
            Spacer(modifier = Modifier.height(15.dp))
            MainVillageVideoImage(
                media = item.media,
                videoPreviewClick = {
                    uiState.event(HomeUiEvent.VideoPreviewClick(item.media))
                },
                imageDisplayClick = {
                    uiState.event(HomeUiEvent.ImageDisplay(item.media))
                }
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .noRippleClickable {
                        // Optional: If you want the Row itself to handle a click
                    }
            ) {
                // Like Section
                Row(
                    modifier = Modifier.noRippleClickable {
                        // val newOwnLike = !ownLike
                        // ownLike = newOwnLike
                        /* likeCount = if (item.ownLike == true) item.likeCount?.plus(1)?:0 else item.likeCount?.minus(1)?:0
                         uiState.event(HomeUiEvent.IsLikeDisLikeAPICall(item.id ?: ""))*/

                        item.ownLike = item.ownLike != true
                        item.likeCount?.let {
                            uiState.event(HomeUiEvent.IsLikeDisLikeAPICall(item.id ?: ""))
                        }
                        item.likeCount =
                            if (item.ownLike == true) item.likeCount?.plus(1) else item.likeCount?.minus(
                                1
                            )

                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = checkedStateItem.first),
                        modifier = Modifier.size(25.dp),
                        contentDescription = checkedStateItem.second
                    )
                    Spacer(modifier = Modifier.padding(3.dp))
                    Text(
                        text = item.likeCount.toString(),
                        fontSize = 14.sp,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W400,
                        color = White
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                // Comment Section
                Row(
                    modifier = Modifier.noRippleClickable {
                        uiState.event(
                            HomeUiEvent.NavigateToPostDetails(item.id ?: "")
                        )
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.padding(3.dp))
                    Text(
                        text = item.commentCount.toString(),
                        fontSize = 14.sp,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W400,
                        color = White
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdvertisementItem(advertisementList: LazyPagingItems<AdvertisementResponse>, onAdvertisementClick: (String) -> Unit = {}) {
    val pagerState = rememberPagerState(pageCount = { advertisementList.itemCount })
    val scope = rememberCoroutineScope()

    // Auto-scroll logic
    LaunchedEffect(pagerState.currentPage) {
        if (advertisementList.itemCount > 1) {
            delay(3000) // Change slide every 3 seconds
            scope.launch {
                val nextPage = (pagerState.currentPage + 1) % advertisementList.itemCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .noRippleClickable {

            }

            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.Transparent, RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val mediaItem = advertisementList[page]
                Box(modifier = Modifier.fillMaxSize()) {
                    var isLoading by remember { mutableStateOf(true) }
                    AsyncImage(
                        model = mediaItem?.image ?: "",
                        contentDescription = stringResource(R.string.post_image),
                        contentScale = ContentScale.Crop,
                        onState = {
                            isLoading = it is AsyncImagePainter.State.Loading
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .noRippleClickable {
                                onAdvertisementClick(mediaItem?.id ?: "")
                                // Handle advertisement click
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
            }
            if (advertisementList.itemCount > 1) {
                HorizontalPagerIndicator(
                    count = advertisementList.itemCount,
                    selectedIndex = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainVillageVideoImage(
    media: List<Media>,
    videoPreviewClick: () -> Unit = {},
    imageDisplayClick: () -> Unit = {}
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
                val imageUrl = if (mediaItem.type == 1) mediaItem.filename else mediaItem.thumbnail

                // Create painter
                val painter = rememberAsyncImagePainter(model = imageUrl)
                val painterState = painter.state

                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painter,
                        contentDescription = stringResource(R.string.post_image),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (mediaItem.type == 1) {
                                    imageDisplayClick()
                                } else {
                                    videoPreviewClick()
                                }
                            }
                    )
                    // Show loader if image is loading
                    if (painterState is AsyncImagePainter.State.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    // Show video icon if it's a video
                    if (mediaItem.type == 2 && painterState is AsyncImagePainter.State.Success) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_icon),
                            contentDescription = stringResource(R.string.video),
                            colorFilter = ColorFilter.tint(White),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clickable {
                                    videoPreviewClick()
                                }
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
