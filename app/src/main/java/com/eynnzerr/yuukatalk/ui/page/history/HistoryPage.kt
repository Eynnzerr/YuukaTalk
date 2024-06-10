package com.eynnzerr.yuukatalk.ui.page.history

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.common.Destinations
import com.eynnzerr.yuukatalk.ui.component.TalkHistoryCard
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.pushTo
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    viewModel: HistoryViewModel,
    navHostController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var openRemoveDialog by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var expandDropdown by remember { mutableStateOf(false) }

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
                },
                scrollBehavior = scrollBehavior
            )
        },

        ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            itemsIndexed(uiState.talkProjects) { index, item ->
                TalkHistoryCard(
                    history = item,
                    onClick = { navHostController.pushTo(Destinations.TALK_ROUTE + "/${item.id}") },
                    onLongCLick = {
                        selectedIndex = index
                        openRemoveDialog = true
                    }
                )
            }
        }
    }

    if (openRemoveDialog) {
        AlertDialog(
            onDismissRequest = {
                openRemoveDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeHistoryProject(uiState.talkProjects[selectedIndex])
                        openRemoveDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openRemoveDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "delete project"
                )
            },
            text = {
                Text(text = stringResource(id = R.string.title_remove_dialog))
            }
        )
    }
}

private const val TAG = "HistoryPage"