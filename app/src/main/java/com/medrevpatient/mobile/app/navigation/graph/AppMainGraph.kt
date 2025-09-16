@file:OptIn(ExperimentalMaterial3Api::class)
package com.medrevpatient.mobile.app.navigation.graph
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.ux.MedicationRemindersScreen

import com.medrevpatient.mobile.app.ux.container.about.AboutRoute
import com.medrevpatient.mobile.app.ux.main.home.HomeRoute
import com.medrevpatient.mobile.app.ux.main.home.HomeScreen
import com.medrevpatient.mobile.app.ux.main.medication.MedicationRoute
import com.medrevpatient.mobile.app.ux.main.appointment.AppointmentRoute
import com.medrevpatient.mobile.app.ux.main.appointment.AppointmentScreen
import com.medrevpatient.mobile.app.ux.main.message.MessageRoute
import com.medrevpatient.mobile.app.ux.main.message.MessageScreen
import com.medrevpatient.mobile.app.ux.main.profile.ProfileRoute
import com.medrevpatient.mobile.app.ux.main.profile.ProfileScreen

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
        Constants.AppScreen.SEARCH_SCREEN -> {
            AppointmentRoute.routeDefinition.value
        }
        Constants.AppScreen.MAIN_VILLAGE_SCREEN -> {
            HomeRoute.routeDefinition.value
        }
        Constants.AppScreen.MESSAGE_SCREEN -> {
            MedicationRoute.routeDefinition.value
        }
        Constants.AppScreen.HOME_SCREEN -> {
            HomeRoute.routeDefinition.value
        }
        else -> {
            HomeRoute.routeDefinition.value
        }
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        HomeRoute.addNavigationRoute(this) { HomeScreen(navController) }
        AppointmentRoute.addNavigationRoute(this){ AppointmentScreen(navController) }
        MedicationRoute.addNavigationRoute(this){ MedicationRemindersScreen(navController)}
        MessageRoute.addNavigationRoute(this){ MessageScreen(navController) }
        ProfileRoute.addNavigationRoute(this){ ProfileScreen(navController) }
    }
}