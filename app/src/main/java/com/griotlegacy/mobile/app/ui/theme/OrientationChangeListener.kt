package com.griotlegacy.mobile.app.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun OrientationChangeListener(onOrientationChange: (Boolean) -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val currentOnOrientationChange by rememberUpdatedState(onOrientationChange)

    LaunchedEffect(isLandscape) {
        currentOnOrientationChange(isLandscape)
    }
}