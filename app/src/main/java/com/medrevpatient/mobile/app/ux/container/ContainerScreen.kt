package com.medrevpatient.mobile.app.ux.container

import android.app.Activity
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
import com.medrevpatient.mobile.app.navigation.graph.AppContainerGraph
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.utils.ext.requireActivity

@Composable
fun ContainerScreen(
    viewModel: ContainerViewModel = hiltViewModel(LocalContext.current.requireActivity()),
    startDestination: String,
    postId:String,
    messageResponse: String,
    userId: String,
    url: String
) {
    val navController = rememberNavController()


    AppContainerGraph(
        navController = navController,
        startDestination = startDestination,
        postId = postId,
        screenName = startDestination,
        messageResponse = messageResponse,
        userId = userId,
        url = url
    )
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}