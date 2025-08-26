package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700


@Composable
fun AppButtonComponent(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        enabled = isEnabled && !isLoading, // Use isEnabled parameter
        contentPadding = contentPadding,
        modifier = modifier
            .background(
                brush = if (isEnabled) {
                    Brush.linearGradient(colors = listOf(AppThemeColor, SteelGray))
                } else {
                    Brush.linearGradient(colors = listOf(
                        AppThemeColor.copy(alpha = 0.5f),
                        SteelGray.copy(alpha = 0.5f)

                    ))
                },
                shape = RoundedCornerShape(12.dp)
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    strokeCap = StrokeCap.Round,
                    color = White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = text,
                    fontFamily = nunito_sans_700,
                    color = if (isEnabled) White else White,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    fontWeight = fontWeight
                )
            }
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
            text = "Click Me", // Changed from buttonText to text
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
            isLoading = false,
            isEnabled = true, // Added isEnabled parameter

            onClick = {}
        )
    }
}