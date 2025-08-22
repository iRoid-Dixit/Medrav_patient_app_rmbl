package com.griotlegacy.mobile.app.ux.container.myCircle.circleType

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.Black50
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ux.container.myCircle.MyCircleUiEvent
import com.griotlegacy.mobile.app.ux.container.myCircle.MyCircleUiState

@ExperimentalMaterial3Api
@Composable
fun InnerCircleScreen(
    uiState: MyCircleUiState
) {
    val context = LocalContext.current
    val changePasswordUiState by uiState.myCircleDataFlow.collectAsStateWithLifecycle()
    uiState.event(MyCircleUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {},
        navBarData = null
    ) {
        MyCircleScreenContent(uiState, uiState.event)
    }

    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyCircleScreenContent(
    uiState: MyCircleUiState,
    event: (MyCircleUiEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val innerCircleListFlow = uiState.innerCircleListFlow.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState(positionalThreshold = 120.dp)
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            innerCircleListFlow.refresh() // Explicitly refresh the LazyPagingItems
            state.endRefresh()
        }
    }
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        val lazyColumnListState = rememberLazyListState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(state.nestedScrollConnection)
        ) {
            innerCircleListFlow.loadState.refresh.apply {
                when (this) {
                    is LoadState.Error -> {
                        TapHereRefreshContent()
                    }

                    is LoadState.Loading -> {
                        CustomLoader()
                    }

                    is LoadState.NotLoading -> {
                        if (innerCircleListFlow.itemCount == 0) {
                            NoDataFoundContent(text = "No InnerCircle List found")
                        } else {
                            LazyColumn(
                                state = lazyColumnListState,
                                contentPadding = PaddingValues(
                                    vertical = 10.dp,
                                    horizontal = 15.dp
                                ),
                                modifier = Modifier.fillMaxSize()
                            )
                            {
                                items(innerCircleListFlow.itemCount) { index ->
                                    innerCircleListFlow[index]?.let { item ->
                                        ItemTribe(item, uiState = uiState)
                                        if (index != innerCircleListFlow.itemCount - 1) HorizontalDivider(
                                            thickness = 0.5.dp,
                                            color = Black50,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }
                                when (innerCircleListFlow.loadState.append) {
                                    is LoadState.Error -> {
                                        item {
                                            TapHereRefreshContent()
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
}

@Preview
@Composable
fun InnerCircleContentPreview() {
    val uiState = MyCircleUiState()
    MyCircleScreenContent(uiState = uiState, event = uiState.event)

}






