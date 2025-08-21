package com.medrevpatient.mobile.app.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.ui.theme.black25
import timber.log.Timber

@Composable
fun <T> NetworkResultHandler(
    modifier: Modifier = Modifier,
    networkResult: NetworkResult<T>,
    barTitle: String? = null,
    onBackPress: (() -> Unit)? = null,
    onTrailingPress: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    loadingFier: Modifier = Modifier,
    errorFier: Modifier = Modifier,
    onError: @Composable (String) -> Unit = { message ->
        FullSizeCenterBox(modifier = errorFier) {
            barTitle?.let {
                TopBarCenterAlignTextAndBack(
                    title = barTitle,
                    onBackPress = onBackPress,
                    onTrailingPress = onTrailingPress,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                )
            }
            VStack(0.dp) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelLarge,
                )
                onRetry?.let {
                    TextButton(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = black25,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "Retry",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    },

    onLoading: @Composable () -> Unit = {
        SimpleLoader(modifier = loadingFier.fillMaxSize())
    },

    onSuccess: @Composable (T) -> Unit,

    ) {
    Crossfade(
        modifier = modifier,
        targetState = networkResult,
        label = "NetworkResultHandler"
    ) {
        when (it) {
            is NetworkResult.Loading -> onLoading()
            is NetworkResult.Success -> networkResult.data?.let { data -> onSuccess(data) }
            is NetworkResult.Error -> onError(networkResult.message ?: "Something went wrong!")
        }
    }
}


@Composable
fun <T : Any> PagingResultHandler(
    lazyPagingState: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    onError: @Composable (String) -> Unit = { message ->
        Timber.d(message)
        FullSizeCenterBox {

            VStack(0.dp, modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelLarge
                )
                TextButton(
                    onClick = { lazyPagingState.retry() }
                ) {
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }
    },

    onLoading: @Composable () -> Unit = {
        SimpleLoader(
            modifier = Modifier
                .heightIn(193.dp)
                .fillMaxSize()
        )
    },

    onSuccess: @Composable (LazyPagingItems<T>) -> Unit,

    ) {

    Crossfade(
        modifier = modifier,
        targetState = lazyPagingState.loadState.refresh,
        label = "NetworkResultHandler"
    ) { pagingState ->
        when (pagingState) {

            is LoadState.Error -> onError(
                pagingState.error.localizedMessage ?: "Something went wrong!"
            )

            LoadState.Loading -> onLoading()

            is LoadState.NotLoading -> onSuccess(lazyPagingState)
        }
    }
}
