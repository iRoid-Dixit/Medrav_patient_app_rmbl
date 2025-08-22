@file:OptIn(ExperimentalMaterial3Api::class)
package com.griotlegacy.mobile.app.navigation.graph
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.ux.container.about.AboutRoute
import com.griotlegacy.mobile.app.ux.main.griotLegacy.GriotLegacyRoute
import com.griotlegacy.mobile.app.ux.main.griotLegacy.GriotLegacyScreen
import com.griotlegacy.mobile.app.ux.main.home.HomeRoute
import com.griotlegacy.mobile.app.ux.main.home.HomeScreen
import com.griotlegacy.mobile.app.ux.main.message.MessageRoute
import com.griotlegacy.mobile.app.ux.main.message.MessageScreen
import com.griotlegacy.mobile.app.ux.main.search.SearchRoute
import com.griotlegacy.mobile.app.ux.main.search.SearchScreen
import com.griotlegacy.mobile.app.ux.main.setting.SettingRoute
import com.griotlegacy.mobile.app.ux.main.setting.SettingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainGraph(
    navController: NavHostController,
    startDestination: String,

) {
    val appStartDestination = when (startDestination) {
        Constants.AppScreen.ABOUT_US -> {
            AboutRoute.routeDefinition.value
        }

        Constants.AppScreen.GRIOT_LEGACY_SCREEN -> {
            GriotLegacyRoute.routeDefinition.value
        }
        Constants.AppScreen.SEARCH_SCREEN -> {
            SearchRoute.routeDefinition.value
        }

        Constants.AppScreen.MAIN_VILLAGE_SCREEN -> {
            HomeRoute.routeDefinition.value
        }
        Constants.AppScreen.MESSAGE_SCREEN -> {
            MessageRoute.routeDefinition.value
        }

        else -> {
            GriotLegacyRoute.routeDefinition.value
        }
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        HomeRoute.addNavigationRoute(this) { HomeScreen(navController) }
        SearchRoute.addNavigationRoute(this){ SearchScreen(navController) }
        MessageRoute.addNavigationRoute(this){ MessageScreen(navController)}
        SettingRoute.addNavigationRoute(this){ SettingScreen(navController) }
        GriotLegacyRoute.addNavigationRoute(this){ GriotLegacyScreen(navController) }
    }
}