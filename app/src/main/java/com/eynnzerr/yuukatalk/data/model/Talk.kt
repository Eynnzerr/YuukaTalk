package com.eynnzerr.yuukatalk.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Talk {

    @Serializable
    class PureText(
        val talker: Character,
        var text: String,
        var isFirst: Boolean,
    ): Talk()

    @Serializable
    class Photo(
        val talker: Character,
        val uri: String,
        var isFirst: Boolean,
    ): Talk()

    @Serializable
    class Narration(
        val text: String
    ): Talk()

    @Serializable
    class Branch(
        val textOptions: List<String>
    ): Talk()

    @Serializable
    class LoveScene(
        val studentName: String
    ): Talk()
}