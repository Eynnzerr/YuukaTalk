package com.eynnzerr.yuukatalk.data.model

import kotlinx.serialization.Serializable

@Serializable
class CharacterAsset(
    val version: Int,
    val characters: Array<Character>
)