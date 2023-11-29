package com.eynnzerr.yuukatalk.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class SpecialPieceEntryItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)