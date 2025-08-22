package com.griotlegacy.mobile.app.ux.main

import android.content.res.Resources
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

// Constants for FAB cutout
private const val smallCornerRadius: Float = 15f
private const val buttonRadius: Float = 30f
private const val buttonPadding: Float = 10f

class BottomNavCurve : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(path = Path().apply {
            var x = 0f
            var y = size.height
            moveTo(x, y)
            // *No rounded top-left corner, straight line*
            lineTo(0f, buttonRadius.convertFloatToPixel())

            // Move to FAB cutout
            x = size.width / 2 - buttonRadius.convertFloatToPixel() - (buttonPadding.convertFloatToPixel() / 2) - smallCornerRadius.convertFloatToPixel()
            y = buttonRadius.convertFloatToPixel() - smallCornerRadius.convertFloatToPixel()
            arcTo(
                Rect(
                    x - smallCornerRadius.convertFloatToPixel(), y - smallCornerRadius.convertFloatToPixel(),
                    x + smallCornerRadius.convertFloatToPixel(), y + smallCornerRadius.convertFloatToPixel()
                ), 90f, -55f, false
            )

            // Main half-circle FAB cutout
            x = size.width / 2
            y += smallCornerRadius.convertFloatToPixel() + buttonPadding.convertFloatToPixel()
            val startAngle = (215).toFloat()
            val endAngle = (325).toFloat()
            arcTo(
                Rect(
                    x - (buttonRadius.convertFloatToPixel() + buttonPadding.convertFloatToPixel()), y - (buttonRadius.convertFloatToPixel() + buttonPadding.convertFloatToPixel()),
                    x + (buttonRadius.convertFloatToPixel() + buttonPadding.convertFloatToPixel()), y + (buttonRadius.convertFloatToPixel() + buttonPadding.convertFloatToPixel())
                ), startAngle, endAngle - startAngle, false
            )

            // Trailing small corner
            x += buttonRadius.convertFloatToPixel() + (buttonPadding.convertFloatToPixel() / 2) + smallCornerRadius.convertFloatToPixel()
            y = buttonRadius.convertFloatToPixel() - smallCornerRadius.convertFloatToPixel()
            arcTo(
                Rect(
                    x - smallCornerRadius.convertFloatToPixel(), y - smallCornerRadius.convertFloatToPixel(),
                    x + smallCornerRadius.convertFloatToPixel(), y + smallCornerRadius.convertFloatToPixel(),
                ), 145f, -55f, false
            )

            // *No rounded top-right corner, straight line*
            lineTo(size.width, buttonRadius.convertFloatToPixel())

            // Complete bottom
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        })
    }

    private fun Float.convertFloatToPixel(): Float {
        val metrics = Resources.getSystem().displayMetrics
        return this * (metrics.densityDpi / 160f)
    }
}