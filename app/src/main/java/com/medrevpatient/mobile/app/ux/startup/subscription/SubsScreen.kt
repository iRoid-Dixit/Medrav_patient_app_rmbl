package com.medrevpatient.mobile.app.ux.startup.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.ui.common.BottomButtonComponent
import com.medrevpatient.mobile.app.ui.common.TitleDualFont
import com.medrevpatient.mobile.app.ui.theme.ColorOsloGray
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown16
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MineShaft4
import com.medrevpatient.mobile.app.ui.theme.MineShaft45
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.black
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ui.theme.white16
import com.medrevpatient.mobile.app.utils.ext.requireActivity

@Composable
fun SubsScreen(
    navController: NavController,
    viewModel: SubsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    MedrevPatientTheme {
        WindowCompat.setDecorFitsSystemWindows(LocalContext.current.requireActivity().window, false)
        SubscriptionScreenContent(uiState, uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)

}

@Composable
private fun SubscriptionScreenContent(uiStateFlow: SubsUiStateFlow, event: (SubscriptionUiEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.on_board_one), contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            black,
                            black
                        )
                    )
                ), contentAlignment = Alignment.BottomStart
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 40.dp),
                        shape = RoundedCornerShape(30.dp),
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(0.8f)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                white,
                                                ColorSwansDown
                                            )
                                        )
                                    )
                            )
                        }
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp)
                            .padding(horizontal = 10.dp),
                        shape = RoundedCornerShape(30.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    ) {
                        CardContent(uiStateFlow)
                    }
                }
                MonthlyYearlyButton(uiStateFlow, event)
                Text(
                    text = stringResource(id = R.string.commited_to_a_stronger_you),
                    color = white,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outFit,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = stringResource(id = R.string.unblock_your_potential),
                    color = ColorOsloGray,
                    fontWeight = FontWeight.Light,
                    fontFamily = outFit,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                BottomButtonComponent(
                    text = stringResource(id = R.string.start_my_free_7_days).uppercase(),
                    onClick = { event(SubscriptionUiEvent.StartFreeTrial) },
                    buttonColors = ButtonDefaults.buttonColors(
                        containerColor = white
                    ),
                    textColor = MineShaft,
                    textStyle = TextStyle(
                        letterSpacing = 0.1.em,
                        shadow = Shadow(
                            color = MineShaft45,
                            offset = Offset(5.0f, 13.0f),
                            blurRadius = 15f
                        )
                    ),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
                Text(
                    text = stringResource(id = R.string.if_canceled_charges_will_begin_on),
                    color = white,
                    fontWeight = FontWeight.Light,
                    fontFamily = outFit,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 56.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

    }
}

@Composable
private fun CardContent(uiStateFlow: SubsUiStateFlow) {
    val isMonthlySelected by uiStateFlow.isMonthlySelected.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        white,
                        ColorSwansDown
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_crown), contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MineShaft,
                            MineShaft,
                            Color.Transparent
                        )
                    )
                ),
            color = Color.Transparent
        )
        TitleDualFont(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            color = MineShaft,
            fontWeightBold = FontWeight.Bold,
            fontWeightRegular = FontWeight.Bold,
            fontSize = 34,
            fontSize2 = 10,
            fontFamilyBold = outFit,
            fontFamilyRegular = outFit,
            titlePart1 = if (isMonthlySelected) stringResource(id = R.string.month_value) else stringResource(id = R.string.year_value),
            titlePart2 = if (isMonthlySelected) stringResource(id = R.string.month_value_txt) else stringResource(id = R.string.year_value_txt)
        )
        Text(
            text = stringResource(id = R.string.free_trial).uppercase(),
            color = MineShaft,
            fontWeight = FontWeight.Normal,
            fontFamily = outFit,
            fontSize = 12.sp,
            style = TextStyle(
                letterSpacing = 0.4.em
            ),
            modifier = Modifier
                .padding(top = 5.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.what_s_included_in_your_subscription),
            color = MineShaft,
            fontWeight = FontWeight.Bold,
            fontFamily = outFit,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 15.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(10.dp))
        val subscriptionList = stringArrayResource(id = R.array.subscription_array)
        subscriptionList.forEach {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_checkbox_selected), contentDescription = "",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = it,
                    color = MineShaft,
                    fontWeight = FontWeight.Light,
                    fontFamily = outFit,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
private fun MonthlyYearlyButton(uiStateFlow: SubsUiStateFlow, event: (SubscriptionUiEvent) -> Unit) {
    val isMonthlySelected by uiStateFlow.isMonthlySelected.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        border = BorderStroke(
            1.dp, brush = Brush.horizontalGradient(
                colors = listOf(
                    white,
                    ColorSwansDown
                )
            )
        ),
    ) {
        Row(modifier = Modifier.padding(3.dp)) {
            Box(
                modifier = Modifier
                    .clip(if (isMonthlySelected) RoundedCornerShape(50) else RoundedCornerShape(0))
                    .background(
                        brush = if (isMonthlySelected) Brush.horizontalGradient(
                            colors = listOf(
                                white,
                                ColorSwansDown
                            )
                        ) else Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    )
                    .clickable {
                        event(SubscriptionUiEvent.PerformSubscriptionTypeClick(true))
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.monthly),
                    color = if (isMonthlySelected) MineShaft else white,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outFit,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clip(if (!isMonthlySelected) RoundedCornerShape(50) else RoundedCornerShape(0))
                    .background(
                        brush = if (!isMonthlySelected) Brush.horizontalGradient(
                            colors = listOf(
                                white,
                                ColorSwansDown
                            )
                        ) else Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    )
                    .clickable {
                        event(SubscriptionUiEvent.PerformSubscriptionTypeClick(false))
                    }
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.yearly),
                        color = if (!isMonthlySelected) MineShaft else white,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outFit,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .align(Alignment.CenterVertically)
                            .padding(end = 10.dp)
                            .background(
                                brush = if (isMonthlySelected) Brush.horizontalGradient(
                                    colors = listOf(
                                        white16,
                                        ColorSwansDown16
                                    )
                                ) else Brush.horizontalGradient(
                                    colors = listOf(
                                        MineShaft4,
                                        MineShaft4
                                    )
                                ), shape = RoundedCornerShape(50)
                            )
                            .border(
                                BorderStroke(
                                    1.dp, brush = if (isMonthlySelected) Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            white
                                        )
                                    ) else Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MineShaft
                                        )
                                    )
                                ), shape = RoundedCornerShape(50)
                            ),
                        content = {
                            Text(
                                text = stringResource(id = R.string.save_41),
                                color = if (isMonthlySelected) white else MineShaft,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = outFit,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 10.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        SubscriptionScreenContent(uiStateFlow = SubsUiStateFlow(), event = {})
    }
}
