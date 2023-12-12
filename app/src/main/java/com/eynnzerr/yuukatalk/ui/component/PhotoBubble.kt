package com.eynnzerr.yuukatalk.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation

@Composable
fun PhotoBubble(
    uri: String,
    isMyMessage: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 192.dp
) {
    Log.d(TAG, "PhotoBubble: uri: $uri")
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = modifier
                .align(if (isMyMessage) Alignment.End else Alignment.Start)
                .padding(bottom = 8.dp)
                .width(size)
                .heightIn(size, Dp.Unspecified),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            // 无意发现AsyncImage一个bug:只有外面被row/column包一层后，才能在bitmap上绘制出来
            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .transformations(RoundedCornersTransformation())
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
            )
        }
    }
}

private const val TAG = "PhotoBubble"