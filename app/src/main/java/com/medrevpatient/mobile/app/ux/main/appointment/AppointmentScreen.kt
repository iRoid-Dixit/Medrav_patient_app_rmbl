package com.medrevpatient.mobile.app.ux.main.appointment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppBottomNavigation
import com.medrevpatient.mobile.app.ui.compose.common.AppointmentFilterTabs
import com.medrevpatient.mobile.app.ui.compose.common.BottomNavItem
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
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

@Composable
private fun AppointmentScreenContent(
    uiState: AppointmentsUiState,
    appointmentsUiDataFlow: AppointmentsUiDataState?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
        // Appointments list
        LazyColumn(
            contentPadding = PaddingValues(vertical = 20.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(White),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            val filteredAppointments = appointmentsUiDataFlow?.appointments?.filter { appointment ->
                when (appointmentsUiDataFlow.selectedTab) {
                    AppointmentTab.ALL -> true
                    AppointmentTab.UPCOMING -> appointment.status == AppointmentStatus.UPCOMING
                    AppointmentTab.PAST -> appointment.status == AppointmentStatus.PAST || appointment.status == AppointmentStatus.CANCELED
                }
            } ?: emptyList()

            items(filteredAppointments) { appointment ->
                AppointmentCard(
                    appointment = appointment,
                    onVideoCallClick = { uiState.event(AppointmentsUiEvent.OnVideoCallClick(appointment.id)) },
                    onMessageClick = { uiState.event(AppointmentsUiEvent.OnMessageClick(appointment.id)) },
                    onViewDetailsClick = { uiState.event(AppointmentsUiEvent.ViewDetailsClick)}
                )
            }
        }
        // Bottom navigation
        AppBottomNavigation(
            items = getBottomNavItems(),
            onItemClick = { index ->
                // Handle bottom nav clicks
            }
        )
    }
}

@Composable
fun AppointmentCard(
    modifier: Modifier = Modifier,
    appointment: AppointmentItem,
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

                // Date circle
                DateCircle(
                    day = appointment.day,
                    isToday = appointment.isToday
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
                                text = appointment.month,
                                fontSize = 14.sp,
                                fontFamily = nunito_sans_400,
                                color = SteelGray.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = appointment.time,
                                fontSize = 14.sp,
                                fontFamily = nunito_sans_600,
                                color = SteelGray
                            )
                        }
                        // Status tag
                        StatusTag(status = appointment.status)
                    }


                }
            }
            // Doctor info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(painter = painterResource(id = R.drawable.ic_place_holder), contentDescription = null, modifier = Modifier.size(45.dp))

                Column {
                    Text(
                        text = appointment.doctorName,
                        fontSize = 16.sp,
                        fontFamily = nunito_sans_600,
                        color = SteelGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = appointment.doctorSpecialization,
                        fontSize = 14.sp,
                        fontFamily = nunito_sans_400,
                        color = Martinique.copy(alpha = 0.4f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(id = R.drawable.ic_video_icon), contentDescription = null)
                Spacer(modifier = Modifier.width(16.dp))
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
    isToday: Boolean
) {
    val backgroundColor = if (isToday) BlueChalk else BlueChalk.copy(alpha = 0.3f)
    Box(
        modifier = Modifier
            .size(45.dp)
            .background(
                color = backgroundColor,
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
    status: AppointmentStatus
) {
    val backgroundColor = when (status) {
        AppointmentStatus.UPCOMING -> BlueChalk
        AppointmentStatus.PAST -> Silver.copy(alpha = 0.1f)
        AppointmentStatus.CANCELED -> RedOrange.copy(alpha = 0.1f)
    }
    val textColors = when (status) {
        AppointmentStatus.UPCOMING -> AppThemeColor
        AppointmentStatus.PAST -> GrayBD
        AppointmentStatus.CANCELED -> RedF7
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
            text = status.name.lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 12.sp,
            fontFamily = nunito_sans_600,
            color = textColors,
        )
    }
}

private fun getBottomNavItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            icon = com.medrevpatient.mobile.app.R.drawable.ic_unselected_home,
            selectedIcon = com.medrevpatient.mobile.app.R.drawable.ic_selected_home,
            label = "Home",
            isSelected = false
        ),
        BottomNavItem(
            icon = com.medrevpatient.mobile.app.R.drawable.ic_unselected_appointments,
            selectedIcon = com.medrevpatient.mobile.app.R.drawable.ic_selected_appointments,
            label = "Appointments",
            isSelected = true
        ),
        BottomNavItem(
            icon = com.medrevpatient.mobile.app.R.drawable.ic_unselected_medication,
            selectedIcon = com.medrevpatient.mobile.app.R.drawable.ic_selected_medication,
            label = "Medication",
            isSelected = false
        ),
        BottomNavItem(
            icon = com.medrevpatient.mobile.app.R.drawable.ic_unselected_message,
            selectedIcon = com.medrevpatient.mobile.app.R.drawable.ic_selected_message,
            label = "Message",
            isSelected = false
        ),
        BottomNavItem(
            icon = com.medrevpatient.mobile.app.R.drawable.ic_unselected_profile,
            selectedIcon = com.medrevpatient.mobile.app.R.drawable.ic_selected_profile,
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