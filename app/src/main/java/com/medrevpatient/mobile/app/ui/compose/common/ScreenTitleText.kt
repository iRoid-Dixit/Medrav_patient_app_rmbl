package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.appTextColor
import com.medrevpatient.mobile.app.ui.theme.Sarabun

@Composable
fun ScreenTitleText(
    modifier: Modifier = Modifier,
    titleText: String = "",
    titleTextAlign: TextAlign = TextAlign.Center,
    textSize: TextUnit = TextUnit.Unspecified,
    textFontFamily: FontFamily = Sarabun,
    textFontWeight: FontWeight = FontWeight.Bold,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = titleText,
        textAlign = titleTextAlign,
        fontSize = textSize,
        color = appTextColor,
        fontFamily = textFontFamily,
        fontWeight = textFontWeight,
        lineHeight = lineHeight
    )
}


@Preview
@Composable
private fun Preview() {
    Surface {
        ScreenTitleText(
            titleText = "Though Rise",
            textSize = 30.sp
        )
    }
}