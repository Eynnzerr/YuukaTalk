package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
    if (talk.talker is Character.Sensei) {
        ChatBubble(
            text = talk.text, 
            isMyMessage = true, 
            showArrow = true,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    } else if (talk.isFirst) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
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
        ChatBubble(
            text = talk.text,
            isMyMessage = false,
            showArrow = false,
            modifier = Modifier.padding(start = 72.dp, bottom = 8.dp)
        )
    }
}

@Composable
private fun PhotoPiece(talk: Talk.Photo) {
    if (talk.talker is Character.Sensei) {
        PhotoBubble(
            uri = talk.uri,
            isMyMessage = true,
        )
    } else if (talk.isFirst) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp)
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
                PhotoBubble(
                    uri = talk.uri,
                    isMyMessage = false,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    } else {
        PhotoBubble(
            uri = talk.uri,
            isMyMessage = false,
            modifier = Modifier.padding(start = 80.dp)
        )
    }
}

@Composable
private fun NarrationPiece(talk: Talk.Narration) {
    Text(
        text = talk.text,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 16.dp),
        fontSize = 18.sp
    )
}

@Preview(
    name = "hybrid talks",
    showBackground = true
)
@Composable
private fun HybridTextPiecePreview() {

    val character = Character(
        name = "Shiroko",
        school = "Abydios",
        avatarPath = "file:///android_asset/shiroko/emoji_0.png",
    )
    
    val talks = listOf(
        Talk.PureText(
            talker = character,
            isFirst = true,
            text = "Say you never let me go."
        ),
        Talk.PureText(
            talker = character,
            isFirst = false,
            text = "Deep in the bones I can feel you."
        ),
        Talk.Photo(
            talker = character,
            isFirst = false,
            uri = "file:///android_asset/shiroko/emoji_3.png"
        ),
        Talk.Narration(
            text = "Take me back to the time."
        ),
        Talk.PureText(
            talker = Character.Sensei,
            isFirst = false,
            text = "We can waste a night with an old film."
        ),
        Talk.Photo(
            talker = Character.Sensei,
            isFirst = false,
            uri = "file:///android_asset/shiroko/emoji_3.png"
        ),
        Talk.PureText(
            talker = Character.Sensei,
            isFirst = false,
            text = "Smoke that little weed in the couch in my bedroom."
        ),
        Talk.Photo(
            talker = character,
            isFirst = true,
            uri = "file:///android_asset/shiroko/emoji_3.png"
        ),
    )
    
    YuukaTalkTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(talks) {
                TalkPiece(talkData = it)
            }
        }
    }
}