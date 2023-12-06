package com.eynnzerr.yuukatalk.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class SettingsItem(
    val title: String,
    val desc: String? = null,
    val icon: ImageVector? = null,
    val onClick: () -> Unit,
)
