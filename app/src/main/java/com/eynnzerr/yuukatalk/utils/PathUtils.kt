package com.eynnzerr.yuukatalk.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import java.io.File

object PathUtils {
    fun getCommonEmojiPaths(context: Context): List<String> {
        val assetManager = context.assets
        return assetManager.list("common_emojis")
            ?.map { "file:///android_asset/common_emojis/$it" }
            ?: emptyList()
    }

    fun getRealPathForPrimary(uri: Uri): String {
        val path: String = uri.path.toString()
        return if (path.contains("primary:")) {
            "/storage/emulated/0/${path.split("primary:").last()}"
        } else {
            Toast.makeText(YuukaTalkApplication.context, "Sorry, this directory is not supported!", Toast.LENGTH_SHORT).show()
            getFileFallbackExportDir().absolutePath
        }
    }

    fun getFileFallbackExportDir() = File(
        YuukaTalkApplication.context.getExternalFilesDir(null),
        "json"
    ).also {
        if (!it.exists()) it.mkdir()
    }

    fun getImageFallbackExportDir() = File(
        YuukaTalkApplication.context.getExternalFilesDir(null),
        "images"
    ).also {
        if (!it.exists()) it.mkdirs()
    }
}