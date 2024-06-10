package com.eynnzerr.yuukatalk.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoeTalkCharacter(
    @SerialName("no")
    val nameRoma: String,
    val index: String // 头像索引，由于命名方式不同，这里不使用
)

@Serializable
data class MoeTalkMessage(
    val content: String,
    val replyDepth: Int,
    val type: String,
    val isFirst: Boolean,
    val isRight: Boolean,
    val isBreaking: Boolean,
    val sCharacter: Character,
    val time: String,
    val name: String?,
    val file: String?
)

@Serializable
data class MoeTalkBackup(
    val title: String,
    val nickname: String,
    val date: String,
    @SerialName("选择角色")
    val talker: Selection,
)

@Serializable
data class Selection(
    val no: String,
    val index: String,
    val list: List<Character>
)

