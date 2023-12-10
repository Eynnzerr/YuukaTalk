package com.eynnzerr.yuukatalk.utils

import android.content.Context

object PathUtils {
    fun getCommonEmojiPaths(context: Context): List<String> {
        val assetManager = context.assets
        return assetManager.list("common_emojis")
            ?.map { "file:///android_asset/common_emojis/$it" }
            ?: emptyList()
    }
}