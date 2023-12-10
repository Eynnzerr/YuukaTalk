package com.eynnzerr.yuukatalk.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Talk {

    @Serializable
    data class PureText(
        val talker: Character,
        var text: String,
        var isFirst: Boolean,
    ): Talk()

    @Serializable
    data class Photo(
        val talker: Character,
        val uri: String,
        var isFirst: Boolean,
    ): Talk()

    @Serializable
    data class Narration(
        val text: String
    ): Talk()

    @Serializable
    data class Branch(
        val textOptions: List<String>
    ): Talk()

    @Serializable
    data class LoveScene(
        val studentName: String
    ): Talk()
}