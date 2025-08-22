package com.griotlegacy.mobile.app.ui.compose.common

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minZoom: Float = 1f,
    maxZoom: Float = 2f,
    onZoom: (Float) -> Unit = {},
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                detectTransformGestures(
                    onGesture = { _, pan, zoom, _ ->
                        coroutineScope.launch {
                            val newScale = (scale.value * zoom).coerceIn(minZoom, maxZoom)
                            scale.snapTo(newScale)

                            if (newScale > 1f) {
                                val newOffset = offset.value + pan
                                offset.snapTo(newOffset)
                            }

                            onZoom(newScale)
                        }
                    }
                )
            }
            .pointerInput(enabled) {
                detectTapGestures(
                    onDoubleTap = {
                        coroutineScope.launch {
                            // Double tap to zoom in/out
                            if (scale.value > 1f) {
                                scale.animateTo(1f, tween(300))
                                offset.animateTo(Offset.Zero, tween(300))
                            } else {
                                scale.animateTo(1.5f, tween(300))
                            }
                        }
                    }
                )
            }
            .pointerInput(enabled) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.all { it.changedToUp() }) {
                            // When fingers lifted, animate back to normal if below threshold
                            coroutineScope.launch {
                                if (scale.value < 1.01f) {
                                    scale.animateTo(1f, tween(300))
                                    offset.animateTo(Offset.Zero, tween(300))
                                }
                            }
                        }
                    }
                }
            }
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                translationX = offset.value.x
                translationY = offset.value.y
            }
    ) {
        content()
    }
}