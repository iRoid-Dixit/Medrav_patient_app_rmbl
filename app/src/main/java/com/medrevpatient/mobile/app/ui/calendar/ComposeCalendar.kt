package com.medrevpatient.mobile.app.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.calendar.CalendarDefaults.calendarColors
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import java.time.LocalDate

@Composable
fun ComposeCalendar(
    modifier: Modifier = Modifier,
    initDate: LocalDate = LocalDate.now(),
    events: ArrayList<CalendarEvent> = arrayListOf(),
    onDayClick: (CalendarEvent?) -> Unit = {},
    onNextClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    calendarColors: CalendarColors = calendarColors()
) {
    val currentDate = rememberSaveable { mutableStateOf(initDate) }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        //verticalArrangement = Arrangement.spacedBy(CalendarDefaults.Dimens.Default)
    ) {
        Spacer(modifier = Modifier.padding(top = 16.dp))
        CalendarHeader(
            currentYear = currentDate.value.year,
            currentMonth = currentDate.value.monthValue,
            calendarColors = calendarColors,
            onPreviousMonthClick = {
                currentDate.value = currentDate.value.minusMonths(1)
                onPreviousClick()
            },
            onNextMonthClick = {
                currentDate.value = currentDate.value.plusMonths(1)
                onNextClick()
            }
        )
        CalendarBody(
            date = currentDate.value,
            events = events,
            onDayClick = { event -> onDayClick(event) },
            calendarColors = calendarColors,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ComposeCalendarPreview() {
    ComposeCalendar(
        events = arrayListOf(
            CalendarEvent(
                date = LocalDate.now(),
                completedWorkoutIcon = ImageVector.vectorResource(id = R.drawable.ic_right_tick),
                pendingWorkoutIcon = ImageVector.vectorResource(id = R.drawable.calendar),
                isCompleted = true
            ),
            CalendarEvent(
                date = LocalDate.now().plusDays(1),
                completedWorkoutIcon = ImageVector.vectorResource(id = R.drawable.ic_right_tick),
                pendingWorkoutIcon = ImageVector.vectorResource(id = R.drawable.calendar),
                isCompleted = false
            ),
            CalendarEvent(
                date = LocalDate.now().plusDays(7),
                completedWorkoutIcon = ImageVector.vectorResource(id = R.drawable.ic_right_tick),
                pendingWorkoutIcon = ImageVector.vectorResource(id = R.drawable.calendar),
                isCompleted = true
            ),
            CalendarEvent(
                date = LocalDate.now().plusDays(14),
                completedWorkoutIcon = ImageVector.vectorResource(id = R.drawable.ic_right_tick),
                pendingWorkoutIcon = ImageVector.vectorResource(id = R.drawable.calendar),
                isCompleted = false
            )
        ),
        calendarColors = calendarColors(
            eventBackgroundColor = Color.Red,
            eventContentColor = MineShaft
        )
    )
}