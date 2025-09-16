package com.medrevpatient.mobile.app.ux.container.bookAppointmen

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.DatePickerWithDialog
import com.medrevpatient.mobile.app.ui.compose.common.DateSelectComponent
import com.medrevpatient.mobile.app.ui.compose.common.DropdownField
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
                onClick = { uiState.event(BookAppointmentUiEvent.ConfirmBooking) },
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
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .noRippleClickable {
                keyboardController?.hide()
            }
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            DateSelectComponent(
                value = bookAppointmentUiState?.selectedDate ?: "",
                header = "mm/dd/yyyy",
                isTitleVisible = true,
                backGroundColor = White,
                errorMessage = bookAppointmentUiState?.selectedDateErrorFlow ?: "",
                borderColors = SteelGray.copy(alpha = 0.2f),
                title = "Select Date",
                trailingIcon = R.drawable.ic_calendar,
                onClick = {
                    event(BookAppointmentUiEvent.BookAppointmentSheetVisibility(true))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Select Time Section - Modified to avoid nested scrolling
            TimeSlotComponentFixed(
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
                isLoading = bookAppointmentUiState?.isLoadingSlots ?: false,
                isEnabled = bookAppointmentUiState?.selectedDate?.isNotBlank() == true,
                onTimeSelected = { time ->
                    event(BookAppointmentUiEvent.SelectTime(time))
                },
                onTimePeriodSelected = { period ->
                    event(BookAppointmentUiEvent.SelectTimePeriod(period))
                },
                onDropdownExpanded = { expanded ->
                    event(BookAppointmentUiEvent.ToggleTimePeriodDropdown(expanded))
                },
                errorMessage = bookAppointmentUiState?.timeSelectErrorFlow
            )
            Spacer(modifier = Modifier.height(20.dp))
            DropdownField(
                list = listOf(
                    stringResource(R.string.weight_loss_management_follow_up),
                    stringResource(R.string.initial_consultation),
                    stringResource(R.string.initial_consultation),
                    stringResource(R.string.Medication_review)
                ),
                valueTextColor = SteelGray,
                isTitleVisible = true,
                backGroundColor = White,
                borderColors = SteelGray.copy(alpha = 0.2f),
                title = "Select Category",
                expanded = expanded,
                selectedCategory = bookAppointmentUiState?.selectCategory ?: "",
                onRoleDropDownExpanded = {
                    expanded = it
                },
                errorMessage = bookAppointmentUiState?.selectCategoryErrorMsg,
                onUserRoleValue = {
                    event(BookAppointmentUiEvent.RoleDropDownExpanded(it))
                },
            )
            Spacer(modifier = Modifier.height(24.dp))
            NotesTextArea(
                value = bookAppointmentUiState?.additionalNotes ?: "",
                onValueChange = { notes ->
                    event(BookAppointmentUiEvent.UpdateNotes(notes))
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
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
}

@Composable
fun TimeSlotComponentFixed(
    selectedTime: String,
    selectedTimePeriod: String,
    availableTimeSlots: List<String>,
    unavailableTimeSlots: List<String>,
    timePeriods: List<String>,
    isDropdownExpanded: Boolean,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    onTimeSelected: (String) -> Unit,
    onTimePeriodSelected: (String) -> Unit,
    onDropdownExpanded: (Boolean) -> Unit,
    errorMessage: String? = null,
) {
    Column {
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
                            color = if (isEnabled) White else White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isEnabled) SteelGray.copy(alpha = 0.2f) else Gray20.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .noRippleClickable {
                            if (isEnabled) onDropdownExpanded(!isDropdownExpanded)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnabled) selectedTimePeriod else "Select date first",
                        fontFamily = nunito_sans_600,
                        color = if (isEnabled) SteelGray else Gray20,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "Dropdown",
                        alpha = if (isEnabled) 1f else 0.5f
                    )
                }
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { onDropdownExpanded(false) },
                    modifier = Modifier.background(Color.Transparent),
                    offset = DpOffset(x = 0.dp, y = 0.dp),
                    properties = PopupProperties(focusable = true),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    containerColor = Color.Transparent
                ) {
                    timePeriods.forEach { period ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(
                                    width = 1.dp,
                                    color = SteelGray.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onTimePeriodSelected(period)
                                    onDropdownExpanded(false)
                                }
                                .padding(vertical = 14.dp, horizontal = 20.dp)
                        ) {
                            Text(
                                text = period,
                                fontSize = 14.sp,
                                fontFamily = nunito_sans_400,
                                color = SteelGray
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
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

        // Replace LazyVerticalGrid with Column and Rows to avoid nested scrolling
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                // Show loading indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading available slots...",
                        fontFamily = nunito_sans_400,
                        color = SteelGray,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Create rows of 3 items each
                availableTimeSlots.chunked(3).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { timeSlot ->
                            val isSelected = timeSlot == selectedTime
                            val isUnavailable = unavailableTimeSlots.contains(timeSlot)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = when {
                                            isUnavailable -> Gray20
                                            isSelected -> Magnolia
                                            else -> White
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            isUnavailable -> Gray20
                                            isSelected -> AppThemeColor
                                            else -> Gray5
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = !isUnavailable) {
                                        if (!isUnavailable) {
                                            onTimeSelected(timeSlot)
                                        }
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = timeSlot,
                                    fontFamily = nunito_sans_400,
                                    color = when {
                                        isUnavailable -> Gray20
                                        isSelected -> AppThemeColor
                                        else -> SteelGray
                                    },
                                    fontSize = 14.sp,
                                )
                            }
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            if (errorMessage?.isNotEmpty() == true) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = nunito_sans_600,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 7.dp)
                )
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
