
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.White


@Composable
fun CustomSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 50.dp,
    height: Dp = 150.dp, // Default dimensions
    checkedTrackColor: androidx.compose.ui.graphics.Color = White,
    uncheckedTrackColor: androidx.compose.ui.graphics.Color = White,
    thumbColor: androidx.compose.ui.graphics.Color = AppThemeColor,
    gapBetweenThumbAndTrackEdge: Dp = 4.dp,
    elevation: Dp = 0.dp
) {
    // Convert Dp to Px using LocalDensity
    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge
    val thumbRadiusPx = with(LocalDensity.current) { (height / 2 - gapBetweenThumbAndTrackEdge).toPx() }
    val trackWidthPx = with(LocalDensity.current) { width.toPx() }
    val gapPx = with(LocalDensity.current) { gapBetweenThumbAndTrackEdge.toPx() }

    // Animating the thumb's position
    val thumbOffsetX = animateFloatAsState(
        targetValue = if (isChecked)
            trackWidthPx - (thumbRadiusPx * 2) - gapPx // Position when checked
        else
            gapPx, // Position when unchecked
        label = "ThumbOffset"
    )

    // Layout for the switch
    Box(
        modifier = modifier
            .size(width, height) // Dynamic size
            .clip(RoundedCornerShape(height / 2)) // Rounded corners for track
            .background(if (isChecked) checkedTrackColor else uncheckedTrackColor) // Dynamic color
            .clickable { onCheckedChange(!isChecked) },// Toggle state on click
        contentAlignment = Alignment.CenterStart // Align child horizontally inside
    ) {
        // Thumb of the switch
        Box(
            modifier = Modifier
                .size(thumbRadius * 2) // Thumb size based on heightt
                .offset { IntOffset(thumbOffsetX.value.toInt(), 0) } // Animated position
                .clip(shape = RoundedCornerShape(56.dp)) // Circular thumb
                .background(thumbColor) // Thumb color
                .shadow(elevation, CircleShape) // Shadow for thumb
        )
    }
}

