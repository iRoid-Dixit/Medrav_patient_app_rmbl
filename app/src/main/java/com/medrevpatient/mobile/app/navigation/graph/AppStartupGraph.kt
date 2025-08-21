package com.medrevpatient.mobile.app.navigation.graph


import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.SimpleNavComposeRoute
import com.medrevpatient.mobile.app.ux.startup.auth.signin.SignInScreen
import com.medrevpatient.mobile.app.ux.startup.onboarding.OnboardingScreen
import com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow.OnBoardDataScreen
import com.medrevpatient.mobile.app.ux.startup.onboarding.splash.SplashScreen
import com.medrevpatient.mobile.app.ux.startup.signup.SignupScreen
import com.medrevpatient.mobile.app.ux.startup.subscription.SubsScreen

@Composable
fun AppStartUpGraph(
    navController: NavHostController,
    startDestination: String,
    bundle: Bundle?,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        (RouteMaker.SplashRoute as SimpleNavComposeRoute).addNavigationRoute(this) {
            SplashScreen(navController,bundle)
        }
        RouteMaker.OnboardingRoute.addNavigationRoute(this) { OnboardingScreen(navController) }
        RouteMaker.SignupRoute.addNavigationRoute(this) { SignupScreen(navController) }
        RouteMaker.SignInRoute.addNavigationRoute(this) { SignInScreen(navController) }
        RouteMaker.SubsRoute.addNavigationRoute(this) { SubsScreen(navController) }
        RouteMaker.OnBoardDataRoute.addNavigationRoute(this) { OnBoardDataScreen(navController) }

    }
}