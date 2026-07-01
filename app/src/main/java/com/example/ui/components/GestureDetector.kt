package com.example.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun GestureArea(
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onDoubleTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    swipeThreshold: Float = 120f,
    content: @Composable () -> Unit
) {
    var totalDragY by remember { mutableFloatStateOf(0f) }
    var totalDragX by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() },
                    onLongPress = { onLongPress() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        totalDragY = 0f
                        totalDragX = 0f
                    },
                    onDragEnd = {
                        val absX = Math.abs(totalDragX)
                        val absY = Math.abs(totalDragY)
                        if (absY > absX) {
                            if (absY > swipeThreshold) {
                                if (totalDragY < 0) {
                                    onSwipeUp()
                                } else {
                                    onSwipeDown()
                                }
                            }
                        } else {
                            if (absX > swipeThreshold) {
                                if (totalDragX < 0) {
                                    onSwipeLeft()
                                } else {
                                    onSwipeRight()
                                }
                            }
                        }
                        totalDragY = 0f
                        totalDragX = 0f
                    },
                    onDragCancel = {
                        totalDragY = 0f
                        totalDragX = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragY += dragAmount.y
                        totalDragX += dragAmount.x
                    }
                )
            }
    ) {
        content()
    }
}
