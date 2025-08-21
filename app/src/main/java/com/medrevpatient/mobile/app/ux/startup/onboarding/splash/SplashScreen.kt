package com.medrevpatient.mobile.app.ux.startup.onboarding.splash

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.ui.common.TitleDualFont
import com.medrevpatient.mobile.app.ui.theme.AppThemeBlue
import com.medrevpatient.mobile.app.ui.theme.ColorBubbles
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.utils.ext.requireActivity

@Composable
fun SplashScreen(
    navController: NavController,
    bundle: Bundle?,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState = viewModel.splashUiState
    MedrevPatientTheme {
        WindowCompat.setDecorFitsSystemWindows(LocalContext.current.requireActivity().window, false)
        uiState.notificationKey(bundle ?: Bundle.EMPTY)
        SplashScreenContent()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White,
                        Color.White,
                        ColorBubbles
                    )
                )
            )
    ) {
        TitleDualFont(
            modifier = Modifier.align(Alignment.Center),
            color = AppThemeBlue,
            fontWeightBold = FontWeight.Bold,
            fontWeightRegular = FontWeight.Light,
            fontSize = 30,
            fontFamilyBold = outFit,
            fontFamilyRegular = outFit,
            titlePart1 = stringResource(id = R.string.skai),
            titlePart2 = stringResource(id = R.string.fitness)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        SplashScreenContent()
    }
}