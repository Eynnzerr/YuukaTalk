package com.eynnzerr.yuukatalk

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.eynnzerr.yuukatalk.ui.page.crash.CrashReportPage
import com.eynnzerr.yuukatalk.ui.theme.YuukaTalkTheme
import com.eynnzerr.yuukatalk.ui.theme.getTypography
import com.eynnzerr.yuukatalk.utils.AppearanceUtils

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setImmerseStatusBar(window)
        val message = intent.getStringExtra("msg") ?: "unknown error!"

        setContent {
            val appearanceState by AppearanceUtils.appearanceState.collectAsState()
            val clipboardManager = LocalClipboardManager.current
            YuukaTalkTheme(
                paletteOption = appearanceState.paletteOption,
                seedColor = Color(appearanceState.assignedSeedColor),
            ) {
                CrashReportPage(
                    message = message,
                    onConfirm = {
                        clipboardManager.setText(AnnotatedString(message))
                        this.finishAffinity()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) finishAffinity()
    }

    private fun setImmerseStatusBar(window: Window) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
    }
}