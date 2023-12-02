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

    class Branch(
        val textOptions: List<String>
    ): Talk()

    class LoveScene(
        val studentName: String
    ): Talk()
}