package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.utils.PathUtils
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmojiPickerDialog(
    character: Character,
    onDismissRequest: () -> Unit,
    onDismiss: () -> Unit,
    onPickEmoji: (String) -> Unit,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { 2 }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(id = R.string.btn_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.EmojiEmotions,
                contentDescription = "emoji dialog icon"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.title_emoji_dialog))
        },
        text = {
            Column {
                Row(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
                HorizontalPager(state = pagerState) { page ->
                    val emojiData = if (page == 0) character.getEmojiPaths(context) else PathUtils.getCommonEmojiPaths(context)
                    LazyVerticalGrid(
                        modifier = Modifier
                            .heightIn(0.dp, 350.dp)
                            .graphicsLayer {
                                val pageOffset = (
                                        (pagerState.currentPage - page) + pagerState
                                            .currentPageOffsetFraction
                                        ).absoluteValue

                                alpha = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                            },
                        columns = GridCells.Adaptive(minSize = 64.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = emojiData,
                            key = { it.hashCode() }
                        ) { path ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                onClick = { onPickEmoji(path) }
                            ) {
                                AsyncImage(
                                    model = ImageRequest
                                        .Builder(context)
                                        .data(path)
                                        .crossfade(true)
                                        .build(),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}