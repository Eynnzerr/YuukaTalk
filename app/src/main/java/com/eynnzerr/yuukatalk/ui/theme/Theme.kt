package com.eynnzerr.yuukatalk.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun YuukaTalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    paletteOption: Int = PaletteOption.SELF_ASSIGNED,
    typography: Typography = Typography,
    seedColor: Color = BlueArchiveTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = when (paletteOption) {
        PaletteOption.DYNAMIC  -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                LightColorScheme
            }
        }
        PaletteOption.DARK -> DarkColorScheme
        PaletteOption.LIGHT -> LightColorScheme
        PaletteOption.FOLLOW_SYSTEM -> if (darkTheme) DarkColorScheme else LightColorScheme
        else -> dynamicColorScheme(seedColor, darkTheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
    )
}