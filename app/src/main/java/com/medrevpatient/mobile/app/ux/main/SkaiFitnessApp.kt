package com.medrevpatient.mobile.app.ux.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.medrevpatient.mobile.app.navigation.HandleNavBarNavigation
import com.medrevpatient.mobile.app.navigation.NavBarItem
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationBar
import com.medrevpatient.mobile.app.navigation.graph.MainGraph
import com.medrevpatient.mobile.app.ui.theme.white

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    startDestination: String = "",
    goalId: String = ""
) {
    val navController = rememberNavController()
    val currBackStackState by navController.currentBackStackEntryAsState()
    val currDestination = currBackStackState?.destination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = white,
        bottomBar = {
            AnimatedVisibility(
                NavBarItem.getNavBarItemRouteMap().values.contains(
                    NavRoute(currDestination?.route.toString())
                ),
                enter = fadeIn(nonSpatialExpressiveSpring()) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spatialExpressiveSpring(),
                ),
                exit = fadeOut(nonSpatialExpressiveSpring()) + slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = spatialExpressiveSpring(),
                )
            ) {
                NavigationBar(
                    navController = navController,
                    onNavItemClicked = { viewModel.onNavBarItemSelected(it) },
                    containerColor = Color.White
                )
            }
        }
    ) { innerPadding ->
        MainGraph(
            navController = navController,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            startDestination = startDestination,
            goalId = goalId

        )
    }
    HandleNavBarNavigation(viewModelNavBar = viewModel, navController = navController)
}


fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)