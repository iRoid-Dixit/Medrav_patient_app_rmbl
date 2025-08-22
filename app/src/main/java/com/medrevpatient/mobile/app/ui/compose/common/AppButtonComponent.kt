package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@Composable
fun AppButtonComponent(
    modifier: Modifier = Modifier,
    text: String,
    //fontSize: TextUnit = 16.sp,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    onClick: () -> Unit,
    buttonBackgroundColor: Color = White,
    textColors: Color = Black,
    borderColors: Color = White,
    fontWeight: FontWeight = FontWeight.W700,
    fonsSize: Int = 18
) {

    Button(
        onClick = { onClick() },
        border = BorderStroke(width = 1.dp, color = borderColors),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonBackgroundColor,
            disabledContainerColor = Black,

            ),
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth(),

        contentPadding = contentPadding,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontFamily = WorkSans,
                color = textColors,
                fontSize = fonsSize.sp,
                textAlign = TextAlign.Center,
                fontWeight = fontWeight
            )

        }
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        AppButtonComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
            text = "Button",
            buttonBackgroundColor = AppThemeColor,
            textColors = White,
            borderColors = White,

            onClick = {})
    }
}