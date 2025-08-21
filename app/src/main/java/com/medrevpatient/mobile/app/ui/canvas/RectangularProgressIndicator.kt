package com.medrevpatient.mobile.app.ui.canvas

import androidx.annotation.FloatRange
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.theme.neonNazar
import com.medrevpatient.mobile.app.ui.theme.neonNazarA03
import com.medrevpatient.mobile.app.ui.theme.neonNazarA20
import kotlinx.coroutines.delay


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun RectangularProgressIndicatorPreview() {
    Box(
        modifier = Modifier.size(400.dp),
        contentAlignment = Alignment.Center
    ) {
        var progress by remember { mutableFloatStateOf(0.5f) }

        val animatedProgress = animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(5000, 1000, FastOutSlowInEasing),
            label = "progress"
        )

        RoundedRectangularProgressIndicator(
            progress = { animatedProgress.value },
            roundedRectangularProgressIndicator = RoundedRectangularProgressIndicator(),
            modifier = Modifier
                .size(200.dp)
                .padding(4.dp)
        ) {
            Text("Your Content", color = Color.White)
        }

        LaunchedEffect(Unit) {
            while (progress < 0.5f) {
                progress += 0.01f // Increment progress in steps
                delay(50L) // Delay for smooth animation
            }
        }

    }
}

@Composable
fun RoundedRectangularProgressIndicator(
    progress: () -> Float,
    roundedRectangularProgressIndicator: RoundedRectangularProgressIndicator = RoundedRectangularProgressIndicatorDefaults.RoundedRectangularProgressIndicator,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {

    val fullPath by remember { mutableStateOf(Path()) }  // Full rounded rectangle path
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        roundedRectangularProgressIndicator.apply {

            Canvas(Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                val cornerRadius = (minOf(width, height) * 0.5f)

                val progressPath = Path() // Partial progress path

                //Background of progress bar
                drawRoundRect(
                    color = contentInside.color,
                    topLeft = Offset(
                        x = contentInside.padding.calculateLeftPadding(LayoutDirection.Ltr).toPx(),
                        y = contentInside.padding.calculateTopPadding().toPx()
                    ),
                    size = Size(
                        width = width - contentInside.padding.calculateLeftPadding(LayoutDirection.Ltr)
                            .toPx() - contentInside.padding.calculateRightPadding(
                            LayoutDirection.Ltr
                        ).toPx(),
                        height = height - contentInside.padding.calculateTopPadding()
                            .toPx() - contentInside.padding.calculateBottomPadding().toPx()
                    ),
                    cornerRadius = CornerRadius(cornerRadius / 2)
                )

                if (fullPath.isEmpty) {
                    fullPath.apply {
                        arcTo(
                            rect = Rect(
                                offset = Offset(0f, height - cornerRadius),
                                size = Size(cornerRadius, cornerRadius)
                            ),
                            startAngleDegrees = 90f,
                            sweepAngleDegrees = 90f,
                            forceMoveTo = false
                        )
                        lineTo(x = 0f, y = 0f + cornerRadius)
                        arcTo(
                            rect = Rect(
                                offset = Offset(0f, 0f),
                                size = Size(cornerRadius, cornerRadius)
                            ),
                            startAngleDegrees = 180f,
                            sweepAngleDegrees = 90f,
                            forceMoveTo = false
                        )
                        lineTo(width - cornerRadius, 0f)
                        arcTo(
                            rect = Rect(
                                offset = Offset(width - cornerRadius, 0f),
                                size = Size(cornerRadius, cornerRadius)
                            ),
                            startAngleDegrees = 270f,
                            sweepAngleDegrees = 90f,
                            forceMoveTo = false
                        )
                        lineTo(x = width, y = height - cornerRadius)
                        arcTo(
                            rect = Rect(
                                offset = Offset(width - cornerRadius, height - cornerRadius),
                                size = Size(cornerRadius, cornerRadius)
                            ),
                            startAngleDegrees = 360f,
                            sweepAngleDegrees = 90f,
                            forceMoveTo = false
                        )
                        close()
                    }
                }

                //Draw full path as progress track.
                drawPath(
                    path = fullPath,
                    color = trackColor,
                    style = Stroke(width = strokeWidth.toPx())
                )

                // Use PathMeasure to extract the progress segment
                val pathMeasure = PathMeasure()
                pathMeasure.setPath(fullPath, forceClosed = true)

                val segmentLength = pathMeasure.length * progress()

                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = segmentLength,
                    destination = progressPath
                )

                // Draw the progress path as part of the rounded rectangle
                drawPath(
                    color = progressColor,
                    path = progressPath,
                    style = Stroke(width = strokeWidth.toPx())
                )
            }
        }
        content()
    }
}

object RoundedRectangularProgressIndicatorDefaults {
    val RoundedRectangularProgressIndicator = RoundedRectangularProgressIndicator()
}

data class RoundedRectangularProgressIndicator(
    @FloatRange(from = 0.0, to = 1.0) val cornerRadius: Float = 0.5f,
    val strokeWidth: Dp = 4.dp,
    val trackColor: Color = neonNazarA20,
    val progressColor: Color = neonNazar,
    val contentInside: Content = Content()
) {
    data class Content(
        val color: Color = neonNazarA03,
        val padding: PaddingValues = PaddingValues(8.dp)
    )
}