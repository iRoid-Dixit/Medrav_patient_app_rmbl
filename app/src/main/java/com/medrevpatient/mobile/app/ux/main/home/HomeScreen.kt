package com.medrevpatient.mobile.app.ux.main.home
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
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.Media
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.CustomDropdownMenu
import com.medrevpatient.mobile.app.ui.compose.common.HorizontalPagerIndicator
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White20
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
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
    AppScaffold(
        containerColor = White,
        topAppBar = {

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


}

