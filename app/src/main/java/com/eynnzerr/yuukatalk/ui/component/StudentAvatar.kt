package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    onClick: (() -> Unit)? = null
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
                border(2.dp, Color.Yellow, CircleShape)
            })
            .conditional(onClick != null, {
                clickable { onClick!!.invoke() }
            })
    )
}

@Preview(showBackground = true)
@Composable
fun StudentAvatarPreview() {
    StudentAvatar(
        url = "file:///android_asset/shiroko/emoji_0.png",
        size = 64.dp,
        withBorder = true,
        onClick = {}
    )
}