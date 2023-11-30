package com.eynnzerr.yuukatalk.ui.page.talk

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.SpecialPieceEntryItem
import com.eynnzerr.yuukatalk.ui.component.DenseTextField
import com.eynnzerr.yuukatalk.ui.component.SpecialPieceEntryButton
import com.eynnzerr.yuukatalk.ui.component.StudentAvatar
import com.eynnzerr.yuukatalk.ui.ext.isScrolled
import com.eynnzerr.yuukatalk.ui.view.TalkAdapter
import com.eynnzerr.yuukatalk.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TalkPage(viewModel: TalkViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // interaction signal with AndroidView
    var saveTalk by remember { mutableStateOf(false) }

    // component states
    val talkListState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    // Native RecyclerView adapter
    val talkAdapter = remember {
        TalkAdapter(uiState.talkList)
    }

    // ActivityResultContract
    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.sendPhoto(it.toString())
            talkAdapter.notifyAppendItem()
        }
    }
    
    // static ui data
    val specialEntryItems = listOf(
        SpecialPieceEntryItem(
            title = stringResource(id = R.string.btn_narration),
            icon = Icons.Filled.GraphicEq,
            onClick = {
                
            }
        ),
        SpecialPieceEntryItem(
            title = stringResource(id = R.string.btn_branch),
            icon = Icons.Filled.FormatListBulleted,
            onClick = {

            }
        ),
        SpecialPieceEntryItem(
            title = stringResource(id = R.string.btn_love),
            icon = Icons.Filled.Favorite,
            onClick = {

            }
        ),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                }
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                Surface(
                    shadowElevation = if (talkListState.isScrolled) 8.dp else 0.dp
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(text = "MomoTalk")
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MenuOpen,
                                    contentDescription = "menu"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Outlined.HelpOutline,
                                    contentDescription = "help"
                                )
                            }
                        },
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        /*TODO open toolbox*/
                        saveTalk = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DoneAll,
                        contentDescription = "done"
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
                                url = uiState.currentStudent.avatarPath,
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
                                    selectPicture.launch("image/*")
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = "image selection",
                                )
                            }

                            DenseTextField(
                                modifier = Modifier.size(210.dp, 40.dp),
                                value = uiState.text,
                                onValueChange = { viewModel.updateText(it) },
                                placeholder = { Text(text = "enjoy momotalk!") },
                                trailingIcon = {
                                    IconButton(onClick = { /*TODO*/ }) {
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
                                        talkAdapter.notifyAppendItem() // TODO 从手动调用改为监听flow
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
            }
        ) { scaffoldPadding ->
                AndroidView(
                    factory = { context ->
                        // LazyColumn转bitmap时，不可见部分无法正确绘制出来，必须使用RecyclerView
                        // TODO 如果未来官方给出了解决方案，还是希望能够使用LazyColumn.
                        /*ComposeView(context).apply {
                            setContent {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned {
                                            val height = it.size.height
                                            Log.d(TAG, "TalkPage: list height: $height")
                                        },
                                    state = talkListState,
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    items(uiState.talkList) {
                                        TalkPiece(talkData = it)
                                    }
                                }
                            }
                        }*/
                        RecyclerView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            // setPadding(32, 16, 32, 16)

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
                        if (saveTalk) {
                            scope.launch(Dispatchers.Main) {
                                saveTalk = false

                                val bitmap = ImageUtils.generateBitmap(view)
                                val imageUri = withContext(Dispatchers.IO) {
                                    ImageUtils.saveBitMapToDisk(bitmap, context)
                                }
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
                        url = it.avatarPath,
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
}

private const val TAG = "TalkPage"