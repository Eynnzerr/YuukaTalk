package com.eynnzerr.yuukatalk.ui.component

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.eynnzerr.yuukatalk.data.model.Sensei
import com.eynnzerr.yuukatalk.ui.common.LocalTalkPieceProperty
import kotlinx.coroutines.delay

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
fun AnimatedTalkPiece(
    talkData: Talk,
    isAnimating: Boolean,
    interval: Long = 2000,
    onClickBranches: List<() -> Unit>? = null,
    onAnimationEnd: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val dotCount by infiniteTransition.animateValue(
        initialValue = 1,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    var showMessage by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(interval)
            showMessage = true
            onAnimationEnd()
        }
    }

    if (!showMessage) {
        when (talkData) {
            is Talk.PureText -> PureTextPiece(talk = Talk.PureText(
                talker = talkData.talker,
                isFirst = talkData.isFirst,
                text = ".".repeat(dotCount)
            ))
            is Talk.Photo -> PureTextPiece(talk = Talk.PureText(
                talker = talkData.talker,
                isFirst = talkData.isFirst,
                text = ".".repeat(dotCount)
            ))
            else -> TalkPiece(talkData)
        }
    } else {
        when (talkData) {
            is Talk.Branch -> BranchPiece(
                talk = talkData,
                onClickBranches = onClickBranches
            )
            else -> TalkPiece(talkData)
        }
    }
}

@Composable
private fun PureTextPiece(talk: Talk.PureText) {
    val talkPieceProperty = LocalTalkPieceProperty.current
    if (talk.talker.nameRoma == Sensei.nameRoma) {
        ChatBubble(
            text = talk.text, 
            isMyMessage = true, 
            showArrow = true,
            modifier = Modifier.padding(
                bottom = talkPieceProperty.verticalMargin,
                start = talkPieceProperty.textStartPadding
            )
        )
    } else if (talk.isFirst) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(vertical = talkPieceProperty.verticalMargin)
        ) {
            StudentAvatar(
                url = talk.talker.currentAvatar,
                isSelected = true,
                size = talkPieceProperty.avatarSize
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    text = talk.talker.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = talkPieceProperty.nameFontSize
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
            modifier = Modifier.padding(
                bottom = talkPieceProperty.verticalMargin,
                start = talkPieceProperty.textStartPadding
            )
        )
    }
}

@Composable
private fun PhotoPiece(talk: Talk.Photo) {
    val photoPieceProperty = LocalTalkPieceProperty.current

    if (talk.talker.nameRoma == Sensei.nameRoma) {
        PhotoBubble(
            uri = talk.uri,
            isMyMessage = true,
        )
    } else if (talk.isFirst) {
        Row(
            modifier = Modifier.padding(vertical = photoPieceProperty.verticalMargin)
        ) {
            StudentAvatar(
                url = talk.talker.currentAvatar,
                isSelected = true,
                size = photoPieceProperty.avatarSize
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    text = talk.talker.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = photoPieceProperty.nameFontSize
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
            modifier = Modifier.padding(start = photoPieceProperty.avatarSize + 16.dp) // 56 + 8 + 8
        )
    }
}

@Composable
private fun NarrationPiece(talk: Talk.Narration) {
    val narrationPieceProperty = LocalTalkPieceProperty.current
    Text(
        text = talk.text,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = narrationPieceProperty.narrationPadding,
                end = narrationPieceProperty.narrationPadding,
                bottom = narrationPieceProperty.narrationPadding + narrationPieceProperty.verticalMargin,
                top = narrationPieceProperty.narrationPadding
            ),
        fontSize = narrationPieceProperty.narrationFontSize,
        fontWeight = FontWeight.Bold,
        color = MomoTalkTextColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchPiece(
    talk: Talk.Branch,
    onClickBranches: List<() -> Unit>? = null
) {
    val branchPieceProperty = LocalTalkPieceProperty.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = BranchDefaultColors.containerBackgroundColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = branchPieceProperty.branchStartPadding,
                bottom = branchPieceProperty.verticalMargin
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_branch),
                    contentDescription = "branch background",
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(90.dp),
                    // .aspectRatio(1f),
                    contentScale = ContentScale.Fit
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
                talk.textOptions.forEachIndexed { index, text ->
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = BranchDefaultColors.optionBackgroundColor
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 6.dp
                        ),
                        onClick = { onClickBranches?.get(index)?.invoke() }
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
}

@Composable
private fun LoveScenePiece(talk: Talk.LoveScene) {
    val loveSceneProperty = LocalTalkPieceProperty.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = LoveSceneDefaultColors.containerBackgroundColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = loveSceneProperty.branchStartPadding,
                bottom = loveSceneProperty.verticalMargin
            )
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
        name = "白子",
        nameRoma = "Shiroko",
        school = "Abydos",
        avatarPath = "abydos/shiroko/avatar",
        emojiPath = "abydos/shiroko/emoji",
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
            uri = "file:///android_asset/abydos/shiroko/emoji/Abydos_Countermeasure_Shiroko.5.webp"
        ),
        Talk.Narration(
            text = "Take me back to the time."
        ),
        Talk.PureText(
            talker = Sensei,
            isFirst = false,
            text = "We can waste a night with an old film."
        ),
        Talk.PureText(
            talker = Sensei,
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

@Preview(
    name = "hybrid dynamic talks",
    showBackground = true
)
@Composable
private fun DynamicTextPiecePreview() {
    val character = Character(
        name = "白子",
        nameRoma = "Shiroko",
        school = "Abydos",
        avatarPath = "abydos/shiroko/avatar",
        emojiPath = "abydos/shiroko/emoji",
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
            uri = "file:///android_asset/abydos/shiroko/emoji/Abydos_Countermeasure_Shiroko.5.webp"
        ),
        Talk.Narration(
            text = "Take me back to the time."
        ),
        Talk.PureText(
            talker = Sensei,
            isFirst = false,
            text = "We can waste a night with an old film."
        ),
        Talk.PureText(
            talker = Sensei,
            isFirst = false,
            text = "Smoke that little weed in the couch in my bedroom."
        ),
        Talk.LoveScene(
            studentName = character.name
        ),
    )

    YuukaTalkTheme {
        var currentMessageIndex by remember { mutableIntStateOf(0) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(talks) { index, talk ->
                if (index <= currentMessageIndex) {
                    AnimatedTalkPiece(
                        talkData = talk,
                        isAnimating = index == currentMessageIndex,
                        onAnimationEnd = {
                            if (index == currentMessageIndex) {
                                currentMessageIndex ++
                            }
                        }
                    )
                }
            }
        }
    }
}

private const val TAG = "TalkPiece"