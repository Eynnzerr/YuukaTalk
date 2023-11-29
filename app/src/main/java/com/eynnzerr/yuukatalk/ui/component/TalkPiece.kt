package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.ui.theme.YuukaTalkTheme

@Composable
fun TalkPiece(talkData: Talk) {
    when (talkData) {
        is Talk.PureText -> PureTextPiece(talk = talkData)
        is Talk.Photo -> PhotoPiece(talk = talkData)
        is Talk.Narration -> NarrationPiece(talk = talkData)
    }
}

@Composable
private fun PureTextPiece(talk: Talk.PureText) {
    if (talk.isFirst) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudentAvatar(
                url = talk.talker.avatarPath,
                size = 64.dp
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = talk.talker.name,
                    fontSize = 18.sp
                )
                ChatBubble(
                    text = talk.text,
                    isMyMessage = false,
                    showArrow = true
                )
            }
        }
    } else {
        ChatBubble(text = talk.text, isMyMessage = false, showArrow = false)
    }
}

@Composable
private fun PhotoPiece(talk: Talk.Photo) {

}

@Composable
private fun NarrationPiece(talk: Talk.Narration) {

}

@Preview(
    name = "first pureText",
    showBackground = true
)
@Composable
fun FirstPureTextPiecePreview() {
    YuukaTalkTheme {
        val character = Character(
            name = "Shiroko",
            school = "Abydios",
            avatarPath = "file:///android_asset/shiroko/emoji_0.png",
        )
        val talk = Talk.PureText(
            talker = character,
            isFirst = true,
            text = "Hello, sensei."
        )
        PureTextPiece(talk = talk)
    }
}

@Preview(
    name = "following pureText",
    showBackground = true
)
@Composable
fun FollowingPureTextPiecePreview() {
    YuukaTalkTheme {
        val character = Character(
            name = "Shiroko",
            school = "Abydios",
            avatarPath = "file:///android_asset/shiroko/emoji_0.png",
        )
        val talk = Talk.PureText(
            talker = character,
            isFirst = false,
            text = "Don't let me go."
        )
        PureTextPiece(talk = talk)
    }
}