package com.eynnzerr.yuukatalk.data.model

open class Character(
    val name: String,
    val school: String,
    val avatarPath: String,
) {
    companion object Sensei: Character(
        name = "sensei",
        school = "schale",
        avatarPath = "file:///android_asset/sensei/avatar.webp"
    )

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