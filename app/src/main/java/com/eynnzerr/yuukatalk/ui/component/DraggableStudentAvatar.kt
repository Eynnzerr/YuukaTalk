package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

@Composable
fun DraggableStudentAvatar(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    withBorder: Boolean = false,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
    threshold: Float = 200f,
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var removed by remember { mutableStateOf(false) }

    if (!removed) {
        Box(
            modifier = modifier
                .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragEnd = {
                            if (offset.getDistance() > threshold) {
                                removed = true
                                onRemove?.invoke()
                            } else {
                                offset = Offset.Zero
                            }
                        }
                    ) { change, dragAmount ->
                        offset += dragAmount
                        change.consume()
                    }
                },
        ) {
            StudentAvatar(
                url = url,
                size = size,
                withBorder = withBorder,
                isSelected = isSelected,
                onClick = onClick
            )
        }
    }
}
