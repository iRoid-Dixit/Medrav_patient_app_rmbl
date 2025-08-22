package com.medrevpatient.mobile.app.ui.compose.common.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import kotlin.text.ifEmpty

@Composable
fun AppUpdateDialog(
    isForceUpdate: Boolean = false,
    appUpdateMessage: String = "Required to update the app for better performance.",
    onClickOfCancel: () -> Unit = {},
    onCLickOfUpdate: () -> Unit = {}
) {
    Dialog(onDismissRequest = {
        // Prevent dismissal for force updates
        if (!isForceUpdate) {
            onClickOfCancel()
        }
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(10.dp))

                // Title
                Text(
                    text = if (isForceUpdate) "Update Required" else "Update Available",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = WorkSans,
                    fontSize = 16.sp,
                    color = AppThemeColor,
                    modifier = Modifier.paddingFromBaseline(bottom = 8.dp)
                )

                // Message
                Text(
                    text = appUpdateMessage.ifEmpty {
                        if (isForceUpdate)
                            "A new version is available. Please update to continue using the app."
                        else
                            "A new version is available. Do you want to update now?"
                    },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontFamily = WorkSans,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = AppThemeColor,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.padding(12.dp))
                HorizontalDivider()

                Row {
                    if (!isForceUpdate) {
                        androidx.compose.material3.Button(
                            onClick = { onClickOfCancel() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = White,
                                contentColor = Color.Blue
                            ),
                            modifier = Modifier.widthIn(113.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = WorkSans,
                                fontSize = 14.sp,
                            )
                        }
                        Spacer(modifier = Modifier.padding(10.dp))
                        VerticalDivider(
                            modifier = Modifier.height(50.dp)
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                    }
                    androidx.compose.material3.Button(
                        onClick = { onCLickOfUpdate() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Color.Red
                        ),
                        modifier = Modifier.widthIn(113.dp)
                    ) {
                        Text(
                            text = "Update",
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = WorkSans,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppUpdateDialog(
        isForceUpdate = false,
        appUpdateMessage = "A new version is available with exciting new features and improved performance."
    )
}

@Preview(showBackground = true)
@Composable
private fun ForceUpdatePreview() {
    AppUpdateDialog(
        isForceUpdate = true,
        appUpdateMessage = "A critical update is required to continue using the app. Please update now."
    )
}