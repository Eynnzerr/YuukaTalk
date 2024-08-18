package com.eynnzerr.yuukatalk.utils

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.ui.common.TalkPieceProperty
import com.eynnzerr.yuukatalk.ui.component.ScreenInfo
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object DimensionUtils {
    private val mmkv = MMKV.defaultMMKV()
    private val _dimensionState = MutableStateFlow(
        TalkPieceProperty(
        avatarSize = mmkv.decodeFloat(PreferenceKeys.AVATAR_SIZE, 56f).dp,
        nameFontSize = mmkv.decodeFloat(PreferenceKeys.NAME_FONT_SIZE, 16f).sp,
        textStartPadding = mmkv.decodeFloat(PreferenceKeys.TEXT_START_PADDING, 64f).dp,
        textFontSize = mmkv.decodeFloat(PreferenceKeys.TEXT_FONT_SIZE, 16f).sp,
        verticalMargin = mmkv.decodeFloat(PreferenceKeys.VERTICAL_MARGIN, 8f).dp,
        photoWidth = mmkv.decodeFloat(PreferenceKeys.PHOTO_WIDTH, 192f).dp,
        narrationPadding = mmkv.decodeFloat(PreferenceKeys.NARRATION_PADDING, 16f).dp,
        narrationFontSize = mmkv.decodeFloat(PreferenceKeys.NARRATION_FONT_SIZE, 18f).sp,
        branchStartPadding = mmkv.decodeFloat(PreferenceKeys.BRANCH_START_PADDING, 72f).dp,
        loveSceneStartPadding = mmkv.decodeFloat(PreferenceKeys.LOVE_SCENE_START_PADDING, 72f).dp,
        )
    )
    val dimensionState = _dimensionState.asStateFlow()

    fun resetDimensions() {
        _dimensionState.update {
            TalkPieceProperty(
                avatarSize = 56.dp,
                nameFontSize = TextUnit.Unspecified,
                textStartPadding = 64.dp,
                textFontSize = 16.sp,
                verticalMargin = 8.dp,
                photoWidth = 192.dp,
                narrationPadding = 16.dp,
                narrationFontSize = 18.sp,
                branchStartPadding = 72.dp,
                loveSceneStartPadding = 72.dp,
            )
        }
        with(mmkv) {
            encode(PreferenceKeys.AVATAR_SIZE, 56)
            encode(PreferenceKeys.NAME_FONT_SIZE, TextUnit.Unspecified.value)
            encode(PreferenceKeys.TEXT_START_PADDING, 64)
            encode(PreferenceKeys.TEXT_FONT_SIZE, 16)
            encode(PreferenceKeys.VERTICAL_MARGIN, 8)
            encode(PreferenceKeys.PHOTO_WIDTH, 192)
            encode(PreferenceKeys.NARRATION_PADDING, 16)
            encode(PreferenceKeys.NARRATION_FONT_SIZE, 18)
            encode(PreferenceKeys.BRANCH_START_PADDING, 72)
            encode(PreferenceKeys.LOVE_SCENE_START_PADDING, 72)
        }
    }

    fun updateAvatarSize(size: Float) {
        _dimensionState.update { it.copy(avatarSize = size.dp) }
        mmkv.encode(PreferenceKeys.AVATAR_SIZE, size)
    }

    fun updateNameFontSize(size: Float) {
        _dimensionState.update { it.copy(nameFontSize = size.sp) }
        mmkv.encode(PreferenceKeys.NAME_FONT_SIZE, size)
    }

    fun updateTextStartPadding(padding: Float) {
        _dimensionState.update { it.copy(textStartPadding = padding.dp) }
        mmkv.encode(PreferenceKeys.TEXT_START_PADDING, padding)
    }

    fun updateTextFontSize(size: Float) {
        _dimensionState.update { it.copy(textFontSize = size.sp) }
        mmkv.encode(PreferenceKeys.TEXT_FONT_SIZE, size)
    }

    fun updateVerticalMargin(margin: Float) {
        _dimensionState.update { it.copy(verticalMargin = margin.dp) }
        mmkv.encode(PreferenceKeys.VERTICAL_MARGIN, margin)
    }

    fun updatePhotoWidth(width: Float) {
        _dimensionState.update { it.copy(photoWidth = width.dp) }
        mmkv.encode(PreferenceKeys.PHOTO_WIDTH, width)
    }
    fun updateNarrationPadding(padding: Float) {
        _dimensionState.update { it.copy(narrationPadding = padding.dp) }
        mmkv.encode(PreferenceKeys.NARRATION_PADDING, padding)
    }

    fun updateNarrationFontSize(size: Float) {
        _dimensionState.update { it.copy(narrationFontSize = size.sp) }
        mmkv.encode(PreferenceKeys.NARRATION_FONT_SIZE, size)
    }

    fun updateBranchStartPadding(padding: Float) {
        _dimensionState.update { it.copy(branchStartPadding = padding.dp) }
        mmkv.encode(PreferenceKeys.BRANCH_START_PADDING, padding)
    }

    fun updateLoveSceneStartPadding(padding: Float) {
        _dimensionState.update { it.copy(loveSceneStartPadding = padding.dp) }
        mmkv.encode(PreferenceKeys.LOVE_SCENE_START_PADDING, padding)
    }
}