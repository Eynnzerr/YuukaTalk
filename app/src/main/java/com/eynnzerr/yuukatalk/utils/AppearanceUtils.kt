package com.eynnzerr.yuukatalk.utils

import android.util.Log
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.ui.theme.PaletteOption
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AppearanceSettings(
    val paletteOption: Int,
    val assignedSeedColor: Long,
    val fontResource: Int,
)

object AppearanceUtils {
    private val mmkv = MMKV.defaultMMKV()
    private val _appearanceState = MutableStateFlow(
        AppearanceSettings(
            paletteOption = mmkv.decodeInt(PreferenceKeys.APPEARANCE_OPTION, PaletteOption.SELF_ASSIGNED),
            assignedSeedColor = mmkv.decodeLong(PreferenceKeys.SEED_COLOR, 0xFF148AFD),
            fontResource = mmkv.decodeInt(PreferenceKeys.FONT_RESOURCE, 0)
        )
    )
    val appearanceState = _appearanceState.asStateFlow()

    fun updatePaletteOption(option: Int) {
        _appearanceState.update { it.copy(paletteOption = option) }
        mmkv.encode(PreferenceKeys.APPEARANCE_OPTION, option)
    }

    fun updateSeedColor(color: Long) {
        _appearanceState.update { it.copy(assignedSeedColor = color) }
        mmkv.encode(PreferenceKeys.SEED_COLOR, color)
    }

    fun updateExportBackground(color: String) {
        // 如果 background 设定为follow app，则更新背景颜色设定
        val background = mmkv.decodeString(PreferenceKeys.BACKGROUND_COLOR, "#fff7e3")
        if (background != "#fff7e3" && background != "#ffffff") {
            Log.d(TAG, "updateExportBackground: change export background: $color")
            mmkv.encode(PreferenceKeys.BACKGROUND_COLOR, color)
        }
    }

    fun updateFont(fontResource: Int) {
        _appearanceState.update { it.copy(fontResource = fontResource) }
        mmkv.encode(PreferenceKeys.FONT_RESOURCE, fontResource)
    }

    private const val TAG = "AppearanceUtils"
}