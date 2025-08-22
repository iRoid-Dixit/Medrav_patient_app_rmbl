package com.medrevpatient.mobile.app.ux.startup.auth.splash

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.ui.compose.common.dialog.AppUpdateDialog
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.BgColor

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
    restartApp: String,
    bundle: Bundle
) {
    val uiState = viewModel.splashUiState
    val context = LocalContext.current
    LaunchedEffect(restartApp, bundle) {
        uiState.event(SplashUiEvent.GetIntentData(bundle))
        uiState.event(SplashUiEvent.RestartAppKey(restartApp))
    }
    uiState.event(SplashUiEvent.GetContext(context))
    MedrevPatientTheme {
        SplashScreenContent(uiState = uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun SplashScreenContent(uiState: SplashUiState) {
    val splashUiData by uiState.splashUiDataFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(
                brush = Brush.linearGradient(
                    colors = BgColor,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1f, 1f)
                )
            )
    ) {
        // Center image (existing)
        Image(
            painterResource(id = R.drawable.ic_app_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center).
                padding(bottom = 80.dp),

            )

        // Bottom-start image (hexagonal medical icons)
        Image(
            painterResource(id = R.drawable.ic_medical_icons),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)

        )
    }

    if (splashUiData?.showAppUpdateDialog == true) {
        AppUpdateDialog(
            isForceUpdate = splashUiData?.isForceUpdate == true,
            appUpdateMessage = splashUiData?.appMessage ?: "Required to update the app for better performance.",
            onClickOfCancel = {
                uiState.event(SplashUiEvent.OnClickOfCancel)
            },
            onCLickOfUpdate = {
                uiState.event(SplashUiEvent.OnClickOfUpdate(appContext = context))
            }
        )
    }
}
@Preview
@Composable
private fun Preview() {
    val uiState = SplashUiState()
    Surface {
        SplashScreenContent(uiState)
    }
}