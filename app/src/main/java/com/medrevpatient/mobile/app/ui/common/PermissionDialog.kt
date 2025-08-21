package com.medrevpatient.mobile.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import com.medrevpatient.mobile.app.ui.theme.AppThemeBlue
import com.medrevpatient.mobile.app.ui.theme.black
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white


@Preview(showBackground = true)
@Composable
private fun DialogWithDoubleButtonPreview() {
    PermissionDialog(
        onDismissRequest = { /*TODO*/ },
        title = "Skai Fitness",
        description = "Allow Skai Fitness to access your storage and camera while you are using the app.",
        negativeText = "Cancel",
        positiveText = "Open Setting"
    ) {

    }
}

@Composable
fun PermissionDialog(
    onDismissRequest: () -> Unit = {},
    title: String,
    description: String = "",
    negativeText: String,
    positiveText: String,
    onPositiveClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(120.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = white)

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(5.dp))

                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    fontFamily = outFit,
                    fontSize = 20.sp,
                    color = black,
                    modifier = Modifier.paddingFromBaseline(bottom = 10.dp)
                )

                if (description.isNotBlank())
                    Text(
                        text = description,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.W500,
                        fontFamily = outFit,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        color = black,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                Spacer(modifier = Modifier.padding(15.dp))

                Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                    androidx.compose.material3.Button(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AppThemeBlue),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = white,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)


                    ) {
                        Text(
                            text = negativeText,
                            fontWeight = FontWeight.W600,
                            fontFamily = outFit,
                            color = AppThemeBlue,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))

                    androidx.compose.material3.Button(
                        onClick = onPositiveClick,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AppThemeBlue), colors = ButtonDefaults.buttonColors(
                            containerColor = AppThemeBlue,
                            contentColor = black
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = positiveText,
                            fontWeight = FontWeight.W600,
                            fontFamily = outFit,
                            fontSize = 14.sp,
                            color = white
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(bottom = 20.dp))
            }
        }
    }
}