package com.medrevpatient.mobile.app.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.calendar.CalendarDefaults.calendarColors
import com.medrevpatient.mobile.app.ui.theme.AppThemeBlue
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import java.time.LocalDate

@Composable
fun CalendarDay(
    day: Any,
    currentDate: LocalDate,
    events: List<CalendarEvent>,
    onDayClick: (CalendarEvent?) -> Unit = {},
    calendarColors: CalendarColors,
    selectedDay: LocalDate? = null
) {
    val date = if (day is Int) LocalDate.of(currentDate.year, currentDate.monthValue, day) else null
    val eventForThisDay = date?.let {
        events.find { it.date == date }
    }
    val isSelected = date == selectedDay && date?.month == currentDate.month
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .width(30.dp)
            .height(43.dp)
            .padding(top = 5.dp)
            .clip(CircleShape)
            .border(
                width = if (eventForThisDay != null && !eventForThisDay.isCompleted) 0.5.dp else 0.dp,
                color = when {
                    isSelected -> AppThemeBlue
                    eventForThisDay != null && !eventForThisDay.isCompleted -> MineShaft
                    else -> Color.Transparent
                },
                shape = if (eventForThisDay != null && !eventForThisDay.isCompleted) CircleShape else CircleShape
            )
            .background(
                brush = if (eventForThisDay != null && eventForThisDay.isCompleted)
                    Brush.linearGradient(
                        colors = listOf(
                            white,
                            ColorSwansDown
                        )
                    )
                else SolidColor(Color.Transparent),
                shape = if (eventForThisDay != null && eventForThisDay.isCompleted) CircleShape else RoundedCornerShape(0.dp)
            )
            .padding(vertical = 5.dp)
            .noRippleClickable {
                onDayClick(eventForThisDay)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (date != null) {
                Text(
                    text = date.dayOfMonth.toString(),
                    textAlign = TextAlign.Center,
                    color = if (isSelected) AppThemeBlue else MineShaft,
                    fontWeight = FontWeight.W300,
                    fontSize = 12.sp,
                    fontFamily = outFit
                )

                eventForThisDay?.let { event ->
                    Image(
                        imageVector = if (event.isCompleted) event.completedWorkoutIcon else event.pendingWorkoutIcon,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(15.dp),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(if (isSelected) AppThemeBlue else calendarColors.eventContentColor)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayPreview() {
    val events = listOf(
        CalendarEvent(
            date = LocalDate.now(),
            completedWorkoutIcon = ImageVector.vectorResource(id = R.drawable.ic_right_tick),
            pendingWorkoutIcon = ImageVector.vectorResource(id = R.drawable.calendar),
            isCompleted = true
        )
    )
    val day = LocalDate.now().dayOfMonth
    val month = LocalDate.now().monthValue
    val year = LocalDate.now().year
    CalendarDay(
        day = day,
        currentDate = LocalDate.of(year, month, day),
        events = events,
        calendarColors = calendarColors(),

        )
}