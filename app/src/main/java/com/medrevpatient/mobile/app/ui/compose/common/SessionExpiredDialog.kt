package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.medrevpatient.mobile.app.ui.theme.Transparent
import com.medrevpatient.mobile.app.ui.theme.appTextColor
import com.medrevpatient.mobile.app.ui.theme.appTextColor60
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@Composable
fun SessionExpiredDialog(
    onClickOk: () -> Unit = {}
) {

    Dialog(onDismissRequest = {}) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Session Expired",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = WorkSans,
                    fontSize = 16.sp,
                    color = appTextColor,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                )

                Text(
                    text = "Your session has expired.Please log in again.",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontFamily = WorkSans,
                    fontSize = 14.sp,
                    color = appTextColor60,
                )

                Spacer(modifier = Modifier.padding(5.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .background(
                            color = appTextColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .height(1.dp),
                    color = Transparent
                )

                TextButton(
                    onClick = { onClickOk() },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = "OK",
                        color = appTextColor,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = WorkSans,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SessionExpiredDialog()
}