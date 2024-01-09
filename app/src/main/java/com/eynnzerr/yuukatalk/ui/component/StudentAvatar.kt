package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.eynnzerr.yuukatalk.ui.ext.conditional

@Composable
fun StudentAvatar(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    withBorder: Boolean = false,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = "current student avatar",
            modifier = modifier
                .size(size)
                .conditional(withBorder, {
                    border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                })
                .conditional(onClick != null, {
                    clickable { onClick!!.invoke() }
                })
        )

        // Scrim
        if (!isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.5f),
                            )
                        ),
                        CircleShape
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentAvatarPreview() {
    StudentAvatar(
        url = "file:///android_asset/abydos/shiroko/avatar/avatar_0.webp",
        size = 64.dp,
        withBorder = false,
        isSelected = false,
        onClick = {}
    )
}