package com.medrevpatient.mobile.app.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun TitleDualFont(
    modifier: Modifier = Modifier,
    color: Color,
    fontWeightBold: FontWeight,
    fontWeightRegular: FontWeight,
    fontSize: Int,
    fontSize2: Int = 20,
    fontFamilyBold: androidx.compose.ui.text.font.FontFamily,
    fontFamilyRegular: androidx.compose.ui.text.font.FontFamily,
    titlePart1: String,
    titlePart2: String,
    textAlign: TextAlign = TextAlign.Center,
    onClick: () -> Unit = {},
    isFromResend: Boolean = false,
    isEnable: Boolean = true
) {
    val title = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = color,
                fontWeight = fontWeightBold,
                fontSize = fontSize.sp,
                fontFamily = fontFamilyBold
            )
        ) {
            append(titlePart1)
        }
        withStyle(
            style = SpanStyle(
                color = color,
                fontWeight = fontWeightRegular,
                fontSize = if (fontSize2 != 20) 20.sp else fontSize.sp,
                fontFamily = fontFamilyRegular,
            )
        ) {
            append(" ".plus(titlePart2))
        }
    }
    Text(text = title, modifier = modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        when {
            isFromResend -> {
                if (isEnable) {
                    onClick()
                }
            }

            else -> {
                onClick()
            }
        }
    }, textAlign = textAlign
    )
}