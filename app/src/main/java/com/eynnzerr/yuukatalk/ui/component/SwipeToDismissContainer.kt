package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissContainer(
    modifier: Modifier = Modifier,
    onConfirmDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    var currentProgress by remember {
        mutableFloatStateOf(0f)
    }
    val dismissState = rememberDismissState(
        confirmValueChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                if (currentProgress >= 0.5f && currentProgress < 1) {
                    onConfirmDismiss()
                    return@rememberDismissState true
                }
            }
            false
        },
        positionalThreshold = { total ->
            total / 2
        }
    )

    ForUpdateData {
        currentProgress = dismissState.progress
    }

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        background = {
            val color by animateColorAsState(  //一些动画
                when (dismissState.targetValue) {
                    DismissValue.Default -> MaterialTheme.colorScheme.surface
                    DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
                    DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error
                }, label = ""
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f, label = ""
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    modifier = Modifier.scale(scale),
                    contentDescription = "Delete",
                )
            }
        },
        dismissContent = { content() },
        directions = setOf(DismissDirection.EndToStart)
    )
}

@Composable
private fun ForUpdateData(onUpdate: () -> Unit) {
    onUpdate()
}