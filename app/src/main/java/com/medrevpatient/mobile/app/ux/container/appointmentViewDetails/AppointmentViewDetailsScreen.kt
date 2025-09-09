package com.medrevpatient.mobile.app.ux.container.appointmentViewDetails

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.VerifyButton
import com.medrevpatient.mobile.app.ui.theme.*
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable

@ExperimentalMaterial3Api
@Composable
fun AppointmentViewDetailsScreen(
    navController: NavController,
    viewModel: AppointmentViewDetailsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                onClick = { uiState.event(AppointmentViewDetailsUiEvent.OnBackClick) },
                isBackVisible = true,
                titleText = "Appointment Details",

                )
        },
        navBarData = null
    ) {
        uiState.event(AppointmentViewDetailsUiEvent.GetContext(context))
        AppointmentViewDetailsContent(uiState = uiState, event = uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun AppointmentViewDetailsContent(uiState: AppointmentViewDetailsUiState, event: (AppointmentViewDetailsUiEvent) -> Unit) {
    val appointmentData by uiState.appointmentViewDetailsDataFlow.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val appointment = appointmentData?.appointment
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        appointment?.let { appointmentDetails ->
            // Appointment Overview Card
            AppointmentOverviewCard(
                appointment = appointmentDetails,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Doctor Information Section
            DoctorInformationSection(
                doctor = appointmentDetails.doctor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            ActionButtonsSection(
                onJoinVideoCall = { event(AppointmentViewDetailsUiEvent.JoinVideoCall) },
                onReschedule = { event(AppointmentViewDetailsUiEvent.RescheduleAppointment) },
                onCancel = { event(AppointmentViewDetailsUiEvent.CancelAppointment) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Appointment Notes Section
            AppointmentNotesSection(
                preparationInstructions = appointmentDetails.preparationInstructions,
                reminder = appointmentDetails.reminder,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Post-Appointment Section
            PostAppointmentSection(
                note = appointmentDetails.postAppointmentNote,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Need Help Section
            NeedHelpSection(
                onContactSupport = { event(AppointmentViewDetailsUiEvent.ContactSupport) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


// Appointment Overview Card Component
@Composable
private fun AppointmentOverviewCard(
    appointment: AppointmentDetails,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Status and ID Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Green2C)
                    )
                    Text(
                        text = "Upcoming",
                        color = Green2C,
                        fontSize = 12.sp,
                        fontFamily = nunito_sans_400
                    )
                }

                // Appointment ID
                Text(
                    text = "ID: #${appointment.id}",
                    color = Gray94,
                    fontSize = 12.sp,
                    fontFamily = nunito_sans_400
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Date and Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = AppThemeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = appointment.date,
                        color = Black,
                        fontSize = 14.sp,
                        fontFamily = nunito_sans_600
                    )
                }

                // Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = AppThemeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = appointment.time,
                        color = Black,
                        fontSize = 14.sp,
                        fontFamily = nunito_sans_600
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Type
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = AppThemeColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = appointment.type,
                    color = Black,
                    fontSize = 14.sp,
                    fontFamily = nunito_sans_600
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Purpose
            Text(
                text = appointment.purpose,
                color = Gray94,
                fontSize = 12.sp,
                fontFamily = nunito_sans_400
            )
        }
    }
}

// Doctor Information Section Component
@Composable
private fun DoctorInformationSection(
    doctor: DoctorInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Doctor Information",
            color = Black,
            fontSize = 16.sp,
            fontFamily = nunito_sans_700,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Doctor Profile Image
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Gray09),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Gray94,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = doctor.name,
                            color = Black,
                            fontSize = 16.sp,
                            fontFamily = nunito_sans_700,
                            fontWeight = FontWeight.Bold
                        )

                        if (doctor.isVerified) {
                            VerifyButton(
                                onClick = { },
                                verifyButtonText = "Verified",
                                verifyButtonBackgroundColor = Green2C.copy(alpha = 0.1f),
                                textColors = Green2C
                            )
                        }
                    }

                    Text(
                        text = doctor.specialization,
                        color = Gray94,
                        fontSize = 12.sp,
                        fontFamily = nunito_sans_400
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AppThemeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = doctor.experience,
                            color = Gray94,
                            fontSize = 12.sp,
                            fontFamily = nunito_sans_400
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onJoinVideoCall: () -> Unit,
    onReschedule: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Join Video Call Button
        AppButtonComponent(
            text = "Join Video Call",
            onClick = onJoinVideoCall,
            modifier = Modifier.fillMaxWidth(),
            drawableResId = R.drawable.ic_video_call,
            backgroundBrush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(AppThemeColor, SteelGray)
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reschedule Button
            AppButtonComponent(
                text = "Reschedule",
                onClick = onReschedule,
                modifier = Modifier.weight(1f),
                drawableResId = R.drawable.ic_calendar,
                textColor = AppThemeColor,
                backgroundBrush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(White, White)
                ),
                borderColors = AppThemeColor
            )
            AppButtonComponent(
                text = "Cancel",
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                drawableResId = R.drawable.ic_close,
                textColor = RedF7,
                backgroundBrush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(White, White)
                ),
                borderColors = RedF7
            )
        }
    }
}

@Composable
private fun AppointmentNotesSection(
    preparationInstructions: List<String>,
    reminder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Appointment Notes",
            color = Black,
            fontSize = 16.sp,
            fontFamily = nunito_sans_700,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Preparation Instructions",
                    color = Black,
                    fontSize = 14.sp,
                    fontFamily = nunito_sans_600,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))
                preparationInstructions.forEach { instruction ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AppThemeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = instruction,
                            color = Black,
                            fontSize = 12.sp,
                            fontFamily = nunito_sans_400,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reminder Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Gray09,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = AppThemeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = reminder,
                            color = Black,
                            fontSize = 12.sp,
                            fontFamily = nunito_sans_400
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun PostAppointmentSection(
    note: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Post-Appointment",
            color = Black,
            fontSize = 16.sp,
            fontFamily = nunito_sans_700,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Gray94,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note,
                    color = Gray94,
                    fontSize = 12.sp,
                    fontFamily = nunito_sans_400,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun NeedHelpSection(
    onContactSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Need Help?",
            color = Black,
            fontSize = 16.sp,
            fontFamily = nunito_sans_700,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onContactSupport() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Contact our support team",
                    color = Gray94,
                    fontSize = 12.sp,
                    fontFamily = nunito_sans_400
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = AppThemeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Customer Service",
                        color = AppThemeColor,
                        fontSize = 12.sp,
                        fontFamily = nunito_sans_600
                    )
                }
            }
        }
    }
}
@Preview
@Composable
private fun AppointmentDetailsScreenPreview() {
    val uiState = AppointmentViewDetailsUiState()
    Surface {
        AppointmentViewDetailsContent(uiState = uiState, event = {})
    }
}
