package com.medrevpatient.mobile.app.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.medrevpatient.mobile.app.ui.theme.black25

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    state: PullToRefreshState = rememberPullToRefreshState(),
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    PullToRefreshBox(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = Color.White,
                color = black25,
                state = state
            )
        },
        content = content
    )
}


