package com.eynnzerr.yuukatalk.ui.page.history

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.FolderWithProjects
import com.eynnzerr.yuukatalk.data.model.TalkProject
import com.eynnzerr.yuukatalk.ui.common.Destinations
import com.eynnzerr.yuukatalk.ui.component.SwipeToDismissContainer
import com.eynnzerr.yuukatalk.ui.component.TalkFolderCard
import com.eynnzerr.yuukatalk.ui.component.TalkHistoryCard
import com.eynnzerr.yuukatalk.ui.component.dialog.FolderDetailDialog
import com.eynnzerr.yuukatalk.ui.component.dialog.FolderNewDialog
import com.eynnzerr.yuukatalk.ui.component.dialog.LoadingDialog
import com.eynnzerr.yuukatalk.ui.component.dialog.ProjectDetailDialog
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.pushTo
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryPage(
    viewModel: HistoryViewModel,
    navHostController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val (openNewDialog, setOpenNewDialog) = remember { mutableStateOf(false) }
    val (openFolderDialog, setOpenFolderDialog) = remember { mutableStateOf(false) }
    var openRemoveDialog by remember { mutableStateOf(false) }
    var selectedFolder by remember { mutableStateOf(FolderWithProjects()) }
    var selectedProject by remember { mutableStateOf(TalkProject()) }
    var expandDropdown by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var lastDeletedProject by remember { mutableStateOf<TalkProject?>(null) }

    val selectYuukaTalkSaveFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                contentResolver.openInputStream(it)?.let { inputStream ->
                    val json = inputStream.bufferedReader().use { reader -> reader.readText() }
                    viewModel.importHistoryFromJson(json)
                    inputStream.close()
                }
            } catch (e: Exception) {
                Toast.makeText(context, context.getText(R.string.toast_invalid_file), Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier
            .appBarScroll(true, scrollBehavior)
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(
                    elevation = 0.dp,
                    color = MaterialTheme.colorScheme.surface
                )
            ),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.history)) },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopStart)
                    ) {
                        IconButton(
                            onClick = {
                                expandDropdown = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DownloadForOffline,
                                contentDescription = "import file."
                            )
                        }
                        DropdownMenu(
                            expanded = expandDropdown,
                            onDismissRequest = { expandDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.import_yuuka_talk)) },
                                onClick = { selectYuukaTalkSaveFile.launch(arrayOf("application/json")) }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.import_moe_talk)) },
                                onClick = { Toast.makeText(context, "将在下一版本支持", Toast.LENGTH_SHORT).show() }
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            setOpenNewDialog(true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CreateNewFolder,
                            contentDescription = "create new folder."
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            items(
                uiState.folders
            ) { folder ->
                TalkFolderCard(
                    talkFolder = folder,
                    onLongCLick = {
                        selectedFolder = folder
                        setOpenFolderDialog(true)
                    },
                    onClickItem = { project -> navHostController.pushTo(Destinations.TALK_ROUTE + "/${project.id}") },
                    onLongClickItem = { project ->
                        selectedProject = project
                        openRemoveDialog = true
                    },
                    onDismissItem = { project ->
                        lastDeletedProject = project
                        viewModel.removeHistoryProject(project)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = context.getString(R.string.deleted_project),
                                actionLabel = context.getString(R.string.undo),
                                duration = SnackbarDuration.Long,
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                lastDeletedProject?.let { project ->
                                    viewModel.restoreHistoryProject(project)
                                }
                            }
                        }
                    }
                )
            }
            items(
                uiState.freeProjects,
                key = { item -> item.id }
            ) { item ->
                SwipeToDismissContainer(
                    modifier = Modifier.animateItemPlacement(),
                    onConfirmDismiss = {
                        lastDeletedProject = item
                        viewModel.removeHistoryProject(item)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = context.getString(R.string.deleted_project),
                                actionLabel = context.getString(R.string.undo),
                                duration = SnackbarDuration.Long
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                lastDeletedProject?.let { project ->
                                    viewModel.restoreHistoryProject(project)
                                }
                            }
                        }
                    }
                ) {
                    TalkHistoryCard(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        history = item,
                        onClick = { navHostController.pushTo(Destinations.TALK_ROUTE + "/${item.id}") },
                        onLongCLick = {
                            selectedProject = item
                            openRemoveDialog = true
                        }
                    )
                }
            }
        }
    }

    if (uiState.isLoading) {
        LoadingDialog(
            titleText = stringResource(R.string.loading)
        ) { viewModel.cancelLoading() }
    }

    if (openRemoveDialog) {
        ProjectDetailDialog(
            project = selectedProject,
            onDismissRequest = { openRemoveDialog = false },
            onDismiss = { openRemoveDialog = false },
            onConfirm = { openRemoveDialog = false }
        )
    }

    if (openFolderDialog) {
        FolderDetailDialog(
            folderWithProject = selectedFolder,
            onDismissRequest = { setOpenFolderDialog(false) },
            onDismiss = { setOpenFolderDialog(false) },
            onConfirm = { remove, newName ->
                setOpenFolderDialog(false)
                if (remove) {
                    viewModel.removeFolder(selectedFolder.folder)
                } else {
                    if (newName != selectedFolder.folder.name) {
                        viewModel.updateFolder(selectedFolder.folder.copy(name = newName))
                    }
                }
            },
        )
    }

    if (openNewDialog) {
        FolderNewDialog(
            onDismissRequest = { setOpenNewDialog(false) },
            onDismiss = { setOpenNewDialog(false) },
            onConfirm = { name ->
                viewModel.createNewFolderWithName(name)
                setOpenNewDialog(false)
            },
        )
    }
}

private const val TAG = "HistoryPage"