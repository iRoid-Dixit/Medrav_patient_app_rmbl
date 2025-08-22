package com.medrevpatient.mobile.app.ui.compose.common
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White30
@Composable
fun HorizontalPagerIndicator(
    count: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    indicatorSpacing: Dp = 7.dp,
    indicatorColor: Color = White30,
    indicatorSelectedColor: Color = White
) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(indicatorSpacing)
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (index == selectedIndex) indicatorSelectedColor else indicatorColor
                    )
            )
        }
    }
}
