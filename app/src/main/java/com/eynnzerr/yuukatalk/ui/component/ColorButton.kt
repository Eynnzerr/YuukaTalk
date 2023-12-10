package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ColorButton(
    color: Long,
    selected: Boolean,
    size: Dp = 48.dp,
    onSelect: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable { onSelect() }
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(Color(color), CircleShape)
        )

        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Preview
@Composable
fun ColorButtonPreview() {
    ColorButton(
        color = 0xFF148AFD,
        selected = true,
        onSelect = { }
    )
}