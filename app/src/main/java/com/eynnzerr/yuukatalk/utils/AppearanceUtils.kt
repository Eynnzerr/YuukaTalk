package com.eynnzerr.yuukatalk.utils

import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.ui.theme.PaletteOption
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AppearanceSettings(
    val paletteOption: Int,
    val assignedSeedColor: Long,
)

object AppearanceUtils {
    private val mmkv = MMKV.defaultMMKV()
    private val _appearanceState = MutableStateFlow(
        AppearanceSettings(
            paletteOption = mmkv.decodeInt(PreferenceKeys.APPEARANCE_OPTION, PaletteOption.SELF_ASSIGNED),
            assignedSeedColor = mmkv.decodeLong(PreferenceKeys.SEED_COLOR, 0xFF148AFD)
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
}