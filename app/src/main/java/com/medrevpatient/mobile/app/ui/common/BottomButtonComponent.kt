package com.medrevpatient.mobile.app.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white

@Composable
fun BottomButtonComponent(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = outFit,
    textColor: Color = white,
    textStyle: TextStyle = TextStyle.Default,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MineShaft
    )
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth(), colors = buttonColors
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp,
            color = textColor,
            style = textStyle,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}