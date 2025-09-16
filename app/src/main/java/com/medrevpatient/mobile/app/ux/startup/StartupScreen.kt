package com.medrevpatient.mobile.app.ux.startup
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
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
    AppStartUpGraph(
        navController = navController,
        startDestination = startDestination,
        restartApp = restartApp,
        bundle = bundle
    )
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}