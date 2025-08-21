package com.medrevpatient.mobile.app.ux.startup.onboarding


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.ui.theme.AppThemeBlue
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.black
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.ext.requireActivity

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    MedrevPatientTheme {
        WindowCompat.setDecorFitsSystemWindows(LocalContext.current.requireActivity().window, false)
        OnboardingScreenContent(uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun OnboardingScreenContent(uiState: OnboardingUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .navigationBarsPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.on_board_one), contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 25.dp, end = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                textAlign = TextAlign.Center,
                fontFamily = outFit,
                fontWeight = FontWeight.Light,
                fontSize = 26.sp,
                color = white,
                text = stringResource(R.string.on_board_1_txt),
                modifier = Modifier.fillMaxWidth().offset(y = 10.dp)
            )

            ButtonShadow(uiState = uiState)

            /*Surface(
                color = white,
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .shadow(
                        elevation = 15.dp,
                        ambientColor = AppThemeBlue,
                        spotColor = AppThemeBlue
                    )
                    .clickable {
                        uiState.onStartClick()
                    },
            )
            {
                Text(
                    text = stringResource(R.string.let_start),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MineShaft,
                    fontFamily = outFit,
                    modifier = Modifier.padding(horizontal = 23.dp, vertical = 15.dp),
                    textAlign = TextAlign.Center
                )
            }*/
        }

    }
}

@Composable
fun ButtonShadow(modifier: Modifier = Modifier,uiState: OnboardingUiState) {
    Box(
        modifier = modifier
            .offset(y = 10.dp)
            .fillMaxWidth()
            .height(170.dp)
            .background(Color.Transparent, RoundedCornerShape(50))
            .drawBehind {
                val width = size.width
                val height = size.height
                drawCircle(
                    alpha = 0.2f,
                    radius = width / 2f,
                    brush = Brush.radialGradient(
                        listOf(AppThemeBlue, Color.Transparent),
                        center = Offset(
                            x = width / 2f,
                            y = height / 2f
                        )
                    ),
                    center = Offset(
                        x = width / 2f,
                        y = height / 2f
                    ),
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.let_start),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MineShaft,
            fontFamily = outFit,
            modifier = Modifier
                .background(white, RoundedCornerShape(50))
                .padding(horizontal = 23.dp, vertical = 15.dp)
                .clickable {
                    uiState.onStartClick()
                },
            textAlign = TextAlign.Center
        )
    }


    /*Box(
        modifier = Modifier.background(
            color = Color.Transparent
        ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 300.dp, height = 150.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(white.copy(alpha = 0.1f), white.copy(alpha = 0.01f)),
                        center = Offset(150.dp.toPx(), 75.dp.toPx()),
                        radius = 50.dp.toPx()
                    )
                )
        )
        Text(
            text = stringResource(R.string.let_start),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MineShaft,
            fontFamily = outFit,
            modifier = Modifier
                .background(white, RoundedCornerShape(50))
                .padding(horizontal = 23.dp, vertical = 15.dp),
            textAlign = TextAlign.Center
        )
    }*/

}

@Preview
@Composable
private fun Preview() {
    Surface {
        OnboardingScreenContent(uiState = OnboardingUiState {})
    }
}