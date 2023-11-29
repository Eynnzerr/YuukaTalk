package com.eynnzerr.yuukatalk.data.model

sealed class Talk {
    class PureText(
        val talker: Character,
        var text: String,
        var isFirst: Boolean,
    ): Talk()

    class Photo(
        val uri: String
    ): Talk()

    class Narration(
        val text: String
    ): Talk()
}