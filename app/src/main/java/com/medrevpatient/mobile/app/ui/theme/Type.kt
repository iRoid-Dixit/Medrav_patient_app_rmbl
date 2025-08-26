package com.medrevpatient.mobile.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
val WorkSans = FontFamily(
    Font(R.font.nunito_sans_light)
)

val nunito_sans_300 = FontFamily(
    Font(R.font.nunito_sans_light)
)

val nunito_sans_400 = FontFamily(
    Font(R.font.nunito_sans_regular)
)

val nunito_sans_500 = FontFamily(
    Font(R.font.nunito_sans_medium)
)

val nunito_sans_600 = FontFamily(
    Font(R.font.nunito_sans_semi_bold)
)

val nunito_sans_700 = FontFamily(
    Font(R.font.nunito_sans_bold)
)

val nunito_sans_800 = FontFamily(
    Font(R.font.nunito_sans_extra_bold)
)
val nunito_sans_900 = FontFamily(
    Font(R.font.nunito_sans_black)
)
