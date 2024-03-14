package com.eynnzerr.yuukatalk.ui.page.settings.editor_options

import androidx.lifecycle.ViewModel
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.utils.ImageUtils
import com.eynnzerr.yuukatalk.utils.PathUtils
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
    val imageExportPath: String,
    val fileExportPath: String,
    val enableAutoSave: Boolean,
    val enableGesture: Boolean,
    val enableMarkdown: Boolean,
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
        imageQuality = mmkv.decodeInt(PreferenceKeys.IMAGE_QUALITY, 100),
        imageExportPath = mmkv.decodeString(PreferenceKeys.IMAGE_EXPORT_PATH) ?: ImageUtils.defaultExportPath,
        fileExportPath = mmkv.decodeString(PreferenceKeys.FILE_EXPORT_PATH) ?: PathUtils.getFileFallbackExportDir().absolutePath,
        enableAutoSave = mmkv.decodeBool(PreferenceKeys.USE_AUTO_SAVE, false),
        enableGesture = mmkv.decodeBool(PreferenceKeys.USE_SWIPE_GESTURE, true),
        enableMarkdown = mmkv.decodeBool(PreferenceKeys.USE_MARKDOWN, false),
    ))
    private val stateValue
        get() = _uiState.value

    val uiState = _uiState.asStateFlow()

    fun switchWatermark() {
        _uiState.update { it.copy(enableWatermark = !stateValue.enableWatermark) }
        mmkv.encode(PreferenceKeys.USE_WATERMARK, stateValue.enableWatermark)
    }

    fun switchAutoSave() {
        _uiState.update { it.copy(enableAutoSave = !stateValue.enableAutoSave) }
        mmkv.encode(PreferenceKeys.USE_AUTO_SAVE, stateValue.enableAutoSave)
    }

    fun switchSwipeGesture() {
        _uiState.update { it.copy(enableGesture = !stateValue.enableGesture) }
        mmkv.encode(PreferenceKeys.USE_SWIPE_GESTURE, stateValue.enableGesture)
    }

    fun switchUseMarkdown() {
        _uiState.update { it.copy(enableMarkdown = !stateValue.enableMarkdown) }
        mmkv.encode(PreferenceKeys.USE_MARKDOWN, stateValue.enableMarkdown)
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

    fun updateImageExportPath(path: String) {
        _uiState.update { it.copy(imageExportPath = path) }
        mmkv.encode(PreferenceKeys.IMAGE_EXPORT_PATH, path)
    }

    fun updateFileExportPath(path: String) {
        _uiState.update { it.copy(fileExportPath = path) }
        mmkv.encode(PreferenceKeys.FILE_EXPORT_PATH, path)
    }
}

private const val TAG = "EditorOptionsViewModel"