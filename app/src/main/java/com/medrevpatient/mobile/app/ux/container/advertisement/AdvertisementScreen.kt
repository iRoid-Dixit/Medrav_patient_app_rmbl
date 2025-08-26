package com.medrevpatient.mobile.app.ux.container.advertisement

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImagePainter
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.Blue05
import com.medrevpatient.mobile.app.ui.theme.Green2C
import com.medrevpatient.mobile.app.ui.theme.RedC9
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.YellowDF

@ExperimentalMaterial3Api
@Composable
fun AdvertisementScreen(
    navController: NavController,
    viewModel: AdvertisementViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val blockUiState by uiState.advertisementDataFlow.collectAsStateWithLifecycle()
    uiState.event(AdvertisementUiEvent.GetContext(context))
    val navBackStackEntry = navController.currentBackStackEntry
    val advertisementScreen =
        navBackStackEntry?.savedStateHandle?.get<String>("advertisementScreen")
    Log.d("TAG", "AdvertisementScreen: $advertisementScreen")
    BackHandler(onBack = {
        uiState.event(AdvertisementUiEvent.BackClick)
    })
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                onClick = { navController.popBackStack() },
                titleText = "BMI & Health Check"
            )
        },
        navBarData = null
    ) {
        AdvertisementScreenContent(uiState)
    }
    if (blockUiState?.showLoader == true) {
        CustomLoader()
    }
    /*if (advertisementScreen != null) {
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
        LaunchedEffect(lifecycleState) {
            when (lifecycleState) {
                Lifecycle.State.RESUMED -> {
                    uiState.event(AdvertisementUiEvent.AdvertisementAPICall)
                }

                else -> {}
            }
        }
    }*/
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvertisementScreenContent(uiState: AdvertisementUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val advertisementList = uiState.advertisementList.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            advertisementList.refresh() // Explicitly refresh the LazyPagingItems
            state.endRefresh()
        }
    }

    Box( // Main full-screen container
        modifier = Modifier
            .fillMaxSize()
            .clickable { keyboardController?.hide() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(state.nestedScrollConnection)
            ) {
                advertisementList.loadState.refresh.apply {
                    when (this) {
                        is LoadState.Error -> TapHereRefreshContent()
                        is LoadState.Loading -> CustomLoader()
                        is LoadState.NotLoading -> {
                            if (advertisementList.itemCount == 0) {
                                NoDataFoundContent(stringResource(R.string.no_advertisement_found))
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(15.dp),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 80.dp)
                                ) {
                                    items(advertisementList.itemCount) { index ->
                                        advertisementList[index]?.let { item ->
                                            AdvertisementItem(item, onEditAdvertisement = {
                                                uiState.event(AdvertisementUiEvent.EditAdvertisement(item))

                                            })
                                        }
                                    }
                                    when (advertisementList.loadState.append) {
                                        is LoadState.Error -> item {
                                            TapHereRefreshContent(onClick = { advertisementList.retry() })
                                        }

                                        LoadState.Loading -> item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 16.dp),
                                                contentAlignment = Alignment.TopCenter
                                            ) {
                                                CircularProgressIndicator(color = White)
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

        // ✅ Floating "Add" button placed outside scrollable area, pinned to bottom-end
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 20.dp, bottom = 20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        uiState.event(AdvertisementUiEvent.NavigateAddAdvertisement)
                    }
            )
        }
    }
}

@Composable
fun AdvertisementItem(item: AdvertisementResponse, onEditAdvertisement: () -> Unit = {}) {

    val status = when (item.isAdminApproveStatus) {
        0 -> "● Pending"
        1 -> {
            when (item.advertisementStatus) {
                1 -> "● Pending"
                2 -> "● Live"
                else -> {
                    "● Completed"
                }
            }
        }

        else -> {
            "● Rejected"
        }
    }
    val statusColors = when (item.isAdminApproveStatus) {
        0 -> YellowDF
        1 -> {
            when (item.advertisementStatus) {
                1 -> YellowDF
                2 -> Green2C
                else -> {
                    Blue05
                }
            }
        }

        else -> {
            RedC9
        }
    }
    var isLoading by remember { mutableStateOf(true) }
    /* val statusBorder = when (item.isAdminApproveStatus) {
         0 -> YellowDF
         1 -> Green2C
         else -> {
             Blue05
         }
     }*/
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(170.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = null,
                onState = {
                    isLoading = it is AsyncImagePainter.State.Loading
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
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
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd) // 👈 Force top-right alignment
                    .padding(top = 12.dp, end = 12.dp), // 👈 Single top/right padding
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.isAdminApproveStatus == 2) {
                    Surface(
                        color = Black,
                        shape = RoundedCornerShape(50.dp),
                        onClick = onEditAdvertisement,
                    ) {
                        Text(
                            text = "Edit",
                            fontSize = 12.sp,
                            color = White,
                            fontFamily = WorkSans,
                            fontWeight = FontWeight.W600,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                }

                Surface(
                    color = statusColors,
                    shape = RoundedCornerShape(50.dp),
                ) {
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        color = White,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TribeListContentPreview() {
    val item = AdvertisementResponse()
    AdvertisementItem(item)

}






