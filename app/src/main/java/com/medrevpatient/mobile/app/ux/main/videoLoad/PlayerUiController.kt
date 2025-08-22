package com.medrevpatient.mobile.app.ux.main.videoLoad

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Suppress("DEPRECATION")
@Composable
fun PlayerUiController(activity: ComponentActivity, isLandscape: Boolean) {

    val window = activity.window

    val windowInsetsController =
        remember(activity) { window?.let { WindowCompat.getInsetsController(it, it.decorView) } }

    LaunchedEffect(isLandscape) {
        windowInsetsController?.apply {
            if (isLandscape) {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                show(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }

    DisposableEffect(isLandscape) {
        windowInsetsController?.apply {
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Black.toArgb()
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }
        onDispose {
            windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

}