package com.eynnzerr.yuukatalk.ui.page.settings.preview

import androidx.lifecycle.ViewModel
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.utils.AppearanceUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PreviewState(
    val currentFontResource: Int,
    val fontName: String,
    val fontResources: Map<Int, String>
)

private val fontResourcesMap = mapOf(
    0 to "Default",
    R.font.yahei to "雅黑",
    R.font.xihei to "细黑",
    R.font.kaiti to "楷体",
    R.font.zhongsong to "中宋",
    R.font.ubuntu_mono to "Ubuntu Mono",
    R.font.jetbrains_mono_nerd to "Jetbrains Mono"
)

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val mmkv: MMKV
): ViewModel() {
    private val _uiState = MutableStateFlow(
        PreviewState(
            currentFontResource = mmkv.decodeInt(PreferenceKeys.FONT_RESOURCE, 0),
            fontResources = fontResourcesMap,
            fontName = fontResourcesMap[mmkv.decodeInt(PreferenceKeys.FONT_RESOURCE, 0)] ?: "default"
        ),
    )
    val uiState = _uiState.asStateFlow()

    fun updateFontResource(resource: Int) {
        _uiState.update {
            it.copy(
                currentFontResource = resource,
                fontName = fontResourcesMap[resource] ?: "default"
            )
        }
        mmkv.encode(PreferenceKeys.FONT_RESOURCE, resource)
        AppearanceUtils.updateFont(resource)
    }
}