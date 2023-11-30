package com.eynnzerr.yuukatalk.data.model

open class Character(
    val name: String,
    val school: String,
    val avatarPath: String,
) {
    companion object Sensei: Character(
        name = "sensei",
        school = "schale",
        avatarPath = ""
    )
}