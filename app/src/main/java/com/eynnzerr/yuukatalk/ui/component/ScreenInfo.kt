package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RememberScreenInfo(): ScreenInfo {
    val configuration = LocalConfiguration.current
    return ScreenInfo(
        widthType = when {
            configuration.screenWidthDp < 840 -> ScreenInfo.ScreenType.Compact
            configuration.screenWidthDp < 1280 -> ScreenInfo.ScreenType.Medium
            else -> ScreenInfo.ScreenType.Expanded
        },
        heightType = when {
            configuration.screenHeightDp < 900 -> ScreenInfo.ScreenType.Compact
            configuration.screenHeightDp < 1080 -> ScreenInfo.ScreenType.Medium
            else -> ScreenInfo.ScreenType.Expanded
        },
        widthDp = configuration.screenWidthDp.dp,
        heightDp = configuration.screenHeightDp.dp
    )
}

data class ScreenInfo(
    val widthType: ScreenType,
    val heightType: ScreenType,
    val widthDp: Dp,
    val heightDp: Dp,
) {
    sealed class ScreenType {
        data object Compact: ScreenType()
        data object Medium: ScreenType()
        data object Expanded: ScreenType()
    }
}