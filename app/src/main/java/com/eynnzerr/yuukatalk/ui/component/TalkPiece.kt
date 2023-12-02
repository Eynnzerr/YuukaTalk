package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.ui.theme.BranchDefaultColors
import com.eynnzerr.yuukatalk.ui.theme.LoveSceneDefaultColors
import com.eynnzerr.yuukatalk.ui.theme.MomoTalkTextColor
import com.eynnzerr.yuukatalk.ui.theme.YuukaTalkTheme
import com.eynnzerr.yuukatalk.R

@Composable
fun TalkPiece(talkData: Talk) {
    when (talkData) {
        is Talk.PureText -> PureTextPiece(talk = talkData)
        is Talk.Photo -> PhotoPiece(talk = talkData)
        is Talk.Narration -> NarrationPiece(talk = talkData)
        is Talk.Branch -> BranchPiece(talk = talkData)
        is Talk.LoveScene -> LoveScenePiece(talk = talkData)
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
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MomoTalkTextColor
    )
}

@Composable
private fun BranchPiece(talk: Talk.Branch) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = BranchDefaultColors.containerBackgroundColor
        ),
        modifier = Modifier
            .padding(start = 72.dp, bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerticalDivider(
                    height = 20.dp,
                    thickness = 2.dp,
                    color = BranchDefaultColors.verticalDividerColor
                )
                Text(
                    text = "回复",
                    color = BranchDefaultColors.textColor,
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(
                thickness = 1.dp
            )
            talk.textOptions.forEach { text ->
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = BranchDefaultColors.optionBackgroundColor
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 6.dp
                    )
                ) {
                    Text(
                        text = text,
                        color = BranchDefaultColors.textColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxSize()
                    )
                }
            }
        }

    }
}

@Composable
private fun LoveScenePiece(talk: Talk.LoveScene) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = LoveSceneDefaultColors.containerBackgroundColor
        ),
        modifier = Modifier
            .padding(start = 72.dp, bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_love_scene),
                    contentDescription = "love background",
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(90.dp),
                        // .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerticalDivider(
                        height = 20.dp,
                        thickness = 2.dp,
                        color = LoveSceneDefaultColors.verticalDividerColor
                    )
                    Text(
                        text = "回复",
                        color = LoveSceneDefaultColors.headerColor,
                        modifier = Modifier.padding(start = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Divider(
                    thickness = 1.dp
                )
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = LoveSceneDefaultColors.buttonColor
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = "${talk.studentName}的羁绊剧情",
                        color = LoveSceneDefaultColors.textColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
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
        Talk.Branch(
            textOptions = listOf("option 1", "option 2")
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
        Talk.PureText(
            talker = Character.Sensei,
            isFirst = false,
            text = "Smoke that little weed in the couch in my bedroom."
        ),
        Talk.LoveScene(
            studentName = character.name
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