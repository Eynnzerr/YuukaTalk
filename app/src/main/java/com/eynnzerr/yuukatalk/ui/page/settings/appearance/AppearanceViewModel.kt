package com.eynnzerr.yuukatalk.ui.page.settings.appearance

import androidx.lifecycle.ViewModel
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.ui.theme.PaletteOption
import com.eynnzerr.yuukatalk.utils.AppearanceUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AppearanceState(
    val currentOption: Int,
    val currentSeedColor: Long,
)

@HiltViewModel
class AppearanceViewModel @Inject constructor(
    private val mmkv: MMKV
): ViewModel() {
    private val _uiState = MutableStateFlow(
        AppearanceState(
            currentOption = mmkv.decodeInt(PreferenceKeys.APPEARANCE_OPTION, PaletteOption.SELF_ASSIGNED),
            currentSeedColor = mmkv.decodeLong(PreferenceKeys.SEED_COLOR, 0xFF148AFD)
        )
    )
    val uiState = _uiState.asStateFlow()

    fun updatePaletteOption(option: Int) {
        _uiState.update { it.copy(currentOption = option) }
        AppearanceUtils.updatePaletteOption(option)
    }

    fun updateSeedColor(color: Long) {
        _uiState.update { it.copy(currentSeedColor = color) }
        AppearanceUtils.updateSeedColor(color)
    }
}