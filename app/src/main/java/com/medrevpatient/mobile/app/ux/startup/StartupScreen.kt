package com.medrevpatient.mobile.app.ux.startup

import android.app.Activity
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.graph.AppStartUpGraph
import com.medrevpatient.mobile.app.utils.ext.requireActivity

@Composable
fun StartupScreen(
    viewModel: StartupViewModel = hiltViewModel(LocalContext.current.requireActivity()),
    startDestination: String,
    restartApp: String,
    bundle: Bundle
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val systemUi = rememberSystemUiController()
    val window = (context as Activity).window

    LaunchedEffect(Unit) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.statusBars())
            show(WindowInsetsCompat.Type.navigationBars())
        }
        systemUi.setSystemBarsColor(
            color = Color.White, // ✅ Use standard `Color.Black`
            darkIcons = true // ✅ Ensures white icons on dark background
        )
        systemUi.setStatusBarColor(
            color = Color.White, // ✅ Use standard `Color.Black`
            darkIcons = true
        )
    }
    AppStartUpGraph(
        navController = navController,
        startDestination = startDestination,
        restartApp = restartApp,
        bundle = bundle
    )
    HandleNavigation(viewModelNav = viewModel, navController = navController)

}