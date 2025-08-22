package com.griotlegacy.mobile.app.navigation.graph

import android.os.Bundle
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.navigation.SimpleNavComposeRoute
import com.griotlegacy.mobile.app.ux.startup.auth.forgetPassword.ForgetPasswordRoute
import com.griotlegacy.mobile.app.ux.startup.auth.forgetPassword.ForgetPasswordScreen
import com.griotlegacy.mobile.app.ux.startup.auth.login.LoginRoute
import com.griotlegacy.mobile.app.ux.startup.auth.login.LoginScreen
import com.griotlegacy.mobile.app.ux.startup.auth.register.RegisterRoute
import com.griotlegacy.mobile.app.ux.startup.auth.register.RegisterScreen
import com.griotlegacy.mobile.app.ux.startup.auth.resetPassword.ResetPasswordRoute
import com.griotlegacy.mobile.app.ux.startup.auth.resetPassword.ResetPasswordScreen
import com.griotlegacy.mobile.app.ux.startup.auth.splash.SplashRoute
import com.griotlegacy.mobile.app.ux.startup.auth.splash.SplashScreen
import com.griotlegacy.mobile.app.ux.startup.auth.verifyOtp.VerifyOtpRoute
import com.griotlegacy.mobile.app.ux.startup.auth.verifyOtp.VerifyOtpScreen


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
        Constants.AppScreen.REGISTER_SCREEN -> {
            RegisterRoute.routeDefinition.value
        }
        else -> {
            SplashRoute.routeDefinition.value
        }
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        (SplashRoute as SimpleNavComposeRoute).addNavigationRoute(this) {
            SplashScreen(
                navController,
                restartApp = restartApp,
                bundle = bundle
            )
        }
        LoginRoute.addNavigationRoute(this) { LoginScreen(navController) }
        RegisterRoute.addNavigationRoute(this) { RegisterScreen(navController) }
        ForgetPasswordRoute.addNavigationRoute(this) { ForgetPasswordScreen(navController) }
        VerifyOtpRoute.addNavigationRoute(this) { VerifyOtpScreen(navController) }
        ResetPasswordRoute.addNavigationRoute(this) { ResetPasswordScreen(navController) }
    }
}