package com.eynnzerr.yuukatalk.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

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
    val avatarPath: String,
    @ColumnInfo(defaultValue = "")
    val emojiPath: String,
    @ColumnInfo(defaultValue = "")
    var currentAvatar: String = "file:///android_asset/$avatarPath/avatar_0.webp"
) {

    fun getEmojiPaths(context: Context): List<String> {
        val assetManager = context.assets
        return assetManager.list(emojiPath)
            ?.map { "file:///android_asset/$emojiPath/$it" }
            ?: emptyList()
    }

    fun getAvatarPaths(context: Context): List<String> {
        val assetManager = context.assets
        return assetManager.list(avatarPath)
            ?.map { "file:///android_asset/$avatarPath/$it" }
            ?: emptyList()
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
    school = "schale",
    avatarPath = "schale/sensei/avatar",
    emojiPath = "schale/sensei/emoji"
)