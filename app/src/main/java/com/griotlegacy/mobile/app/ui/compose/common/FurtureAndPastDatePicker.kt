package com.griotlegacy.mobile.app.ui.compose.common

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.White
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutureAndPastDatePickerWithDialog(
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    onDateSelectedLong: (Long) -> Unit,
    onSelectedDate: Long? = null,
) {
    val today = Calendar.getInstance()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = onSelectedDate ?: today.timeInMillis,
        initialDisplayedMonthMillis = today.timeInMillis,
        // Removed the selectableDates restriction to allow all dates
    )

    val selectedDate = remember { mutableLongStateOf(today.timeInMillis) }
    if (datePickerState.selectedDateMillis != selectedDate.longValue) {
        selectedDate.longValue = datePickerState.selectedDateMillis ?: today.timeInMillis
    }

    val formattedDate = remember(selectedDate.longValue) {
        convertMillisToDate(selectedDate.longValue)
    }

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(formattedDate)
                    onDateSelectedLong(selectedDate.longValue)
                    onDismiss.invoke()
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) { Text("Cancel") }
        }
    ) {
        DatePicker(
            colors = DatePickerDefaults.colors(
                todayDateBorderColor = AppThemeColor,
                currentYearContentColor = AppThemeColor,
                selectedYearContentColor = White,
                selectedDayContainerColor = AppThemeColor,
                todayContentColor = AppThemeColor,
                selectedYearContainerColor = AppThemeColor,
                selectedDayContentColor = White,
                dayContentColor = AppThemeColor,
            ),
            state = datePickerState,
            showModeToggle = false,
        )
    }
}

@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}




    
