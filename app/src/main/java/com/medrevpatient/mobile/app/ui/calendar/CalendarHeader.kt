package com.medrevpatient.mobile.app.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.calendar.CalendarDefaults.calendarColors
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarHeader(
    currentYear: Int,
    currentMonth: Int,
    calendarColors: CalendarColors,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    val dayNames = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CalendarDefaults.Dimens.Default)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = LocalDate.of(currentYear, currentMonth, 1)
                    .month
                    .getDisplayName(TextStyle.FULL, Locale.getDefault()) + ", " + currentYear,
                color = MineShaft,
                fontFamily = outFit,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = Modifier.noRippleClickable { onPreviousMonthClick() }
            )
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 7.dp)
                    .rotate(degrees = 180f)
                    .noRippleClickable {
                        onNextMonthClick()
                    }
            )
            /*Text(
                modifier = Modifier.weight(1.5f),
                text = LocalDate.of(currentYear, currentMonth, 1)
                    .month
                    .getDisplayName(TextStyle.FULL, Locale.getDefault()) + ", " + currentYear,
                color = MineShaft,
                fontFamily = outFit,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp
            )
            NavigationButton(
                onClick = {
                    onPreviousMonthClick()
                },
                icon = ImageVector.vectorResource(id = R.drawable.ic_back),
                modifier = Modifier.size(33.dp),
                backgroundColor = calendarColors.navigationBackgroundColor,
                tintColor = MineShaft
            )
            NavigationButton(
                onClick = {
                    onNextMonthClick()
                },
                icon = ImageVector.vectorResource(id = R.drawable.ic_back),
                modifier = Modifier.rotate(degrees = 180f).size(33.dp),
                backgroundColor = calendarColors.navigationBackgroundColor,
                tintColor = MineShaft
            )*/
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp),
        ) {
            dayNames.forEach { dayName ->
                Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = MineShaft,
                    fontFamily = outFit,
                    fontSize = 12.sp


                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    CalendarHeader(
        currentYear = LocalDate.now().year,
        currentMonth = LocalDate.now().monthValue,
        onPreviousMonthClick = {},
        onNextMonthClick = {},
        calendarColors = calendarColors()
    )
}