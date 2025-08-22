package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.Transparent
import com.medrevpatient.mobile.app.ui.theme.appTextColor
import com.medrevpatient.mobile.app.ui.theme.colorClockBg
import com.medrevpatient.mobile.app.ui.theme.colorClockSelectedText
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    currentHrs: Int = 12,
    currentMins: Int = 0,
    onClickOfCancel: () -> Unit,
    onClickOfPick: (TimePickerState) -> Unit
) {
    Dialog(onDismissRequest = {}) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(44.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val timeState = rememberTimePickerState(
                    initialHour = currentHrs,
                    initialMinute = currentMins,
                    is24Hour = false
                )

                TimePicker(
                    state = timeState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = colorClockBg,
                        clockDialUnselectedContentColor = appTextColor,
                        clockDialSelectedContentColor = colorClockSelectedText,
                        selectorColor = appTextColor,
                        periodSelectorBorderColor = appTextColor,
                        periodSelectorSelectedContainerColor = colorClockBg,
                        periodSelectorSelectedContentColor = appTextColor,
                        periodSelectorUnselectedContainerColor = White,
                        periodSelectorUnselectedContentColor = appTextColor,
                        timeSelectorSelectedContainerColor = colorClockBg,
                        timeSelectorSelectedContentColor = appTextColor,
                        timeSelectorUnselectedContentColor = appTextColor,
                        timeSelectorUnselectedContainerColor = colorClockBg,
                    ))

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    Button(
                        onClick = {
                            onClickOfCancel()
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Transparent,
                            disabledContainerColor = Transparent
                        ),
                        contentPadding = PaddingValues(12.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(width = 1.5.dp, color = appTextColor)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.cancel),
                                fontFamily = WorkSans,
                                color = appTextColor,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onClickOfPick(timeState)
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = appTextColor,
                            disabledContainerColor = appTextColor
                        ),
                        contentPadding = PaddingValues(12.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.pick),
                                fontFamily = WorkSans,
                                color = colorClockSelectedText,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

//selectorColor = Color.Green

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun Preview() {
    TimePickerDialog( currentHrs = 12,
        currentMins = 10,
        onClickOfCancel = {},
        onClickOfPick = {})
}