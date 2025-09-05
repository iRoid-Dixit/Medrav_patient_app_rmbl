package com.medrevpatient.mobile.app.ux.container.bookAppointmen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.medrevpatient.mobile.app.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.DatePickerWithDialog
import com.medrevpatient.mobile.app.ui.compose.common.DateSelectComponent
import com.medrevpatient.mobile.app.ui.compose.common.NotesTextArea
import com.medrevpatient.mobile.app.ui.compose.common.TimeSlotComponent
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.*
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.ux.container.editProfile.EditProfileUiEvent

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
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("09:00 AM") }
    var selectedTimePeriod by remember { mutableStateOf("Morning") }
    var additionalNotes by remember { mutableStateOf("") }
    var isTimePeriodDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
  //  val editProfileUiState by uiState.boo.collectAsStateWithLifecycle()
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
            value = selectedDate,
            header = "mm/dd/yyyy",
            isTitleVisible = true,
            backGroundColor = White,
            borderColors = SteelGray.copy(alpha = 0.2f),
            title = "Select Date",
            trailingIcon = R.drawable.ic_calendar,
            onClick = {
                // TODO: Implement date picker
                event(BookAppointmentUiEvent.ToggleDatePicker(true))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Select Time Section
        TimeSlotComponent(
            selectedTime = selectedTime,
            selectedTimePeriod = selectedTimePeriod,
            availableTimeSlots = listOf(
                "08:00 AM", "08:15 AM", "08:30 AM", "08:45 AM",
                "09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM",
                "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM",
                "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM"
            ),
            unavailableTimeSlots = listOf("10:00 PM"),
            timePeriods = listOf("Morning", "Afternoon", "Evening"),
            isDropdownExpanded = isTimePeriodDropdownExpanded,
            onTimeSelected = { time ->
                selectedTime = time
                event(BookAppointmentUiEvent.SelectTime(time))
            },
            onTimePeriodSelected = { period ->
                selectedTimePeriod = period
                event(BookAppointmentUiEvent.SelectTimePeriod(period))
            },
            onDropdownExpanded = { expanded ->
                isTimePeriodDropdownExpanded = expanded
                event(BookAppointmentUiEvent.ToggleTimePeriodDropdown(expanded))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Additional Notes Section
        NotesTextArea(
            value = additionalNotes,
            onValueChange = { notes ->
                additionalNotes = notes
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
   // if ()
    DatePickerWithDialog(
        onSelectedDate = selectedDateMillis,
        onDateSelected = { dateString ->
           // event(EditProfileUiEvent.OnClickOfDate(dateString))
            //showDatePickerDialog = false
        },
        onDismiss = {
           // showDatePickerDialog = false
        },
        onDateSelectedLong = {
            selectedDateMillis = it
        }
    )
}


@Preview
@Composable
private fun BookAppointmentScreenPreview() {
    val uiState = BookAppointmentUiState()
    Surface {
        BookAppointmentScreenContent(uiState = uiState, event = {})
    }
}
