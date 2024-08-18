package com.eynnzerr.yuukatalk.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.FolderWithProjects
import com.eynnzerr.yuukatalk.data.model.TalkFolder
import com.eynnzerr.yuukatalk.data.model.TalkProject

enum class FolderDisplay {
    HORIZONTAL,
    VERTICAL,
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TalkFolderCard(
    modifier: Modifier = Modifier,
    talkFolder: FolderWithProjects,
    displayMode: FolderDisplay = FolderDisplay.VERTICAL,
    onLongCLick: () -> Unit,
    onClickItem: (TalkProject) -> Unit,
    onLongClickItem: (TalkProject) -> Unit,
    onDismissItem: (TalkProject) -> Unit,
) {
    val (folder, projects) = talkFolder
    val (expanded, setExpanded) = remember { mutableStateOf(true) }

    Column {
        Row(
            modifier = modifier
                .padding(16.dp)
                .combinedClickable(
                    enabled = true,
                    onClick = { setExpanded(!expanded) },
                    onLongClick = onLongCLick
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.FolderOpen else Icons.Filled.Folder,
                modifier = Modifier.size(50.dp),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp
                )
                Text(
                    text = stringResource(R.string.last_created) + folder.createdDate,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                contentDescription = ""
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            if (displayMode == FolderDisplay.VERTICAL) {
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    projects.forEach { item ->
                        SwipeToDismissContainer(
                            onConfirmDismiss = {
                                onDismissItem(item)
                            }
                        ) {
                            TalkHistoryCard(
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                                history = item,
                                onClick = { onClickItem(item) },
                                onLongCLick = { onLongClickItem(item) }
                            )
                        }
                    }
                }
            } else if (displayMode == FolderDisplay.HORIZONTAL) {

            }
        }
    }
}

@Preview(
    name = "TalkFolderCard",
    showBackground = true
)
@Composable
private fun TalkFolderCardPreview() {
    val talkFolder = FolderWithProjects(
        folder = TalkFolder(),
        projects = mutableListOf<TalkProject>().apply { repeat(10) { add(TalkProject()) } }
    )
    TalkFolderCard(
        talkFolder = talkFolder,
        onLongCLick = {},
        onClickItem = {},
        onLongClickItem = {},
        onDismissItem = {},
    )
}