package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.yuukatalk.ui.theme.YuukaTalkTheme

@Composable
fun ChatBubble(
    text: String,
    isMyMessage: Boolean,
    showArrow: Boolean
) {
    val bubbleColor = if (isMyMessage) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .drawBehind {
                val cornerRadius = 8.dp.toPx()
                val triangleWidth = 8.dp.toPx()
                val triangleHeight = 12.dp.toPx()
                val triangleOffset = 8.dp.toPx()

                if (showArrow) {
                    val triangleStartX =
                        if (isMyMessage) size.width - triangleWidth else triangleWidth
                    val triangleStartY = triangleOffset
                    val triangleEndX = if (isMyMessage) size.width else 0f
                    val triangleEndY = triangleOffset + triangleHeight

                    drawTriangle(
                        start = Offset(triangleStartX, triangleStartY),
                        end = Offset(triangleEndX, triangleEndY),
                        color = bubbleColor
                    )
                }

                drawRoundRect(
                    color = bubbleColor,
                    topLeft = Offset(triangleWidth, 0f),
                    size = Size(size.width - triangleWidth * 2, size.height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                )
            }
            .clip(RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

fun DrawScope.drawTriangle(
    start: Offset,
    end: Offset,
    color: Color
) {
    drawPath(
        path = Path().apply {
            moveTo(start.x, start.y)
            lineTo(end.x, start.y + (end.y - start.y) / 2)
            lineTo(start.x, end.y)
            close()
        },
        color = color
    )
}

@Preview(
    name = "chatBubble",
    showBackground = true
)
@Composable
fun ChatBubblePreview() {
    YuukaTalkTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ChatBubble(
                text = "Hello, this is a chat bubble with a triangular arrow on the left. hahaha!",
                isMyMessage = true,
                showArrow = true
            )

            ChatBubble(
                text = "Can you see me?",
                isMyMessage = true,
                showArrow = false
            )

            ChatBubble(
                text = "sure.",
                isMyMessage = false,
                showArrow = true
            )

            ChatBubble(
                text = "And this is a chat bubble for an incoming message with a triangular arrow on the right.",
                isMyMessage = false,
                showArrow = false
            )
        }
    }
}