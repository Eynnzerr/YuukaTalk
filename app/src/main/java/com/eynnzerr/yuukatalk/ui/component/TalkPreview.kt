package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.Sensei
import com.eynnzerr.yuukatalk.data.model.Talk

@Composable
fun TalkPreview() {
    val character = Character(
        name = "优香",
        nameRoma = "Yuuka",
        school = "Millennium",
        avatarPath = "millennium/yuuka/avatar",
        emojiPath = "millennium/yuuka/emoji",
    )

    val talks = listOf(
        Talk.Narration(
            text = "夏莱的平常一天。"
        ),
        Talk.PureText(
            talker = character,
            isFirst = true,
            text = "老师，能占用你一点时间吗？"
        ),
        Talk.PureText(
            talker = character,
            isFirst = false,
            text = "老师，你又乱花钱了！"
        ),
        Talk.Photo(
            talker = character,
            isFirst = false,
            uri = "file:///android_asset/millennium/yuuka/emoji/Millennium_TheSeminar_Yuuka.4.webp"
        ),
        Talk.Branch(
            textOptions = listOf("啊哈哈...", "才不是乱花钱！")
        ),
        Talk.PureText(
            talker = Sensei,
            isFirst = false,
            text = "计划很成功...没有被优香发现呢。"
        ),
        Talk.PureText(
            talker = Sensei,
            isFirst = false,
            text = "优香...生日快乐。"
        ),
        Talk.LoveScene(
            studentName = character.name
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        talks.forEach {
            TalkPiece(talkData = it)
        }
    }
}