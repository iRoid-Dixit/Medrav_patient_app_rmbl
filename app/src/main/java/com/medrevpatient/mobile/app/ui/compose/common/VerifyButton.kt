package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600

@Composable
fun VerifyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    verifyButtonText: String,
    verifyButtonBackgroundColor: Color,
    textColors: Color,
) {
    Text(
        text = verifyButtonText,
        fontFamily = nunito_sans_400,
        fontSize = 12.sp,
        color = textColors,
        modifier = modifier
            .background(
                color =verifyButtonBackgroundColor,
                shape = RoundedCornerShape(16.dp)
            )

            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clickable { onClick() }
    )
}

@Preview
@Composable
fun VerifyButtonPreview(modifier: Modifier = Modifier) {
    VerifyButton(
        text = "Verify",
        onClick = { /*TODO*/ },
        modifier = modifier,
        verifyButtonText = "Verify",
        verifyButtonBackgroundColor = AppThemeColor.copy(alpha = 0.1f),
        textColors = AppThemeColor
    )

}


