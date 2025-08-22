package com.griotlegacy.mobile.app.ux.main

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.griotlegacy.mobile.app.navigation.HandleNavBarNavigation
import com.griotlegacy.mobile.app.navigation.graph.AppMainGraph
import com.griotlegacy.mobile.app.navigation.scaffold.AppNavBarData
import com.griotlegacy.mobile.app.navigation.scaffold.AppNavBarType
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.utils.ext.requireActivity
import com.griotlegacy.mobile.app.ux.main.bottombar.CustomBottomNavigationBar
import com.griotlegacy.mobile.app.ux.main.griotLegacy.GriotLegacyRoute

@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(LocalContext.current.requireActivity()),
    startDestination: String,
) {
    val navController = rememberNavController()
    val systemUi = rememberSystemUiController()
    val currBackStackState by navController.currentBackStackEntryAsState()
    val currDestination = currBackStackState?.destination
    val context = LocalContext.current
    AppScaffold(
        modifier = Modifier.background(
            color = AppThemeColor
        ),
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                CustomBottomNavigationBar(
                    currDestination = currDestination,
                    onNavItemClicked = { viewModel.onNavBarItemSelected(it) },
                    floatingButtonClick = {
                        navController.navigate(GriotLegacyRoute.routeDefinition.value)
                    }
                )
            }
        )
    ) {

    val window = (context as Activity).window
        LaunchedEffect(Unit) {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, window.decorView).apply {
                show(WindowInsetsCompat.Type.statusBars())
                show(WindowInsetsCompat.Type.navigationBars())
            }
            systemUi.setSystemBarsColor(
                color = Color.Black,
                darkIcons = false
            )
            systemUi.setStatusBarColor(
                color = Color.Black,
                darkIcons = false
            )
        }
        AppMainGraph(navController = navController, startDestination)
    }
    HandleNavBarNavigation(viewModelNavBar = viewModel, navController = navController)


}