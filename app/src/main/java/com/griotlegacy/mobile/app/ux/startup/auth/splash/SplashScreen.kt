package com.griotlegacy.mobile.app.ux.startup.auth.splash

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.navigation.HandleNavigation
import com.griotlegacy.mobile.app.ui.compose.common.dialog.AppUpdateDialog
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.GriotLegacyTheme
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
    GriotLegacyTheme {
        SplashScreenContent(uiState = uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun SplashScreenContent(uiState: SplashUiState) {
    val splashUiData by uiState.splashUiDataFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppThemeColor)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = R.drawable.ic_app_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(270.dp)
                    .align(Alignment.Center)
            )
        }
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