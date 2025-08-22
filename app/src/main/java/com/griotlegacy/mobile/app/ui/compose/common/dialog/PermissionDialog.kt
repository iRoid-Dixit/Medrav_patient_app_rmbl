package com.griotlegacy.mobile.app.ui.compose.common.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.widthIn
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
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.Sarabun
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.WorkSans

@Preview(showBackground = true)
@Composable
private fun DialogWithDoubleButtonPreview() {
    PermissionDialog(
        onDismissRequest = { /*TODO*/ },
        title = "Legacy Cache App",
        description = "Allow “Legacy Cache App” to access your storage and camera while you are using the app.",
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
            colors = CardDefaults.cardColors(containerColor = White)

        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    fontFamily = WorkSans,
                    fontSize = 20.sp,
                    color = AppThemeColor,
                    modifier = Modifier.paddingFromBaseline(bottom = 15.dp)
                )
                if (description.isNotBlank())
                    Text(
                        text = description,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.W500,
                        fontFamily = WorkSans,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        color = AppThemeColor,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                Spacer(modifier = Modifier.padding(20.dp))
                Row {
                    androidx.compose.material3.Button(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AppThemeColor),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.widthIn(113.dp)
                    ) {
                        Text(
                            text = negativeText,
                            fontWeight = FontWeight.W600,
                            fontFamily = Sarabun,
                            color = AppThemeColor,
                            fontSize = 16.sp,
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                    androidx.compose.material3.Button(
                        onClick = onPositiveClick,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AppThemeColor),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppThemeColor,
                            contentColor = White
                        ),
                        modifier = Modifier.widthIn(113.dp)
                    ) {
                        Text(
                            text = positiveText,
                            fontWeight = FontWeight.W600,
                            fontFamily = Sarabun,
                            fontSize = 16.sp,
                            color = White
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(bottom = 20.dp))
            }
        }
    }
}