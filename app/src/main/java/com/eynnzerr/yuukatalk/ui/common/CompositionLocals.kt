package com.eynnzerr.yuukatalk.ui.common

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TalkPieceProperty(
    val avatarSize: Dp, // 头像长宽
    val nameFontSize: TextUnit, //名字字体大小
    val textStartPadding: Dp, // 文字消息到左侧屏幕的最小间距
    val textFontSize: TextUnit, // 文字消息内容字体大小
    val verticalMargin: Dp, // 消息上下间距
    val photoWidth: Dp, // 图片宽度
    val narrationPadding: Dp, // 旁白左右间距
    val narrationFontSize: TextUnit, // 旁白字体大小
    val branchStartPadding: Dp, // 分支到左侧屏幕的间距
    val loveSceneStartPadding: Dp, // 羁绊剧情到左侧屏幕的间距
)

val LocalTalkPieceProperty = compositionLocalOf {
    TalkPieceProperty(
        avatarSize = 56.dp,
        nameFontSize = TextUnit.Unspecified,
        textStartPadding = 64.dp,
        textFontSize = 16.sp,
        verticalMargin = 8.dp,
        photoWidth = 192.dp,
        narrationPadding = 16.dp,
        narrationFontSize = 18.sp,
        branchStartPadding = 72.dp, // or 288.dp in large screen
        loveSceneStartPadding = 72.dp,
    )
}