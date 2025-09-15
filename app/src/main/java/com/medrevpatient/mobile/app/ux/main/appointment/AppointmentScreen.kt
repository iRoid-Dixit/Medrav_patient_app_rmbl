package com.medrevpatient.mobile.app.ux.main.appointment

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.appointment.AppointmentResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppBottomNavigation
import com.medrevpatient.mobile.app.ui.compose.common.AppointmentFilterTabs
import com.medrevpatient.mobile.app.ui.compose.common.BottomNavItem
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.BlueChalk
import com.medrevpatient.mobile.app.ui.theme.GrayBD
import com.medrevpatient.mobile.app.ui.theme.Martinique
import com.medrevpatient.mobile.app.ui.theme.RedF7
import com.medrevpatient.mobile.app.ui.theme.RedOrange
import com.medrevpatient.mobile.app.ui.theme.Silver
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable

@ExperimentalMaterial3Api
@Composable
fun AppointmentScreen(
    navController: NavController,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val appointmentsUiDataFlow by uiState.appointmentsUiDataFlow.collectAsState()
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                titleText = "Appointment List",
                isActionVisible = true,
                actionIcon = R.drawable.ic_add,
                onActionClick = {
                    uiState.event(AppointmentsUiEvent.BookAppointmentClick)
                }
            )
        },
        navBarData = null
    ) {
        AppointmentScreenContent(
            uiState = uiState,
            appointmentsUiDataFlow,
        )
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentScreenContent(
    uiState: AppointmentsUiState,
    appointmentsUiDataFlow: AppointmentsUiDataState?
) {
    val appointmentList = uiState.appointmentList.collectAsLazyPagingItems()
    val keyboardController = LocalSoftwareKeyboardController.current
    var isUserRefreshing by remember { mutableStateOf(false) }

    // Only show pull-to-refresh indicator when user manually refreshes
    val isRefreshing = isUserRefreshing && appointmentList.loadState.refresh is LoadState.Loading

    // Track when user manually refreshes
    LaunchedEffect(appointmentList.loadState.refresh) {
        if (appointmentList.loadState.refresh is LoadState.NotLoading) {
            isUserRefreshing = false
        }
    }
    val pullToRefreshState = rememberPullToRefreshState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 20.dp)
            .noRippleClickable {
                keyboardController?.hide()
            }
            .background(White)
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        AppointmentFilterTabs(
            selectedTab = appointmentsUiDataFlow?.selectedTab ?: AppointmentTab.ALL,
            onTabSelected = { tab -> uiState.event(AppointmentsUiEvent.OnTabSelected(tab)) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    isUserRefreshing = true
                    appointmentList.refresh()
                },
                modifier = Modifier.fillMaxSize(),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        containerColor = White,
                        color = AppThemeColor,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }
            ) {
                appointmentList.loadState.refresh.apply {
                    when (this) {
                        is LoadState.Error -> {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                TapHereRefreshContent(onClick = { appointmentList.retry() })
                            }
                        }

                        is LoadState.Loading -> {
                            CustomLoader()
                        }

                        is LoadState.NotLoading -> {
                            if (appointmentList.itemCount == 0) {
                                NoDataFoundContent(text = "No appointments found")
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 15.dp),
                                ) {
                                    items(appointmentList.itemCount) { index ->
                                        appointmentList[index]?.let { response ->
                                            AppointmentCard(
                                                appointment = response,
                                                onVideoCallClick = { },
                                                onMessageClick = { },
                                                onViewDetailsClick = { uiState.event(AppointmentsUiEvent.ViewDetailsClick) }
                                            )
                                        }
                                    }
                                    when (appointmentList.loadState.append) {
                                        is LoadState.Error -> {
                                            item {
                                                TapHereRefreshContent(onClick = { appointmentList.retry() })
                                            }
                                        }

                                        LoadState.Loading -> {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 16.dp),
                                                    contentAlignment = Alignment.TopCenter
                                                ) {
                                                    CircularProgressIndicator(color = AppThemeColor)
                                                }
                                            }
                                        }

                                        is LoadState.NotLoading -> Unit
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun AppointmentCard(
    modifier: Modifier = Modifier,
    appointment: AppointmentResponse,
    onVideoCallClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onViewDetailsClick: () -> Unit = {}
) {

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(12.dp)
            )


    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(
                // modifier = Modifier.fillMaxWidth().padding(16.dp),
                // horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                DateCircle(
                    day = AppUtils.getDayFromTimestamp(appointment.appointmentTimestamp),
                )
                Spacer(modifier = Modifier.width(12.dp))

                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date and time row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = AppUtils.getMonthFromTimestamp(appointment.appointmentTimestamp),
                                fontSize = 14.sp,
                                fontFamily = nunito_sans_400,
                                color = SteelGray.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = AppUtils.getFormattedDateTime(appointment.appointmentTimestamp),
                                fontSize = 14.sp,
                                fontFamily = nunito_sans_600,
                                color = SteelGray
                            )
                        }
                        StatusTag(status = appointment.status)
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = appointment.doctorInfo.profileImage,
                    contentDescription = null,
                    error = painterResource(id = R.drawable.ic_place_holder),
                    placeholder = painterResource(id = R.drawable.ic_place_holder),
                    contentScale = ContentScale.Crop,
                    modifier =
                    Modifier
                        .size(45.dp)
                        .clip(shape = CircleShape)
                )
                Column {
                    Text(
                        text = appointment.doctorInfo.fullName,
                        fontSize = 16.sp,
                        fontFamily = nunito_sans_600,
                        color = SteelGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = appointment.doctorInfo.specialization,
                        fontSize = 14.sp,
                        fontFamily = nunito_sans_400,
                        color = Martinique.copy(alpha = 0.4f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (appointment.joinVideoCall){
                    Image(painter = painterResource(id = R.drawable.ic_video_icon), contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Image(painter = painterResource(id = R.drawable.ic_message), contentDescription = null)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "View Details",
                    fontSize = 14.sp,
                    fontFamily = nunito_sans_600,
                    color = AppThemeColor,
                    modifier = Modifier.clickable { onViewDetailsClick() }
                )
            }
        }
    }
}

@Composable
private fun DateCircle(
    day: String,

    ) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .background(
                color = BlueChalk,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            fontSize = 16.sp,
            fontFamily = nunito_sans_700,
            color = AppThemeColor
        )
    }
}

@Composable
private fun StatusTag(
    status: Int
) {
    val backgroundColor = when (status) {
        1 -> BlueChalk
        2 -> Silver.copy(alpha = 0.1f)
        3 -> RedOrange.copy(alpha = 0.1f)
        else -> BlueChalk
    }
    val textColors = when (status) {
        1 -> AppThemeColor
        2 -> GrayBD
        3 -> RedF7
        else -> AppThemeColor
    }
    val statusText = when (status) {
        1 -> "Upcoming"
        2 -> "Past"
        3 -> "Cancelled"
        else -> "Unknown"
    }

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            fontSize = 12.sp,
            fontFamily = nunito_sans_600,
            color = textColors,
        )
    }
}


private fun getBottomNavItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            icon = R.drawable.ic_unselected_home,
            selectedIcon = R.drawable.ic_selected_home,
            label = "Home",
            isSelected = false
        ),
        BottomNavItem(
            icon = R.drawable.ic_unselected_appointments,
            selectedIcon = R.drawable.ic_selected_appointments,
            label = "Appointments",
            isSelected = true
        ),
        BottomNavItem(
            icon = R.drawable.ic_unselected_medication,
            selectedIcon = R.drawable.ic_selected_medication,
            label = "Medication",
            isSelected = false
        ),
        BottomNavItem(
            icon = R.drawable.ic_unselected_message,
            selectedIcon = R.drawable.ic_selected_message,
            label = "Message",
            isSelected = false
        ),
        BottomNavItem(
            icon = R.drawable.ic_unselected_profile,
            selectedIcon = R.drawable.ic_selected_profile,
            label = "Profile",
            isSelected = false
        )
    )
}

@Preview
@Composable
private fun Preview() {
    val uiState = AppointmentsUiState()
    val sampleData = AppointmentsUiDataState(
        selectedTab = AppointmentTab.ALL,
        appointments = listOf(
            AppointmentItem(
                id = "1",
                day = "15",
                month = "July 2023",
                time = "Today, 10:00 AM",
                isToday = true,
                doctorName = "Dr. Mark Wilson",
                doctorSpecialization = "Weight Management Specialist",
                status = AppointmentStatus.UPCOMING
            )
        )
    )
    AppointmentScreenContent(uiState = uiState, appointmentsUiDataFlow = sampleData)
}