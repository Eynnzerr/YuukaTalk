package com.eynnzerr.yuukatalk.ui.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

@Composable
fun ColorScheme.surfaceColorAtElevation(
    elevation: Dp,
    color: Color = surface,
): Color = remember(this, elevation, color) { color.atElevation(surfaceTint, elevation) }

fun Color.atElevation(
    sourceColor: Color,
    elevation: Dp,
): Color {
    if (elevation == 0.dp) return this
    return sourceColor.copy(alpha = elevation.alphaLN(constant = 4.5f)).compositeOver(this)
}

fun Color.toHexLong(): Long =
    (alpha * 255).toLong() shl 24 or
    (red * 255).toLong() shl 16 or
    (green * 255).toLong() shl 8 or
    (blue * 255).toLong()

fun Color.toHexString(): String {
    val alpha = (alpha * 255).toInt()
    val red = (red * 255).toInt()
    val green = (green * 255).toInt()
    val blue = (blue * 255).toInt()
    return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
}

fun Dp.alphaLN(constant: Float = 1f, weight: Float = 0f): Float =
    ((constant * ln(value + 1) + weight) + 2f) / 100f