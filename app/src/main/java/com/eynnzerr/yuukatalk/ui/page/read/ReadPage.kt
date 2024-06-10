package com.eynnzerr.yuukatalk.ui.page.read

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.Sensei
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.ui.component.AnimatedTalkPiece
import com.eynnzerr.yuukatalk.ui.component.TalkPiece
import com.eynnzerr.yuukatalk.utils.SplitRelay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadPage(
    navHostController: NavHostController
) {
    val listState = rememberLazyListState()
    var talkList by remember { mutableStateOf(SplitRelay.talkList) }
    var isScrollingUp by remember { mutableStateOf(true) }
    var currentMessageIndex by remember { mutableIntStateOf(0) }
    var autoPlay by remember { mutableStateOf(false) }
    var openPlayDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // dialog state
    var manualBranch by remember { mutableStateOf(false) }
    var interval by remember { mutableLongStateOf(2000) }

    LaunchedEffect(listState) {
        var previousIndex = 0
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { offset ->
                isScrollingUp = offset <= previousIndex
                previousIndex = offset
            }
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = !autoPlay,
                enter = slideInVertically(),
                exit = slideOutVertically(),
            ) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navHostController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                openPlayDialog = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PlayCircleOutline,
                                contentDescription = "auto play mode"
                            )
                        }
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentMessageIndex > SplitRelay.talkList.lastIndex,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    onClick = { currentMessageIndex = 0 }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Replay,
                        contentDescription = null
                    )
                }
            }
        }
    ) { scaffoldPadding ->
        if (autoPlay) {
            LaunchedEffect(currentMessageIndex) {
                scope.launch {
                    if (currentMessageIndex > 0) {
                        listState.scrollToItem(currentMessageIndex)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.padding(scaffoldPadding + PaddingValues(16.dp)),
                state = listState,
                userScrollEnabled = false
            ) {
                itemsIndexed(talkList) { index, talk ->
                    if (index <= currentMessageIndex) {
                        AnimatedTalkPiece(
                            talkData = talk,
                            isAnimating = index == currentMessageIndex,
                            interval = interval,
                            onClickBranches = if (talk is Talk.Branch) {
                                talk.textOptions.map {{
                                    Log.d(TAG, "ReadPage: click branch option.")
                                    talkList = talkList.mapIndexed { i, talk ->
                                        if (index == i) {
                                            Talk.PureText(
                                                talker = Sensei,
                                                text = it,
                                                isFirst = true
                                            )
                                        } else talk
                                    }
                                    currentMessageIndex ++
                                }}
                            } else null,
                            onAnimationEnd = {
                                if (index == currentMessageIndex) {
                                    if (talk !is Talk.Branch || !manualBranch) {
                                        currentMessageIndex ++
                                        Log.d(TAG, "ReadPage: current index: $index. Talk: $talk")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(scaffoldPadding + PaddingValues(16.dp)),
                state = listState
            ) {
                items(SplitRelay.talkList) {
                    TalkPiece(talkData = it)
                }
            }
        }
    }

    if (openPlayDialog) {
        var startIndexString by remember { mutableStateOf("0") }
        var endIndexString by remember { mutableStateOf(SplitRelay.talkList.lastIndex.toString()) }
        var intervalString by remember { mutableStateOf("2000") }

        AlertDialog(
            onDismissRequest = {
                openPlayDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        talkList = SplitRelay.talkList.subList(startIndexString.toInt(), endIndexString.toInt() + 1)
                        interval = intervalString.toLong()

                        openPlayDialog = false
                        autoPlay = true
                    },
                    enabled = startIndexString.toIntOrNull() != null &&
                            endIndexString.toIntOrNull() != null &&
                            intervalString.toIntOrNull() != null
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openPlayDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.PlayCircleFilled,
                    contentDescription = "play dialog icon"
                )
            },
            title = {
                Text(text = stringResource(id = R.string.title_play_dialog))
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    ) {
                        OutlinedTextField(
                            value = startIndexString,
                            onValueChange = { startIndexString = it },
                            label = { Text(text = stringResource(R.string.start)) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = startIndexString.toIntOrNull() == null
                        )
                        OutlinedTextField(
                            value = endIndexString,
                            onValueChange = { endIndexString = it},
                            label = { Text(text = stringResource(R.string.end)) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = endIndexString.toIntOrNull() == null
                        )
                    }
                    OutlinedTextField(
                        value = intervalString,
                        onValueChange = { intervalString = it },
                        label = { Text(text = stringResource(R.string.play_interval)) },
                        modifier = Modifier.padding(bottom = 16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = intervalString.toIntOrNull() == null
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = manualBranch,
                                onValueChange = { manualBranch = !manualBranch },
                                role = Role.Checkbox
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.manual_click),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Checkbox(
                            checked = manualBranch,
                            onCheckedChange = null
                        )
                    }
                }
            }
        )
    }
}

private operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    return PaddingValues(
        start = other.calculateStartPadding(LayoutDirection.Ltr) + calculateStartPadding(LayoutDirection.Ltr),
        end = other.calculateEndPadding(LayoutDirection.Ltr) + calculateEndPadding(LayoutDirection.Ltr),
        top = other.calculateTopPadding() + calculateTopPadding(),
        bottom = other.calculateBottomPadding() + calculateBottomPadding(),
    )
}

private const val TAG = "ReadPage"