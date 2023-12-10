package com.eynnzerr.yuukatalk.ui.page.settings.editor_options

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.component.SettingGroupItem
import com.eynnzerr.yuukatalk.ui.component.SettingGroupSwitch
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorOptionsPage(
    navHostController: NavHostController,
    viewModel: EditorOptionsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var openAuthorNameDialog by remember { mutableStateOf(false) }

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
                title = { Text(stringResource(id = R.string.editor_options)) },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
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
                scrollBehavior = scrollBehavior
            )
        },
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier.padding(scaffoldPadding)
        ) {
            SettingGroupSwitch(
                title = stringResource(id = R.string.watermark),
                icon = Icons.Outlined.WaterDrop,
                desc = stringResource(id = R.string.watermark_desc),
                checked = uiState.enableWatermark,
                onSwitch = { viewModel.switchWatermark() }
            )
            AnimatedVisibility(
                visible = uiState.enableWatermark,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    SettingGroupItem(
                        title = stringResource(id = R.string.author_name),
                        desc = uiState.authorName,
                        icon = Icons.Filled.Abc,
                        onClick = {
                            openAuthorNameDialog = true
                        }
                    )
                    SettingGroupItem(
                        title = stringResource(id = R.string.watermark_position),
                        desc = stringResource(id = R.string.watermark_position_desc),
                        icon = Icons.Filled.Radar,
                        onClick = {
                            Toast.makeText(context, context.getText(R.string.to_be_implemented), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            SettingGroupItem(
                title = stringResource(id = R.string.font),
                desc = stringResource(id = R.string.font_desc),
                icon = Icons.Filled.TextFields,
                onClick = {
                    Toast.makeText(context, context.getText(R.string.to_be_implemented), Toast.LENGTH_SHORT).show()
                }
            )
            SettingGroupItem(
                title = stringResource(id = R.string.background),
                desc = stringResource(id = R.string.background_desc),
                icon = Icons.Filled.Gradient,
                onClick = {
                    // TODO 设置导出图片背景颜色
                }
            )
        }
    }

    if (openAuthorNameDialog) {
        AlertDialog(
            onDismissRequest = {
                openAuthorNameDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openAuthorNameDialog = false
                        viewModel.saveAuthorName()
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openAuthorNameDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Abc,
                    contentDescription = "author name dialog icon"
                )
            },
            title = {
                Text(text = stringResource(id = R.string.title_author_name_dialog))
            },
            text = {
                OutlinedTextField(
                    value = uiState.authorName,
                    onValueChange = { viewModel.updateAuthorName(it) },
                    label = { Text(text = stringResource(id = R.string.name)) },
                )
            }
        )
    }
}