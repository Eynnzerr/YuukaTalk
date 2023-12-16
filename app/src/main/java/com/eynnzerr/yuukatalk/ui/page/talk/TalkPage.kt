package com.eynnzerr.yuukatalk.ui.page.talk

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.SaveAs
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.SpecialPieceEntryItem
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.ui.component.DenseTextField
import com.eynnzerr.yuukatalk.ui.component.PlainButton
import com.eynnzerr.yuukatalk.ui.component.SpecialPieceEntryButton
import com.eynnzerr.yuukatalk.ui.component.StudentAvatar
import com.eynnzerr.yuukatalk.ui.component.StudentInfo
import com.eynnzerr.yuukatalk.ui.component.StudentSearchBar
import com.eynnzerr.yuukatalk.ui.component.dialog.BranchDialog
import com.eynnzerr.yuukatalk.ui.component.dialog.EmojiPickerDialog
import com.eynnzerr.yuukatalk.ui.component.dialog.NarrationDialog
import com.eynnzerr.yuukatalk.ui.view.TalkAdapter
import com.eynnzerr.yuukatalk.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("NotifyDataSetChanged")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
)
@Composable
fun TalkPage(
    viewModel: TalkViewModel,
    navHostController: NavHostController,
) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // interaction signal with AndroidView
    var screenshotTalk by remember { mutableStateOf(false) }

    // component states
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val snackbarHostState = remember { SnackbarHostState() }
    var expandDropDown by remember { mutableStateOf(false) }
    var openClearDialog by rememberSaveable { mutableStateOf(false) }
    var openNarrationDialog by rememberSaveable { mutableStateOf(false) }
    var openBranchDialog by rememberSaveable { mutableStateOf(false) }
    var openEmojiPickerDialog by rememberSaveable { mutableStateOf(false) }
    var openSaveDialog by rememberSaveable { mutableStateOf(false) }
    var openRemindSaveDialog by rememberSaveable { mutableStateOf(false) }
    var openTalkPieceEditDialog by rememberSaveable { mutableStateOf(false) }
    var openTalkPieceInsertDialog by rememberSaveable { mutableStateOf(false) }
    var insertIndex by remember { mutableIntStateOf(0) }

    // Native RecyclerView adapter
    val talkAdapter = remember {
        TalkAdapter(uiState.talkList)
    }
    val talkPieceEditState by talkAdapter.talkPieceState.collectAsState()

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

    LaunchedEffect(uiState.talkListStateChange) {
        for (state in uiState.talkListStateChange) {
            when (state) {
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
                is TalkListState.Modified -> {
                    talkAdapter.notifyItemChanged(state.index)
                }
                is TalkListState.Removed -> {
                    talkAdapter.notifyItemRemoved(state.index)
                }
                is TalkListState.Inserted -> {
                    talkAdapter.notifyItemInserted(state.index)
                }
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
                    items(uiState.filteredStudents) { student ->
                        StudentInfo(
                            student = student,
                            onPickAvatar = {
                                if (student !in uiState.studentList) {
                                    viewModel.addStudent(student = student)
                                    Toast.makeText(context, "成功添加角色。", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Surface {
                    TopAppBar(
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
                                    openClearDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "clear all message"
                                )
                            }
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
                            Box(
                                modifier = Modifier
                                    .wrapContentSize(Alignment.TopStart)
                            ) {
                                IconButton(
                                    onClick = { expandDropDown = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.FileDownload,
                                        contentDescription = "export"
                                    )
                                }
                                DropdownMenu(
                                    expanded = expandDropDown,
                                    onDismissRequest = { expandDropDown = false },
                                ) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Outlined.Photo,
                                                contentDescription = "as picture"
                                            )
                                        },
                                        text = { Text(text = stringResource(id = R.string.export_as_pic)) },
                                        onClick = {
                                            expandDropDown = false
                                            screenshotTalk = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.UploadFile,
                                                contentDescription = "as json"
                                            )
                                        },
                                        text = { Text(text = stringResource(id = R.string.export_as_file)) },
                                        onClick = {
                                            expandDropDown = false
                                            viewModel.saveTalkAsJson()
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Successfully export talk as json file.")
                                            }
                                        }
                                    )
                                }
                            }
                        },
                    )
                }
            },
            bottomBar = {
                Column {
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
                                isSelected = true,
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
                                try {
                                    screenshotTalk = false
                                    val bitmap = ImageUtils.generateBitmap(view)
                                    val imageUri = withContext(Dispatchers.IO) {
                                        ImageUtils.saveBitMapToDisk(bitmap, context)
                                    }
                                    snackbarHostState.showSnackbar(context.getText(R.string.toast_export_project).toString() + " ${imageUri.path}")
                                } catch (e: Exception) {
                                    viewModel.updateText(e.toString() + "\n" + e.stackTraceToString())
                                    viewModel.sendPureText()
                                }
                            }
                        }
                    }
                )
        }
    }

    // dialog to edit single talk piece.
    if (talkPieceEditState.openEditDialog) {
        val radioOptions = listOf(
            stringResource(id = R.string.edit_piece),
            stringResource(id = R.string.remove_piece),
            stringResource(id = R.string.insert_piece),
        )
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

        AlertDialog(
            onDismissRequest = {
                talkAdapter.closeDialog()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        talkAdapter.closeDialog()
                        when (selectedOption) {
                            radioOptions[0] -> {
                                openTalkPieceEditDialog = true
                            }
                            radioOptions[1] -> {
                                viewModel.removeTalkPiece(talkPieceEditState.position)
                            }
                            else -> {
                                openTalkPieceInsertDialog = true
                                insertIndex = talkPieceEditState.position
                            }
                        }
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        talkAdapter.closeDialog()
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "edit talk history"
                )
            },
            text = {
                Column(Modifier.selectableGroup()) {
                    radioOptions.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        )
    }

    if (openTalkPieceEditDialog) {
        when (val talkData = talkPieceEditState.talkData) {
            is Talk.PureText -> {
                // 修改说话学生或文字 TODO 提取成组件
                var talkingStudent by remember { mutableStateOf(talkData.talker) }
                var text by remember { mutableStateOf(talkData.text) }
                AlertDialog(
                    onDismissRequest = {
                        openTalkPieceEditDialog = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val newPureText = Talk.PureText(
                                    talker = talkingStudent,
                                    text = text,
                                    isFirst = talkingStudent != talkData.talker // 考虑上文，如果修改了角色，认为是第一个；如果与上文角色相同，则又不是第一个
                                )
                                viewModel.editTalkHistory(newPureText, talkPieceEditState.position)
                                openTalkPieceEditDialog = false
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openTalkPieceEditDialog = false
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_cancel))
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Character:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 16.dp,
                                    alignment = Alignment.Start
                                ),
                            ) {
                                items(items = uiState.studentList) {
                                    StudentAvatar(
                                        url = it.currentAvatar,
                                        withBorder = talkingStudent == it,
                                        isSelected = talkingStudent == it,
                                        size = 48.dp,
                                        onClick = {
                                            talkingStudent = it
                                        }
                                    )
                                }
                            }
                            Text(
                                text = "Text:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            OutlinedTextField(
                                value = text,
                                onValueChange = { text = it },
                                label = { Text(text = "text") },
                                trailingIcon = {
                                    IconButton(onClick = { text = "" }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Cancel,
                                            contentDescription = "clear text"
                                        )
                                    }
                                }
                            )
                        }
                    }
                )
            }
            is Talk.Photo -> {
                // 选择：从相册读取或从表情包读取或删除 TODO 提取成组件
                var talkingStudent by remember { mutableStateOf(talkData.talker) }
                var uri by remember { mutableStateOf(talkData.uri) }
                var openSubDialog by remember { mutableStateOf(false) }

                val reselectPicture = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { photoUri ->
                    photoUri?.let {
                        val contentResolver = context.contentResolver
                        val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        contentResolver.takePersistableUriPermission(it, takeFlags)

                        uri = it.toString()
                    }
                }

                AlertDialog(
                    onDismissRequest = {
                        openTalkPieceEditDialog = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val newPhoto = Talk.Photo(
                                    talker = talkingStudent,
                                    uri = uri,
                                    isFirst = talkingStudent != talkData.talker
                                )
                                viewModel.editTalkHistory(newPhoto, talkPieceEditState.position)
                                openTalkPieceEditDialog = false
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openTalkPieceEditDialog = false
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_cancel))
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 16.dp,
                                    alignment = Alignment.Start
                                ),
                            ) {
                                items(items = uiState.studentList) {
                                    StudentAvatar(
                                        url = it.currentAvatar,
                                        withBorder = talkingStudent == it,
                                        isSelected = talkingStudent == it,
                                        size = 48.dp,
                                        onClick = {
                                            talkingStudent = it
                                        }
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PlainButton(
                                    onClick = { reselectPicture.launch(arrayOf("image/*")) },
                                    imageVector = Icons.Outlined.PhotoLibrary,
                                    text = stringResource(id = R.string.gallery)
                                )
                                PlainButton(
                                    onClick = { openSubDialog = true },
                                    imageVector = Icons.Outlined.EmojiEmotions,
                                    text = stringResource(id = R.string.emotions)
                                )
                            }
                            AsyncImage(
                                model = ImageRequest
                                    .Builder(context)
                                    .data(uri)
                                    .crossfade(true)
                                    .transformations(RoundedCornersTransformation())
                                    .build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = "",
                                modifier = Modifier.size(144.dp),
                            )
                        }
                    }
                )

                if (openSubDialog) {
                    EmojiPickerDialog(
                        character = talkingStudent,
                        onDismissRequest = { openSubDialog = false },
                        onDismiss = { openSubDialog = false },
                        onPickEmoji = { path ->
                            uri = path
                            openSubDialog = false
                        }
                    )
                }
            }
            is Talk.Narration -> {
                // 选择：修改文案或删除
                var narrationText by remember { mutableStateOf(talkData.text) }
                NarrationDialog(
                    onDismissRequest = {
                        openTalkPieceEditDialog = false
                    },
                    onConfirm = {
                        val newNarration = Talk.Narration(narrationText)
                        viewModel.editTalkHistory(newNarration, talkPieceEditState.position)
                        openTalkPieceEditDialog = false
                    },
                    onDismiss = {
                        openTalkPieceEditDialog = false
                    },
                    value = narrationText,
                    onValueChange = { narrationText = it }
                )
            }
            is Talk.Branch -> {
                var textBranches by remember { mutableStateOf(listOf(*talkData.textOptions.toTypedArray())) }
                BranchDialog(
                    onDismissRequest = {
                        openTalkPieceEditDialog = false
                    },
                    onConfirm = {
                        val newBranch = Talk.Branch(textBranches)
                        viewModel.editTalkHistory(newBranch, talkPieceEditState.position)
                        openTalkPieceEditDialog = false
                    },
                    onDismiss = {
                        openTalkPieceEditDialog = false
                    },
                    onAdd = {
                        textBranches = listOf(*textBranches.toTypedArray(), "")
                    },
                    onRemove = {
                        textBranches = mutableListOf(*textBranches.toTypedArray()).apply { if(size > 1) removeLastOrNull() }
                    },
                    values = textBranches,
                    onValueChange = { newText, index ->
                        textBranches = mutableListOf(*textBranches.toTypedArray()).apply { this[index] = newText }
                    }
                )
            }
            is Talk.LoveScene -> {
                // 列出当前对话中学生列表 TODO 也封成一个Dialog组件
                var loveName by remember { mutableStateOf(talkData.studentName) }
                AlertDialog(
                    onDismissRequest = {
                        openTalkPieceEditDialog = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val newLoveScene = Talk.LoveScene(loveName)
                                viewModel.editTalkHistory(newLoveScene, talkPieceEditState.position)
                                openTalkPieceEditDialog = false
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openTalkPieceEditDialog = false
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_cancel))
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Character: $loveName",
                                style = MaterialTheme.typography.titleMedium
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 16.dp,
                                    alignment = Alignment.Start
                                ),
                            ) {
                                items(items = uiState.studentList) {
                                    StudentAvatar(
                                        url = it.currentAvatar,
                                        withBorder = loveName == it.name,
                                        isSelected = loveName == it.name,
                                        size = 48.dp,
                                        onClick = {
                                            loveName = it.name
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    if (openTalkPieceInsertDialog) {
        // local dialog switches
        var openInsertIndex by remember { mutableIntStateOf(-1) }

        AlertDialog(
            onDismissRequest = {
                openTalkPieceInsertDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openTalkPieceInsertDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Text(text = "Choose insert type.")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ) {
                    // 选择插入文字、图片、旁白、分支、羁绊剧情
                    PlainButton(
                        onClick = { openInsertIndex = 0 },
                        imageVector = Icons.Outlined.TextFields,
                        text = "Text"
                    )
                    PlainButton(
                        onClick = { openInsertIndex = 1 },
                        imageVector = Icons.Outlined.Image,
                        text = "Image"
                    )
                    PlainButton(
                        onClick = { openInsertIndex = 2 },
                        imageVector = Icons.Outlined.GraphicEq,
                        text = "Narration"
                    )
                    PlainButton(
                        onClick = { openInsertIndex = 3 },
                        imageVector = Icons.Outlined.FormatListBulleted,
                        text = "Branch"
                    )
                    PlainButton(
                        onClick = {
                            viewModel.sendLoveScene(insertIndex)
                            openInsertIndex = -1
                            openTalkPieceInsertDialog = false
                        },
                        imageVector = Icons.Filled.Favorite,
                        text = "Love Scene"
                    )
                }
            }
        )

        when (openInsertIndex) {
            0 -> {
                AlertDialog(
                    onDismissRequest = {
                       openInsertIndex = -1
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.sendPureText(insertIndex)
                                openInsertIndex = -1
                                openTalkPieceInsertDialog = false
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openInsertIndex = -1
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_cancel))
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Character:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 16.dp,
                                    alignment = Alignment.Start
                                ),
                            ) {
                                items(items = uiState.studentList) {
                                    StudentAvatar(
                                        url = it.currentAvatar,
                                        withBorder = uiState.currentStudent == it,
                                        isSelected = uiState.currentStudent == it,
                                        size = 48.dp,
                                        onClick = {
                                            viewModel.selectStudent(it)
                                        }
                                    )
                                }
                            }
                            Text(
                                text = "Text:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            OutlinedTextField(
                                value = uiState.text,
                                onValueChange = { viewModel.updateText(it) },
                                label = { Text(text = "text") },
                                trailingIcon = {
                                    IconButton(onClick = { viewModel.updateText("") }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Cancel,
                                            contentDescription = "clear text"
                                        )
                                    }
                                }
                            )
                        }
                    }
                )
            }
            1 -> {
                var uri by remember { mutableStateOf(uiState.currentStudent.currentAvatar) }
                var openSubDialog by remember { mutableStateOf(false) }

                val reselectPicture = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { photoUri ->
                    photoUri?.let {
                        val contentResolver = context.contentResolver
                        val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        contentResolver.takePersistableUriPermission(it, takeFlags)

                        uri = it.toString()
                    }
                }

                AlertDialog(
                    onDismissRequest = {
                        openInsertIndex = -1
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.sendPhoto(uri, insertIndex)
                                openInsertIndex = -1
                                openTalkPieceInsertDialog = false
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openInsertIndex = -1
                            },
                        ) {
                            Text(stringResource(id = R.string.btn_cancel))
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 16.dp,
                                    alignment = Alignment.Start
                                ),
                            ) {
                                items(items = uiState.studentList) {
                                    StudentAvatar(
                                        url = it.currentAvatar,
                                        withBorder = uiState.currentStudent == it,
                                        isSelected = uiState.currentStudent == it,
                                        size = 48.dp,
                                        onClick = {
                                            viewModel.selectStudent(it)
                                        }
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PlainButton(
                                    onClick = { reselectPicture.launch(arrayOf("image/*")) },
                                    imageVector = Icons.Outlined.PhotoLibrary,
                                    text = stringResource(id = R.string.gallery)
                                )
                                PlainButton(
                                    onClick = { openSubDialog = true },
                                    imageVector = Icons.Outlined.EmojiEmotions,
                                    text = stringResource(id = R.string.emotions)
                                )
                            }
                            AsyncImage(
                                model = ImageRequest
                                    .Builder(context)
                                    .data(uri)
                                    .crossfade(true)
                                    .transformations(RoundedCornersTransformation())
                                    .build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = "",
                                modifier = Modifier.size(144.dp),
                            )
                        }
                    }
                )

                if (openSubDialog) {
                    EmojiPickerDialog(
                        character = uiState.currentStudent,
                        onDismissRequest = { openSubDialog = false },
                        onDismiss = { openSubDialog = false },
                        onPickEmoji = { path ->
                            uri = path
                            openSubDialog = false
                        }
                    )
                }
            }
            2 -> {
                NarrationDialog(
                    onDismissRequest = {
                        openInsertIndex = -1
                    },
                    onConfirm = {
                        viewModel.sendNarration(insertIndex)
                        openInsertIndex = -1
                        openTalkPieceInsertDialog = false
                    },
                    onDismiss = {
                        openInsertIndex = -1
                    },
                    value = uiState.narrationText,
                    onValueChange = {
                        viewModel.updateNarrationText(it)
                    }
                )
            }
            3 -> {
                BranchDialog(
                    onDismissRequest = {
                        openInsertIndex = -1
                    },
                    onConfirm = {
                        viewModel.sendBranches(insertIndex)
                        openInsertIndex = -1
                        openTalkPieceInsertDialog = false
                    },
                    onDismiss = {
                        openInsertIndex = -1
                    },
                    onAdd = {
                        viewModel.appendBranch()
                    },
                    onRemove = {
                        viewModel.removeBranch()
                    },
                    values = uiState.textBranches,
                    onValueChange = { newText, index ->
                        viewModel.editBranchAtIndex(newText, index)
                    }
                )
            }
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
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.studentList.forEach {
                    StudentAvatar(
                        url = it.currentAvatar,
                        withBorder = uiState.currentStudent == it,
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
        NarrationDialog(
            onDismissRequest = {
                openNarrationDialog = false
            },
            onConfirm = {
                if (uiState.narrationText != "") {
                    viewModel.sendNarration()
                }
                openNarrationDialog = false
            },
            onDismiss = {
                openNarrationDialog = false
            },
            value = uiState.narrationText,
            onValueChange = { viewModel.updateNarrationText(it) }
        )
    }

    if (openBranchDialog) {
        BranchDialog(
            onDismissRequest = {
                openBranchDialog = false
            },
            onConfirm = {
                viewModel.sendBranches()
                openBranchDialog = false
            },
            onDismiss = {
                openBranchDialog = false
            },
            onAdd = {
                viewModel.appendBranch()
            },
            onRemove = {
                viewModel.removeBranch()
            },
            values = uiState.textBranches,
            onValueChange = { newText, index ->
                viewModel.editBranchAtIndex(newText, index)
            }
        )
    }

    if (openEmojiPickerDialog) {
        EmojiPickerDialog(
            character = uiState.currentStudent,
            onDismissRequest = { openEmojiPickerDialog = false },
            onDismiss = { openEmojiPickerDialog = false },
            onPickEmoji = {uri ->
                viewModel.sendPhoto(uri)
                openEmojiPickerDialog = false
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
                        Toast.makeText(
                            context,
                            context.getText(R.string.toast_save_project).toString() + " ${uiState.chatName}",
                            Toast.LENGTH_SHORT
                        ).show()
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

    if (openClearDialog) {
        AlertDialog(
            onDismissRequest = {
                openClearDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearTalk()
                        openClearDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openClearDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "clear all messages"
                )
            },
            text = {
                Text(text = stringResource(id = R.string.title_clear_dialog))
            }
        )
    }
}

private const val TAG = "TalkPage"