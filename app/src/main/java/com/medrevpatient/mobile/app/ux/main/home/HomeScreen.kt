package com.medrevpatient.mobile.app.ux.main.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.UserData.RecentActivity
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.HomeHeader
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black20
import com.medrevpatient.mobile.app.ui.theme.Gray09
import com.medrevpatient.mobile.app.ui.theme.Gray40
import com.medrevpatient.mobile.app.ui.theme.Gray80
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    uiState.event(HomeUiEvent.GetContext(context))
    val homeDetailsData by uiState.homeUiDataFlow.collectAsStateWithLifecycle()
    Log.d("TAG", "HomeScreen: ${homeDetailsData?.userName}")
    AppScaffold(
        containerColor = White,
        topAppBar = {
            HomeHeader(
                userName = homeDetailsData?.userName?:"",
                userProfileImage = homeDetailsData?.userProfile?:"",
                onNotificationClick = {
                   uiState.event(HomeUiEvent.NotificationClick)
                }
            )
        }
    ) {
        HomeScreenContent(uiState, homeDetailsData)
    }
    if (homeDetailsData?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(uiState: HomeUiState, homeDetailsData: HomeUiDataState?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .background(White),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(15.dp))
            AssignedDoctorCard(
                doctorName = "Dr. Mark Wilson",
                doctorSpecialty = "Weight Management Specialist",
                doctorImage = R.drawable.ic_place_holder.toString()
            )
        }
        item {
            GoalProgressCard(
                currentWeight = "55.6 kg",
                weightChange = "Dropped - 4 kg",
                minWeight = "50",
                maxWeight = "70",
                progress = 0.3f
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = "Side Effect Check-In",
                    iconRes = R.drawable.ic_side_effect_checkin,
                    onClick = {
                       uiState.event(HomeUiEvent.SideEffectClick)
                    }
                )
                ActionCard(
                    title = "Daily Diet Challenge",
                    iconRes = R.drawable.ic_daily_diet_challenge,
                    onClick = {
                        uiState.event(HomeUiEvent.DailyDietClick)
                    }
                )
            }
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Recent Activity",
                        fontFamily = nunito_sans_600,
                        fontSize = 18.sp,
                        color = Black20,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    val recentActivities = listOf(
                        RecentActivity(
                            title = "Weight logged: 185 lbs",
                            timestamp = "Today, 8:30 AM",
                            iconRes = R.drawable.ic_weight_scale,
                        ),
                        RecentActivity(
                            title = "Medication taken",
                            timestamp = "Yesterday, 9:00 AM",
                            iconRes = R.drawable.ic_medication,

                        ),
                        RecentActivity(
                            title = "Appointment with Dr. Wilson",
                            timestamp = "3 days ago",
                            iconRes = R.drawable.ic_appointment,
                        )
                    )
                    recentActivities.forEach { activity ->
                        RecentActivityItem(activity = activity)
                    }
                }
            }
        }
        item {
            AppButtonComponent(
                onClick = {

                },
                modifier = Modifier.fillMaxWidth(),
                text = "Calculate BMI",
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Gray09),
        colors = CardDefaults.cardColors(containerColor = White),
        // elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = title,
                fontFamily = nunito_sans_600,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SteelGray
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Arrow",
            )
        }


    }
}

@Composable
fun RecentActivityItem(
    activity: RecentActivity
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = activity.iconRes),
            contentDescription = activity.title,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = activity.title,
                fontFamily = nunito_sans_600,
                fontSize = 14.sp,
                color = SteelGray
            )
            Text(
                text = activity.timestamp,
                fontFamily = nunito_sans_400,
                fontSize = 12.sp,
                color = Gray40
            )
        }
    }
}


@Composable
fun GoalProgressCard(
    currentWeight: String = "55.6 kg",
    weightChange: String = "Dropped - 4 kg",
    minWeight: String = "50",
    maxWeight: String = "70",
    progress: Float = 0.3f // 30% progress
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Goal Progress",
                    fontFamily = nunito_sans_700,
                    fontSize = 18.sp,
                    color = SteelGray
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "View more",
                    fontFamily = nunito_sans_700,
                    fontSize = 12.sp,
                    color = SteelGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_view_more_arrow),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val centerX = canvasWidth / 2
                    val centerY = canvasHeight
                    val radius = (canvasWidth / 2) - 20

                    // Background arc
                    drawArc(
                        color = Gray09,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Progress arc
                    drawArc(
                        color = AppThemeColor,
                        startAngle = 180f,
                        sweepAngle = 180f * progress,
                        useCenter = false,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 25.dp)
                ) {

                    Text(
                        text = "Now",
                        fontFamily = nunito_sans_400,
                        fontSize = 12.sp,
                        color = Gray40,
                    )
                    Text(
                        text = currentWeight,
                        fontFamily = nunito_sans_700,
                        fontSize = 18.sp,
                        color = AppThemeColor,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),

                // .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = minWeight,
                    fontFamily = nunito_sans_700,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = weightChange,
                    fontFamily = nunito_sans_400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Gray80,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = maxWeight,
                    fontFamily = nunito_sans_700,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}
@Composable
fun AssignedDoctorCard(
    doctorName: String ="",
    doctorSpecialty: String = "Weight Management Specialist",
    doctorImage: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Assigned Doctor",
                fontFamily = nunito_sans_600,
                fontSize = 14.sp,
                color = SteelGray
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_place_holder),
                    contentDescription = "Doctor Profile",
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = doctorName,
                        fontFamily = nunito_sans_600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp,
                        color = SteelGray
                    )
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(
                        text = doctorSpecialty,
                        fontFamily = nunito_sans_400,
                        maxLines = 2,
                        fontSize = 14.sp,
                        color = Gray40
                    )
                }
            }
        }
    }
}

@Preview(heightDp = 900)
@Composable
fun HomeScreenContentPreview() {
    val uiState = HomeUiState()
    HomeScreenContent(uiState = uiState, homeDetailsData = HomeUiDataState())

}

