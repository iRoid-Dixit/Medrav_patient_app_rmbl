package com.medrevpatient.mobile.app.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.calendar.CalendarDefaults.calendarColors
import java.time.LocalDate

@Composable
fun CalendarBody(
    date: LocalDate,
    events: List<CalendarEvent>,
    onDayClick: (CalendarEvent?) -> Unit = {},
    calendarColors: CalendarColors
) {
    val startDay = date.withDayOfMonth(1)
    val daysInMonth = startDay.lengthOfMonth()
    val startDayOfWeek = (startDay.dayOfWeek.value - 1) % 7
    val selectedDay = remember { mutableStateOf<LocalDate?>(null) }

    // List of days including leading empty days
    val days = List(startDayOfWeek) { "" } + List(daysInMonth) { it + 1 }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth().offset(y = (-10).dp),
        horizontalArrangement = Arrangement.spacedBy(CalendarDefaults.Dimens.XSmall),
        //verticalArrangement = Arrangement.spacedBy(CalendarDefaults.Dimens.XSmall)
    ) {
        items(days) { day ->
            CalendarDay(
                day = day,
                events = events,
                currentDate = date,
                onDayClick = { event ->
                    selectedDay.value = event?.date
                    onDayClick(event) },
                calendarColors = calendarColors,
                selectedDay = selectedDay.value
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    CalendarBody(date = LocalDate.now(), events = emptyList(), calendarColors = calendarColors())
}