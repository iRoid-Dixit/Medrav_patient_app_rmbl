package com.griotlegacy.mobile.app.ui.compose.common.progress

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HalfCircularProgress(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.DarkGray,
    progressColor: Color = Color.White,
    strokeWidth: Dp = 10.dp
) {
    val stroke = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val topLeft = Offset(
            (size.width - diameter) / 2,
            (size.height - diameter) / 2
        )

        val extraAngle = 90f
        val arcStartAngle = 180f - (extraAngle / 2)  // 135f
        val arcSweepAngle = 180f + extraAngle       // 270f

        // Background arc
        drawArc(
            color = backgroundColor,
            startAngle = arcStartAngle,
            sweepAngle = arcSweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        // Foreground arc (progress)
        drawArc(
            color = progressColor,
            startAngle = arcStartAngle,
            sweepAngle = arcSweepAngle * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}