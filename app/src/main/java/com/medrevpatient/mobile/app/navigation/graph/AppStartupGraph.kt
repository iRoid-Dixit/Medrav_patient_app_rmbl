package com.medrevpatient.mobile.app.navigation.graph
import android.os.Bundle
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.navigation.SimpleNavComposeRoute
import com.medrevpatient.mobile.app.ux.startup.auth.login.LoginRoute
import com.medrevpatient.mobile.app.ux.startup.auth.login.LoginScreen
import com.medrevpatient.mobile.app.ux.startup.auth.splash.SplashRoute
import com.medrevpatient.mobile.app.ux.startup.auth.splash.SplashScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppStartUpGraph(
    navController: NavHostController,
    startDestination: String,
    restartApp: String,
    bundle: Bundle
) {
    Log.d("TAG", "AppStartUpGraph: $startDestination")
    val appStartDestination = when (startDestination) {
        Constants.AppScreen.START_UP -> {
            SplashRoute.routeDefinition.value
        }
        Constants.AppScreen.SIGN_IN -> {
            LoginRoute.routeDefinition.value
        }

        else -> {
            SplashRoute.routeDefinition.value
        }
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        (SplashRoute as SimpleNavComposeRoute).addNavigationRoute(this) { SplashScreen(navController, restartApp = restartApp, bundle = bundle) }
        LoginRoute.addNavigationRoute(this) { LoginScreen(navController) }

    }
}