package com.eynnzerr.yuukatalk.ui.page.talk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.SaveAs
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.setPadding
import androidx.navigation.NavHostController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.SpecialPieceEntryItem
import com.eynnzerr.yuukatalk.ui.component.DenseTextField
import com.eynnzerr.yuukatalk.ui.component.SpecialPieceEntryButton
import com.eynnzerr.yuukatalk.ui.component.StudentAvatar
import com.eynnzerr.yuukatalk.ui.component.StudentInfo
import com.eynnzerr.yuukatalk.ui.component.StudentSearchBar
import com.eynnzerr.yuukatalk.ui.view.TalkAdapter
import com.eynnzerr.yuukatalk.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

@SuppressLint("NotifyDataSetChanged")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun TalkPage(
    viewModel: TalkViewModel,
    navHostController: NavHostController,
) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val imePadding = with(LocalDensity.current) { WindowInsets.ime.getBottom(this).toDp() }

    // interaction signal with AndroidView
    var screenshotTalk by remember { mutableStateOf(false) }

    // component states
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    var openNarrationDialog by rememberSaveable { mutableStateOf(false) }
    var openBranchDialog by rememberSaveable { mutableStateOf(false) }
    var openEmojiPickerDialog by rememberSaveable { mutableStateOf(false) }
    var openSaveDialog by rememberSaveable { mutableStateOf(false) }
    var openRemindSaveDialog by rememberSaveable { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { 2 }

    // Native RecyclerView adapter
    val talkAdapter = remember {
        TalkAdapter(uiState.talkList)
    }

    // ActivityResultContract
    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            val contentResolver = context.contentResolver
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(it, takeFlags)

            viewModel.sendPhoto(it.toString())
        }
    }
    
    // static ui data
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val textFieldSize = screenWidth - 56.dp - 112.dp - 25.dp

    val specialEntryItems = listOf(
        SpecialPieceEntryItem(
            title = stringResource(id = R.string.btn_narration),
            icon = Icons.Filled.GraphicEq,
            onClick = {
                openNarrationDialog = true
            }
        ),
        SpecialPieceEntryItem(
            title = stringResource(id = R.string.btn_branch),
            icon = Icons.Filled.FormatListBulleted,
            onClick = {
                openBranchDialog = true
            }
        ),
        SpecialPieceEntryItem(
            title = stringResource(id = R.string.btn_love),
            icon = Icons.Filled.Favorite,
            onClick = {
                viewModel.sendLoveScene()
            }
        ),
    )

    LaunchedEffect(uiState.talkListState) {
        when (uiState.talkListState) {
            is TalkListState.Initialized -> {
                // do nothing
            }
            is TalkListState.Push -> {
                talkAdapter.notifyAppendItem()
            }
            is TalkListState.Pop -> {
                talkAdapter.notifyRemoveItemAtLast()
            }
            is TalkListState.Refresh -> {
                talkAdapter.notifyDataSetChanged()
                talkAdapter.notifyScrollToLast()
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.loadHistory()
    }

    BackHandler {
        if (uiState.isEdited) {
            openRemindSaveDialog = true
        } else {
            navHostController.popBackStack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(screenWidth - 80.dp)
                    .fillMaxHeight()
            ) {
                StudentSearchBar(
                    textValue = uiState.searchText,
                    onTextChanged = { viewModel.updateSearchText(it) },
                    modifier = Modifier.padding(top = 12.dp)
                )
                LazyColumn {
                    items(uiState.allStudents) { student ->
                        StudentInfo(
                            student = student,
                            onClick = {
                                viewModel.addStudent(student = student)
                                Toast.makeText(context, "成功添加角色。", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                Surface {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(text = uiState.chatName)
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    if (uiState.isEdited) {
                                        openRemindSaveDialog = true
                                    } else {
                                        navHostController.popBackStack()
                                    }
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
                                    if (viewModel.isHistoryTalk()) {
                                        viewModel.saveProject()
                                        Toast.makeText(context, "project saved as ${uiState.chatName}", Toast.LENGTH_SHORT).show()
                                    } else {
                                        openSaveDialog = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SaveAs,
                                    contentDescription = "save as project"
                                )
                            }
                            IconButton(
                                onClick = { screenshotTalk = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FileDownload,
                                    contentDescription = "export as picture"
                                )
                            }
                        },
                    )
                }
            },
            bottomBar = {
                Column(
                    // modifier = Modifier.padding(bottom = imePadding)
                ) {
                    AnimatedVisibility(
                        visible = uiState.isMoreToolsOpen,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            specialEntryItems.forEach { 
                                SpecialPieceEntryButton(item = it)
                            }
                        }
                    }
                    BottomAppBar(
                        actions = {
                            StudentAvatar(
                                url = uiState.currentStudent.currentAvatar,
                                size = 48.dp,
                                withBorder = true,
                                onClick = { openBottomSheet = true },
                            )

                            IconButton(
                                onClick = {
                                    viewModel.updateToolVision()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AddCircleOutline,
                                    contentDescription = "more options",
                                    tint = if (uiState.isMoreToolsOpen) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                )
                            }

                            IconButton(
                                onClick = {
                                    selectPicture.launch(arrayOf("image/*"))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = "image selection",
                                )
                            }

                            DenseTextField(
                                modifier = Modifier.size(textFieldSize, 40.dp),
                                value = uiState.text,
                                onValueChange = { viewModel.updateText(it) },
                                placeholder = { Text(text = "enjoy!") },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        // open emoji picker for current student
                                        openEmojiPickerDialog = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.InsertEmoticon,
                                            contentDescription = "insert emoji",
                                        )
                                    }
                                },
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    if (uiState.text != "") {
                                        viewModel.sendPureText()
                                    }
                                },
                                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                Icon(Icons.Filled.Send, "Localized description")
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch { drawerState.open() }
                    }
                ) {
                    Icon(Icons.Filled.MenuOpen, "add student menu")
                }
            }
        ) { scaffoldPadding ->
                AndroidView(
                    factory = { context ->
                        // LazyColumn转bitmap时，不可见部分无法正确绘制出来，必须使用RecyclerView
                        // TODO 如果未来官方给出了解决方案，还是希望能够使用LazyColumn.
                        RecyclerView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )

                            layoutManager = LinearLayoutManager(context)
                            adapter = talkAdapter

                            // bind adapter and layoutManager
                            talkAdapter.layoutManager = layoutManager
                        }
                    },
                    modifier = Modifier
                        .padding(scaffoldPadding)
                        .padding(16.dp),
                    update = { view ->
                        if (screenshotTalk) {
                            scope.launch(Dispatchers.Main) {
                                screenshotTalk = false

                                val bitmap = ImageUtils.generateBitmap(view)
                                val imageUri = withContext(Dispatchers.IO) {
                                    ImageUtils.saveBitMapToDisk(bitmap, context)
                                }
                                // TODO bug: 偶现这里没切回主线程导致异常
                                Log.d(TAG, "TalkPage: current thread: ${Thread.currentThread().name}")
                                Toast.makeText(context, "saved to" + imageUri.path, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            windowInsets = BottomSheetDefaults.windowInsets
        ) {
            FlowRow(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.Start
                )
            ) {
                uiState.studentList.forEach {
                    StudentAvatar(
                        modifier = Modifier.padding(bottom = 8.dp),
                        url = it.currentAvatar,
                        isSelected = uiState.currentStudent == it,
                        size = 48.dp,
                        onClick = {
                            viewModel.selectStudent(it)
                        }
                    )
                }
            }
        }
    }

    if (openNarrationDialog) {
        AlertDialog(
            onDismissRequest = {
                openNarrationDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (uiState.narrationText != "") {
                            viewModel.sendNarration()
                        }
                        openNarrationDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openNarrationDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = "narration dialog icon"
                )
            },
            title = {
                Text(text = stringResource(id = R.string.title_narration_dialog))
            },
            text = {
                OutlinedTextField(
                    value = uiState.narrationText,
                    onValueChange = { viewModel.updateNarrationText(it) },
                    label = { Text(text = stringResource(id = R.string.narration)) },
                )
            }
        )
    }

    if (openBranchDialog) {
        AlertDialog(
            onDismissRequest = {
                openBranchDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.sendBranches()
                        openBranchDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openBranchDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.FormatListBulleted,
                    contentDescription = "branch dialog icon"
                )
            },
            title = {
                Text(text = stringResource(id = R.string.title_branch_dialog))
            },
            text = {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    stickyHeader {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.appendBranch()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AddCircleOutline,
                                    contentDescription = "add branch."
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.removeBranch()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RemoveCircleOutline,
                                    contentDescription = "remove branch"
                                )
                            }
                        }
                    }
                    itemsIndexed(
                        items = uiState.textBranches,
                        key = { index, _ -> index }
                    ) { index, item ->
                        OutlinedTextField(
                            value = item,
                            onValueChange = { viewModel.editBranchAtIndex(it, index) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            singleLine = true,
                            prefix = { Text(text = "$index.") }
                        )
                    }
                }
            }
        )
    }

    if (openEmojiPickerDialog) {
        AlertDialog(
            onDismissRequest = { openEmojiPickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openEmojiPickerDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.EmojiEmotions,
                    contentDescription = "emoji dialog icon"
                )
            },
            title = {
                Text(text = stringResource(id = R.string.title_emoji_dialog))
            },
            text = {
                Column {
                    Row(
                        Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                    HorizontalPager(state = pagerState) { page ->
                        val emojiData = if (page == 0) uiState.currentStudent.getEmojiPaths(context) else getCommonEmojiPaths(context)
                        LazyVerticalGrid(
                            modifier = Modifier
                                .heightIn(0.dp, 350.dp)
                                .graphicsLayer {
                                    val pageOffset = (
                                            (pagerState.currentPage - page) + pagerState
                                                .currentPageOffsetFraction
                                            ).absoluteValue

                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                },
                            columns = GridCells.Adaptive(minSize = 64.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(
                                items = emojiData,
                                key = { it.hashCode() }
                            ) { path ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    onClick = {
                                        viewModel.sendPhoto(path)
                                        openEmojiPickerDialog = false
                                    }
                                ) {
                                    AsyncImage(
                                        model = ImageRequest
                                            .Builder(context)
                                            .data(path)
                                            .crossfade(true)
                                            .build(),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .aspectRatio(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    if (openSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                openSaveDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveProject()
                        Toast.makeText(context, "project saved as ${uiState.chatName}", Toast.LENGTH_SHORT).show()
                        openSaveDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openSaveDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "save dialog icon"
                )
            },
            title = {
                Text(text = stringResource(id = R.string.title_save_dialog))
            },
            text = {
                OutlinedTextField(
                    value = uiState.chatName,
                    onValueChange = { viewModel.updateChatName(it) },
                    label = { Text(text = stringResource(id = R.string.name)) },
                )
            }
        )
    }

    if (openRemindSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                openRemindSaveDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openRemindSaveDialog = false
                        navHostController.popBackStack()
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openRemindSaveDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Undo,
                    contentDescription = "save reminder dialog icon"
                )
            },
            text = {
                Text(text = stringResource(id = R.string.title_save_reminder_dialog))
            }
        )
    }
}

private fun getCommonEmojiPaths(context: Context): List<String> {
    val assetManager = context.assets
    return assetManager.list("common_emojis")
        ?.map { "file:///android_asset/common_emojis/$it" }
        ?: emptyList()
}

private const val TAG = "TalkPage"