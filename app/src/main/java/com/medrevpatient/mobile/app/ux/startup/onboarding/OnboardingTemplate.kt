package com.medrevpatient.mobile.app.ux.startup.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeBlue
import com.medrevpatient.mobile.app.ui.theme.black
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ui.theme.white10

@Composable
fun OnboardingTemplate(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onSkipClick: () -> Unit,
    currentStep: Int,
    totalSteps: Int = 7,
    nextOrStartText: String = stringResource(R.string.next)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .paint(painterResource(id = R.drawable.on_board_two), contentScale = ContentScale.Crop),
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        // Step Indicators
        Row(modifier = Modifier.fillMaxWidth()) {
            StepIndicators(currentStep = currentStep, totalSteps = totalSteps)
            Spacer(modifier = Modifier.weight(1f))
            //if (currentStep == 0) {
            Text(
                text = stringResource(R.string.skip),
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = outFit,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(end = 18.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onSkipClick() },
                style = TextStyle(
                    textDecoration = TextDecoration.Underline
                )
            )
            //}
        }

        //Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
            // Header
            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
            ) {
                Text(text = title, fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.ExtraBold, fontFamily = outFit)
                Text(
                    text = subtitle, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Light, fontFamily = outFit,
                    modifier = Modifier.padding(top = 5.dp),
                    style = TextStyle(
                        lineHeight = 1.1.em,
                    )
                )

                // Content
                content()

                // Navigation Buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, bottom = 56.dp),
                    verticalAlignment = Alignment.Bottom
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Back",
                        modifier = Modifier
                            .size(53.dp)
                            .clickable {
                                onPrevClick()
                            }
                    )

                    Surface(
                        onClick = onNextClick,
                        shape = RoundedCornerShape(50),
                        color = white10,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = nextOrStartText, color = white, fontFamily = outFit,
                                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_arrow_ahead_white_bg), contentDescription = "Next",
                                modifier = Modifier.size(45.dp)
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun StepIndicators(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        for (step in 0 until totalSteps) {
            if (step == currentStep) {
                Surface(
                    content = {},
                    shape = RoundedCornerShape(60),
                    color = AppThemeBlue,
                    modifier = Modifier
                        .width(19.dp)
                        .height(6.dp)
                )
            } else {
                Surface(
                    content = {},
                    shape = RoundedCornerShape(100),
                    color = if (currentStep >= 3) AppThemeBlue.copy(alpha = 0.3f) else AppThemeBlue.copy(alpha = 0.1f * (totalSteps - step)),
                    modifier = Modifier
                        .width(8.dp)
                        .height(6.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewOnboardingTemplate() {
    OnboardingTemplate(
        title = "Age",
        subtitle = "Please share more information\nabout yourself.",
        content = {
            Column(modifier = Modifier.height(310.dp)) {
                Text("Content goes here", color = Color.White)
            }
        },
        onNextClick = {},
        onPrevClick = {},
        onSkipClick = {},
        currentStep = 0,
        totalSteps = 7
    )
}

