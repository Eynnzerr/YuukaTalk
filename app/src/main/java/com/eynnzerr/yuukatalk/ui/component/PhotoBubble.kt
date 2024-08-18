package com.eynnzerr.yuukatalk.ui.component

import android.util.Base64
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.eynnzerr.yuukatalk.ui.common.LocalTalkPieceProperty
import com.eynnzerr.yuukatalk.utils.ImageUtils

@Composable
fun PhotoBubble(
    uri: String,
    isMyMessage: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = LocalTalkPieceProperty.current.photoWidth
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = modifier
                .align(if (isMyMessage) Alignment.End else Alignment.Start)
                .padding(bottom = LocalTalkPieceProperty.current.verticalMargin)
                .width(size),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            // 无意发现AsyncImage一个bug:只有外面被row/column包一层后，才能在bitmap上绘制出来
            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(if (ImageUtils.isImageBase64(uri)) Base64.decode(uri, Base64.DEFAULT) else uri)
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