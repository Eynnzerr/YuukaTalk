package com.eynnzerr.yuukatalk.ui.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentInfo(
    student: Character,
    modifier: Modifier = Modifier,
    onPickAvatar: () -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    var expand by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Column {
        Row(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
                .combinedClickable(
                    enabled = true,
                    onClick = { expand = !expand },
                    onLongClick = onLongPress
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(student.getAvatarPaths(context).getOrNull(selectedIndex))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(64.dp, 64.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = student.school,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            SchoolLogo(school = student.school)
        }

        AnimatedVisibility(
            visible = expand,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            LazyRow (
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = student.getAvatarPaths(context),
                    key = { index, _ -> index }
                ) { index, path ->
                    StudentAvatar(
                        url = path,
                        withBorder = index == selectedIndex,
                        isSelected = index == selectedIndex,
                        size = 48.dp,
                        onClick = {
                            student.currentAvatar = path
                            selectedIndex = index
                            onPickAvatar()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SchoolLogo(
    school: String,
    size: Dp = 56.dp
) {
    val painter = when (school) {
        School.ABYDOS -> painterResource(id = R.drawable.school_logo_abydos)
        School.GEHENNA -> painterResource(id = R.drawable.school_logo_gehenna)
        School.HYAKKIYAKO -> painterResource(id = R.drawable.school_logo_hyakkiyako)
        School.MILLENNIUM -> painterResource(id = R.drawable.school_logo_millennium)
        School.RED_WINTER -> painterResource(id = R.drawable.school_logo_redwinter)
        School.SHAN_HAI_JING -> painterResource(id = R.drawable.school_logo_shanhaijing)
        School.SRT -> painterResource(id = R.drawable.school_logo_srt)
        School.TRINITY -> painterResource(id = R.drawable.school_logo_trinity)
        School.VALKYRIE -> painterResource(id = R.drawable.school_logo_valkyrie)
        School.ARIUS -> painterResource(id = R.drawable.student_logo_arius)
        School.KRONOS -> painterResource(id = R.drawable.school_logo_kronos)
        else -> painterResource(id = R.drawable.school_logo_federal) // TODO 搞个别的placeholder
    }

    Image(
        painter = painter,
        contentDescription = "school logo",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(size)
    )
}

@Preview(
    name = "StudentInfo",
    showBackground = true
)
@Composable
fun StudentInfoPreview() {
    val student = Character(
        name = "妃咲",
        nameRoma = "Kisaaki",
        school = "Shanhaijing",
        avatarPath = "Shanhaijing/Genryumon/Kissaki/avatar",
        emojiPath = "Shanhaijing/Genryumon/Kissaki/emoji",
    )

    StudentInfo(student = student) {
        Log.d(TAG, "StudentInfoPreview: student avatar path: ${student.currentAvatar}")
    }
}

object School {
    const val ABYDOS = "Abydos"
    const val FEDERAL = "Federal"
    const val GEHENNA = "Gehenna"
    const val HYAKKIYAKO = "Hyakkiyako"
    const val MILLENNIUM = "Millennium"
    const val RED_WINTER = "Red Winter"
    const val SHAN_HAI_JING = "Shanhaijing"
    const val SRT = "SRT"
    const val TRINITY = "Trinity"
    const val VALKYRIE = "Valkyrie"
    const val ARIUS = "Arius"
    const val KRONOS = "Kronos"

    val schoolLists = listOf(
        ABYDOS,
        GEHENNA,
        MILLENNIUM,
        TRINITY,
        HYAKKIYAKO,
        RED_WINTER,
        SHAN_HAI_JING,
        SRT,
        VALKYRIE,
        ARIUS,
        KRONOS,
        FEDERAL
    )

    val schoolInChinese = mapOf(
        ABYDOS to "阿拜多斯",
        GEHENNA to "格黑娜",
        MILLENNIUM to "千年",
        TRINITY to "圣三一",
        HYAKKIYAKO to "百鬼夜行",
        RED_WINTER to "红冬",
        SHAN_HAI_JING to "山海经",
        SRT to SRT,
        VALKYRIE to "瓦尔基里",
        ARIUS to "阿里乌斯",
        KRONOS to "克罗诺斯",
        FEDERAL to "联邦学生会"
    )
}

private const val TAG = "StudentInfo"