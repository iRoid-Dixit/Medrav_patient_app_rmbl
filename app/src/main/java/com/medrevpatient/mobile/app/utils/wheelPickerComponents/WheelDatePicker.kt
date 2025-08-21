package com.medrevpatient.mobile.app.utils.wheelPickerComponents

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun WheelDatePicker(
    modifier: Modifier = Modifier,
    startDate: LocalDate = LocalDate.now(),
    minDate: LocalDate = LocalDate.MIN,
    maxDate: LocalDate = LocalDate.MAX,
    yearsRange: IntRange? = IntRange(1922, 2122),
    size: DpSize = DpSize(256.dp, 228.dp),
    rowCount: Int = 5,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = Color.White,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedDate : (snappedDate: LocalDate) -> Unit = {}
) {
    DefaultWheelDatePicker(
        modifier,
        startDate,
        minDate,
        maxDate,
        yearsRange,
        size,
        rowCount,
        textStyle,
        textColor,
        selectorProperties,
        onSnappedDate = { snappedDate ->
            onSnappedDate(snappedDate.snappedLocalDate)
            snappedDate.snappedIndex
        }
    )
}