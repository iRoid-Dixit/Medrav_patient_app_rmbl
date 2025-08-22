package com.medrevpatient.mobile.app.ui.networkresult

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult

@Composable
fun <T> NetworkResultHandler(
    networkResult: NetworkResult<T>,
    onError: @Composable (String) -> Unit = { message ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    },
    onLoading: @Composable () -> Unit = {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    },
    onSuccess: @Composable (T) -> Unit,
    onUnAuthenticated: @Composable (String) -> Unit = { message ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
) {
    when (networkResult) {
        is NetworkResult.Loading -> onLoading()
        is NetworkResult.Success -> networkResult.data?.let { onSuccess(it) }
        is NetworkResult.Error -> onError(networkResult.message ?: "Unknown error")
        is NetworkResult.UnAuthenticated -> onUnAuthenticated(networkResult.message ?: "UnAuthenticated")
    }
}