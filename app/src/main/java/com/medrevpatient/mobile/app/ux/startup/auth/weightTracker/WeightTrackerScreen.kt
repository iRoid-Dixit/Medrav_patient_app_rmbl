package com.medrevpatient.mobile.app.ux.startup.auth.weightTracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.*
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line

@ExperimentalMaterial3Api
@Composable
fun WeightTrackerScreen(
    navController: NavController,
    viewModel: WeightTrackerViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                onClick = { uiState.event(WeightTrackerUiEvent.OnBackClick) },
                isBackVisible = true,
                titleText = "Weight Tracker",
            )
        },
        navBarData = null
    ) {
        uiState.event(WeightTrackerUiEvent.GetContext(context))
        WeightTrackerScreenContent(uiState = uiState, event = uiState.event)
    }

    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun WeightTrackerScreenContent(uiState: WeightTrackerUiState, event: (WeightTrackerUiEvent) -> Unit) {
    val weightData by uiState.bmiDataFlow.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .noRippleClickable {
                keyboardController?.hide()
            }
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CurrentWeightSection(
            weightData = weightData,
            onUnitChange = { event(WeightTrackerUiEvent.UpdateUnit(it)) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        WeightProgressSection()
        Spacer(modifier = Modifier.height(24.dp))
        DoseRecommendationSection(weightData = weightData)
        Spacer(modifier = Modifier.height(30.dp))
        SummaryStatisticsSection(weightData = weightData)
        Spacer(modifier = Modifier.height(32.dp))
        AppButtonComponent(
            onClick = {
                event(WeightTrackerUiEvent.SubmitWeight)
            },
            modifier = Modifier.fillMaxWidth(),
            text = "Continue",

            )
        Spacer(modifier = Modifier.height(16.dp))
        AppButtonComponent(
            onClick = {
            },
            borderColors = AppThemeColor,
            modifier = Modifier.fillMaxWidth(),
            textColor = AppThemeColor,
            text = "Schedule Check-in",
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    White,
                    White
                )

            ),
            drawableResId = R.drawable.ic_check_schedule
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CurrentWeightSection(
    weightData: WeightTrackerData?,
    onUnitChange: (WeightUnit) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Enter Your Current Weight",
            fontFamily = nunito_sans_600,
            fontSize = 20.sp,
            color = SteelGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Last recorded: ${weightData?.lastRecordedWeight ?: "185"} ${weightData?.weightUnit?.name?.lowercase() ?: "lbs"} (${weightData?.lastRecordedDate ?: "7 days ago"})",
            fontFamily = nunito_sans_400,
            fontSize = 14.sp,
            color = SteelGray.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(25.dp),

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly, // distribute evenly
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = weightData?.currentWeight ?: "183",
                        fontFamily = nunito_sans_600,
                        color = Martinique50,
                        fontSize = 32.sp
                    )
                    Row(
                        modifier = Modifier

                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = AthensGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(AthensGray)
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (weightData?.weightUnit == WeightUnit.LBS) ElectricViolet else AthensGray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onUnitChange(WeightUnit.LBS) }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "lbs",
                                fontFamily = nunito_sans_600,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (weightData?.weightUnit == WeightUnit.LBS) AthensGray else ElectricViolet
                            )
                        }

                        // KG Button
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (weightData?.weightUnit == WeightUnit.KG) ElectricViolet else AthensGray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onUnitChange(WeightUnit.KG) }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "kg",
                                fontFamily = nunito_sans_600,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (weightData?.weightUnit == WeightUnit.KG) AthensGray else ElectricViolet
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_down_arrow),
                        contentDescription = "Down arrow",
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${weightData?.weeklyChange ?: "-2"} ${weightData?.weightUnit?.name?.lowercase() ?: "lbs"}",
                        fontFamily = nunito_sans_600,
                        fontSize = 16.sp,
                        color = Green16
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Since last week",
                    fontFamily = nunito_sans_400,
                    fontSize = 12.sp,
                    color = Gray60,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun WeightProgressSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val tempList = listOf(150.0, 300.0, 150.0, 300.0, 150.0)
        ChartView(list = tempList)

    }
}

@Composable
private fun ChartView(list: List<Double>) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(
            modifier = Modifier
                .background(White)
                .height(305.dp)
        ) {
            Text(
                text = "Weight Progress",
                style = TextStyle(
                    fontFamily = nunito_sans_600,
                    fontSize = 18.sp,
                    color = Black20
                ),
                modifier = Modifier.padding(top = 20.dp, start = 20.dp)
            )
            LineChart(
                labelHelperProperties = LabelHelperProperties(enabled = false),
                data = remember {
                    listOf(
                        Line(
                            //Line of show on graph
                            label = "",
                            values = list,
                            color = SolidColor(AppThemeColor),
                            curvedEdges = false,
                            dotProperties = DotProperties(
                                enabled = true,
                                color = SolidColor(AppThemeColor),
                                strokeWidth = 4.dp,
                                radius = 2.dp,
                                strokeColor = SolidColor(AppThemeColor),
                            ),
                            firstGradientFillColor = AppThemeColor.copy(alpha = 0.2f),  // stronger near line
                            secondGradientFillColor = Color.White.copy(alpha = 0.0f),
                        )
                    )
                },
                curvedEdges = true,
                maxValue = 400.0.toInt().toDouble(),
                minValue = 0.0.toInt().toDouble(),
                //column value left-side [0 to 400]
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(color = Black, fontFamily = nunito_sans_400, fontSize = 10.sp),
                    count = IndicatorCount.StepBased(stepBy = 50.0.toInt().toDouble()),
                    padding = 9.dp,
                    contentBuilder = { indicator ->
                        indicator.toInt().toString()  // convert Double -> Int
                    },
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = listOf("Week 1", "Week 2", "Week 3", "Week 4", "Week 5"),
                    builder = { modifier, label, shouldRotate, index ->
                        Text(
                            modifier = modifier.align(Alignment.CenterHorizontally),
                            style = TextStyle(
                                color = Black,
                                fontFamily = nunito_sans_400,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Left
                            ), text = label
                        )
                    },
                    padding = 8.dp,
                ),
                modifier = Modifier.padding(20.dp),
                gridProperties = GridProperties(
                    enabled = true,
                    xAxisProperties = GridProperties.AxisProperties(
                        thickness = 1.dp,
                        lineCount = 9,
                        color = SolidColor(Gray5)
                    ),
                    yAxisProperties = GridProperties.AxisProperties(
                        thickness = 1.dp,
                        color = SolidColor(Gray5)
                    )
                ),

                )
        }
    }

}

@Composable
private fun DoseRecommendationSection(weightData: WeightTrackerData?) {
    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = YellowFE, shape = RoundedCornerShape(16.dp))
            .background(Dolly)
            .padding(25.dp),

        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(painterResource(id = R.drawable.ic_dose), contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Dose Recommendation",
                fontFamily = nunito_sans_600,
                fontSize = 18.sp,
                color = Yellow85
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Based on your weight loss of ${weightData?.weeklyChange ?: "2"} ${weightData?.weightUnit?.name?.lowercase() ?: "lbs"} this week (${weightData?.weeklyChangePercentage ?: "1.1%"}), we recommend maintaining your current dose.",
            fontFamily = nunito_sans_400,
            fontSize = 14.sp,
            color = YellowA1,
        )
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier =
            Modifier
                .background(Beeswax, shape = RoundedCornerShape(12.dp))
                .clip(shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "Clinical Note:",
                fontFamily = nunito_sans_600,
                fontSize = 14.sp,
                color = Yellow85,

                )
            Text(
                text = "Target: 2% monthly weight loss for dose increase consideration",
                fontFamily = nunito_sans_400,
                fontSize = 12.sp,
                color = YellowA1,
            )
        }
    }
}

@Composable
private fun SummaryStatisticsSection(weightData: WeightTrackerData?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StatCard(
            value = "${weightData?.totalLost ?: "-12"} lbs", // add lbs
            label = "Total Lost",
            modifier = Modifier.weight(1f) // apply weight here
        )
        StatCard(
            value = weightData?.bodyWeightPercentage ?: "6.1%",
            label = "Body Weight",
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),

        ) {
        Column(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontFamily = nunito_sans_700,
                fontSize = 24.sp,
                color = AppThemeColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontFamily = nunito_sans_400,
                fontSize = 12.sp,
                color = Gray80,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(heightDp = 1200)
@Composable
private fun WeightTrackerScreenPreview() {
    val uiState = WeightTrackerUiState()
    WeightTrackerScreenContent(uiState = uiState, event = {})
}
