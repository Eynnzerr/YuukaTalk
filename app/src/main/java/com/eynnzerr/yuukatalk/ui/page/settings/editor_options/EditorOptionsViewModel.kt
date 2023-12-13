package com.eynnzerr.yuukatalk.ui.page.settings.editor_options

import android.util.Log
import androidx.lifecycle.ViewModel
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class EditorOptionsState(
    val enableWatermark: Boolean,
    val authorName: String,
    val watermarkPosition: Int,
    val screenshotBackground: String,
    val compressFormatIndex: Int,
    val imageQuality: Int,
)

@HiltViewModel
class EditorOptionsViewModel @Inject constructor(
    private val mmkv: MMKV
): ViewModel() {
    private val _uiState = MutableStateFlow(EditorOptionsState(
        enableWatermark = mmkv.decodeBool(PreferenceKeys.USE_WATERMARK, false),
        authorName = mmkv.decodeString(PreferenceKeys.AUTHOR_NAME) ?: "",
        watermarkPosition = mmkv.decodeInt(PreferenceKeys.WATERMARK_POSITION, 0),
        screenshotBackground = mmkv.decodeString(PreferenceKeys.BACKGROUND_COLOR) ?: "#fff7e3",
        compressFormatIndex = mmkv.decodeInt(PreferenceKeys.COMPRESS_FORMAT, 1),
        imageQuality = mmkv.decodeInt(PreferenceKeys.IMAGE_QUALITY, 100)
    ))
    private val stateValue
        get() = _uiState.value

    val uiState = _uiState.asStateFlow()

    fun switchWatermark() {
        _uiState.update { it.copy(enableWatermark = !stateValue.enableWatermark) }
        mmkv.encode(PreferenceKeys.USE_WATERMARK, stateValue.enableWatermark)
    }

    fun updateAuthorName(name: String) {
        _uiState.update { it.copy(authorName = name) }
    }

    fun saveAuthorName() = mmkv.encode(PreferenceKeys.AUTHOR_NAME, stateValue.authorName)

    fun updateBackgroundColor(color: String) {
        _uiState.update { it.copy(screenshotBackground = color) }
        mmkv.encode(PreferenceKeys.BACKGROUND_COLOR, color)
    }

    fun updateImageQuality(quality: Int) {
        _uiState.update { it.copy(imageQuality = quality) }
    }

    fun encodeImageQuality() = mmkv.encode(PreferenceKeys.IMAGE_QUALITY, _uiState.value.imageQuality)

    fun updateCompressFormat(formatIndex: Int) {
        _uiState.update { it.copy(compressFormatIndex = formatIndex) }
        mmkv.encode(PreferenceKeys.COMPRESS_FORMAT, formatIndex)
    }
}

private const val TAG = "EditorOptionsViewModel"