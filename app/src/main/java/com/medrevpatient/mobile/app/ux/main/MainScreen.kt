package com.medrevpatient.mobile.app.ux.main

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.medrevpatient.mobile.app.navigation.HandleNavBarNavigation
import com.medrevpatient.mobile.app.navigation.graph.AppMainGraph
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.utils.ext.requireActivity
import com.medrevpatient.mobile.app.ux.main.bottombar.AppNavigationBar
import com.medrevpatient.mobile.app.ux.main.griotLegacy.GriotLegacyRoute

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
        containerColor = White,
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                AppNavigationBar(
                    currDestination = currDestination,
                    onNavItemClicked = { viewModel.onNavBarItemSelected(it) },
                )
            }
        )
    ) {
        AppMainGraph(navController = navController, startDestination)
    }
    HandleNavBarNavigation(viewModelNavBar = viewModel, navController = navController)


}