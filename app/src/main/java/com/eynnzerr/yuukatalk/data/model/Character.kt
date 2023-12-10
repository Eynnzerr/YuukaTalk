package com.eynnzerr.yuukatalk.data.model

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
@Entity(tableName = "momotalk_character")
open class Character(
    @PrimaryKey
    val name: String,
    @ColumnInfo(defaultValue = "")
    val nameRoma: String,
    @ColumnInfo(defaultValue = "")
    val school: String,
    @ColumnInfo(defaultValue = "")
    val isAsset: Boolean = true,
    @ColumnInfo(defaultValue = "")
    val avatarPath: String,
    @ColumnInfo(defaultValue = "")
    val emojiPath: String,
    @ColumnInfo(defaultValue = "")
    var currentAvatar: String = "file:///android_asset/$avatarPath/avatar_0.webp"
) {

    open fun getEmojiPaths(context: Context): List<String> {
        return if (isAsset) {
            val assetManager = context.assets
            assetManager.list(emojiPath)
                ?.map { "file:///android_asset/$emojiPath/$it" }
                ?: emptyList()
        } else {
            val emojiRoot = File(emojiPath)
            if (!emojiRoot.exists()) emojiRoot.mkdir()
            emojiRoot.list()?.toList() ?: emptyList()
        }
    }

    open fun getAvatarPaths(context: Context): List<String> {
        return if (isAsset) {
            val assetManager = context.assets
            assetManager.list(avatarPath)
                ?.map { "file:///android_asset/$avatarPath/$it" }
                ?: emptyList()
        } else {
            val avatarRoot = File(avatarPath)
            if (!avatarRoot.exists()) avatarRoot.mkdirs()
            avatarRoot.list()
                ?.map { "$avatarPath/$it" }
                ?: emptyList()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Character

        if (name != other.name) return false
        if (school != other.school) return false
        if (avatarPath != other.avatarPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + school.hashCode()
        result = 31 * result + avatarPath.hashCode()
        return result
    }
}

object Sensei: Character(
    name = "老师",
    nameRoma = "sensei",
    school = "Schale",
    avatarPath = "schale/sensei/avatar",
    emojiPath = "schale/sensei/emoji"
)

private const val TAG = "Character"