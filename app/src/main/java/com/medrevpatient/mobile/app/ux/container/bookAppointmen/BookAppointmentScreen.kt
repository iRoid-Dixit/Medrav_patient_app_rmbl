package com.medrevpatient.mobile.app.ux.container.bookAppointmen

import android.widget.Toast
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.medrevpatient.mobile.app.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.DatePickerWithDialog
import com.medrevpatient.mobile.app.ui.compose.common.DateSelectComponent
import com.medrevpatient.mobile.app.ui.compose.common.NotesTextArea
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.*
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable

@ExperimentalMaterial3Api
@Composable
fun BookAppointmentScreen(
    navController: NavController,
    viewModel: BookAppointmentViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                isBackVisible = true,
                onClick = { navController.popBackStack() },
                titleText = "Book Appointment",

                )
        },
        navBarData = null
    ) {
        uiState.event(BookAppointmentUiEvent.GetContext(context))
        BookAppointmentScreenContent(uiState = uiState, event = uiState.event)
    }

    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun BookAppointmentScreenContent(uiState: BookAppointmentUiState, event: (BookAppointmentUiEvent) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedTime by remember { mutableStateOf("09:00 AM") }
    var selectedTimePeriod by remember { mutableStateOf("Morning") }
    var additionalNotes by remember { mutableStateOf("") }
    var isTimePeriodDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    val bookAppointmentUiState by uiState.bookAppointmentUiDataFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
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
        DateSelectComponent(
            value = bookAppointmentUiState?.selectedDate ?: "",
            header = "mm/dd/yyyy",
            isTitleVisible = true,
            backGroundColor = White,
            borderColors = SteelGray.copy(alpha = 0.2f),
            title = "Select Date",
            trailingIcon = R.drawable.ic_calendar,
            onClick = {
                event(BookAppointmentUiEvent.BookAppointmentSheetVisibility(true))
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Select Time Section
        TimeSlotComponent(
            selectedTime = bookAppointmentUiState?.selectedTime ?: "09:00 AM",
            selectedTimePeriod = bookAppointmentUiState?.selectedTimePeriod ?: "Morning",
            availableTimeSlots = bookAppointmentUiState?.availableTimeSlots ?: listOf(
                "08:00 AM", "08:15 AM", "08:30 AM", "08:45 AM",
                "09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM",
                "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM",
                "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM"
            ),
            unavailableTimeSlots = bookAppointmentUiState?.unavailableTimeSlots ?: listOf("10:00 PM"),
            timePeriods = bookAppointmentUiState?.timePeriods ?: listOf("Morning", "Afternoon", "Evening"),
            isDropdownExpanded = bookAppointmentUiState?.isTimePeriodDropdownExpanded ?: false,
            onTimeSelected = { time ->
                event(BookAppointmentUiEvent.SelectTime(time))
            },
            onTimePeriodSelected = { period ->
                event(BookAppointmentUiEvent.SelectTimePeriod(period))
            },
            onDropdownExpanded = { expanded ->
                event(BookAppointmentUiEvent.ToggleTimePeriodDropdown(expanded))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Additional Notes Section
        NotesTextArea(
            value = bookAppointmentUiState?.additionalNotes ?: "",
            onValueChange = { notes ->
                event(BookAppointmentUiEvent.UpdateNotes(notes))
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Confirm Booking Button
        AppButtonComponent(
            modifier = Modifier.fillMaxWidth(),
            text = "Confirm Booking",
            onClick = {
                event(BookAppointmentUiEvent.ConfirmBooking)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
   if (bookAppointmentUiState?.isDatePickerVisible == true) {
       DatePickerWithDialog(
           onSelectedDate = selectedDateMillis,
           onDateSelected = { dateString ->
                event(BookAppointmentUiEvent.SelectDate(dateString))
                event(BookAppointmentUiEvent.BookAppointmentSheetVisibility(false))
           },
           onDismiss = {
               event(BookAppointmentUiEvent.BookAppointmentSheetVisibility(false))
           },
           onDateSelectedLong = {
               selectedDateMillis = it
           }
       )
   }
}

@Composable
fun TimeSlotComponent(
    selectedTime: String,
    selectedTimePeriod: String,
    availableTimeSlots: List<String>,
    unavailableTimeSlots: List<String>,
    timePeriods: List<String>,
    isDropdownExpanded: Boolean,
    onTimeSelected: (String) -> Unit,
    onTimePeriodSelected: (String) -> Unit,
    onDropdownExpanded: (Boolean) -> Unit
) {
    Column {
        // Time Period Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Time",
                fontFamily = nunito_sans_600,
                color = SteelGray,
                fontSize = 16.sp,
            )
            Box {
                Row(
                    modifier = Modifier
                        .background(
                            color = White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(width = 1.dp, color = SteelGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                        .clickable { onDropdownExpanded(!isDropdownExpanded) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedTimePeriod,
                        fontFamily = nunito_sans_600,
                        color = SteelGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "Dropdown",
                    )
                }
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { onDropdownExpanded(false) }
                ) {
                    timePeriods.forEach { period ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = period,
                                    fontFamily = nunito_sans_600,
                                    color = SteelGray,
                                    fontSize = 14.sp
                                )
                            },
                            onClick = {
                                onTimePeriodSelected(period)
                                onDropdownExpanded(false)
                            }
                        )
                    }
                }
            }
        }
        Text(
            text = "Select a convenient time",
            fontFamily = nunito_sans_400,
            color = Martinique.copy(alpha = 0.5f),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Time Slots Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items(availableTimeSlots) { timeSlot ->
                val isSelected = timeSlot == selectedTime
                val isUnavailable = unavailableTimeSlots.contains(timeSlot)

                Box(
                    modifier = Modifier
                        .background(
                            color = when {
                                isUnavailable -> Gray5
                                isSelected -> AppThemeColor
                                else -> White
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = when {
                                isUnavailable -> Gray5
                                isSelected -> AppThemeColor
                                else -> Gray5
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(enabled = !isUnavailable) {
                            if (!isUnavailable) {
                                onTimeSelected(timeSlot)
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeSlot,
                        fontFamily = nunito_sans_600,
                        color = when {
                            isUnavailable -> Gray40
                            isSelected -> White
                            else -> SteelGray
                        },
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun BookAppointmentScreenPreview() {
    val uiState = BookAppointmentUiState()
    Surface {
        BookAppointmentScreenContent(uiState = uiState, event = {})
    }
}
