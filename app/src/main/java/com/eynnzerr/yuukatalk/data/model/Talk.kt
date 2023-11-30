package com.eynnzerr.yuukatalk.data.model

sealed class Talk {
    class PureText(
        val talker: Character,
        var text: String,
        var isFirst: Boolean,
    ): Talk()

    class Photo(
        val talker: Character,
        val uri: String,
        var isFirst: Boolean,
    ): Talk()

    class Narration(
        val text: String
    ): Talk()
}