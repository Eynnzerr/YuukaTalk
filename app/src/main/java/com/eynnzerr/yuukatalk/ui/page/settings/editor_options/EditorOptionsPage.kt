package com.eynnzerr.yuukatalk.ui.page.settings.editor_options

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import com.eynnzerr.yuukatalk.ui.common.Destinations
import com.eynnzerr.yuukatalk.ui.component.Banner
import com.eynnzerr.yuukatalk.ui.component.SettingGroupItem
import com.eynnzerr.yuukatalk.ui.component.SettingGroupSwitch
import com.eynnzerr.yuukatalk.ui.component.SettingsRadioButton
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.pushTo
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation
import com.eynnzerr.yuukatalk.ui.ext.toHexLong
import com.eynnzerr.yuukatalk.ui.ext.toHexString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorOptionsPage(
    navHostController: NavHostController,
    viewModel: EditorOptionsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val background = MaterialTheme.colorScheme.surface.toHexString()

    var openAuthorNameDialog by remember { mutableStateOf(false) }
    var showBackgroundOption by remember { mutableStateOf(false) }
    var showImageQualitySlider by remember { mutableStateOf(false) }
    var showCompressFormatOption by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier = Modifier.padding(scaffoldPadding)
        ) {
            item {
                SettingGroupSwitch(
                    title = stringResource(id = R.string.watermark),
                    icon = Icons.Outlined.WaterDrop,
                    desc = stringResource(id = R.string.watermark_desc),
                    checked = uiState.enableWatermark,
                    onSwitch = { viewModel.switchWatermark() }
                )
            }
            item {
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
            }
            item {
                SettingGroupItem(
                    title = stringResource(id = R.string.quality),
                    desc = stringResource(id = R.string.quality_desc),
                    icon = Icons.Outlined.HighQuality,
                    onClick = {
                        showImageQualitySlider = !showImageQualitySlider
                    },
                    action = {
                        Icon(
                            imageVector = if (showImageQualitySlider) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowLeft,
                            contentDescription = ""
                        )
                    }
                )
            }
            item {
                AnimatedVisibility(
                    visible = showImageQualitySlider,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "0")
                            Text(text = "100")
                        }
                        Slider(
                            value = uiState.imageQuality.toFloat(),
                            onValueChange = { viewModel.updateImageQuality(it.toInt()) },
                            valueRange = 0f..100f,
                            onValueChangeFinished = { viewModel.encodeImageQuality() },
                        )
                    }
                }
            }
            item {
                SettingGroupItem(
                    title = stringResource(id = R.string.compress_format),
                    desc = stringResource(id = R.string.compress_format_desc),
                    icon = Icons.Filled.Compress,
                    onClick = {
                        showCompressFormatOption = !showCompressFormatOption
                    },
                    action = {
                        Icon(
                            imageVector = if (showCompressFormatOption) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowLeft,
                            contentDescription = ""
                        )
                    }
                )
            }
            item {
                AnimatedVisibility(
                    visible = showCompressFormatOption,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .selectableGroup()
                    ) {
                        // TODO convert to list
                        SettingsRadioButton(
                            title = stringResource(id = R.string.format_option_jpeg),
                            isSelected = uiState.compressFormatIndex == 0,
                            onSelect = { viewModel.updateCompressFormat(0) }
                        )
                        SettingsRadioButton(
                            title = stringResource(id = R.string.format_option_png),
                            isSelected = uiState.compressFormatIndex == 1,
                            onSelect = { viewModel.updateCompressFormat(1) }
                        )
                        SettingsRadioButton(
                            title = stringResource(id = R.string.format_option_webp_lossy),
                            isSelected = uiState.compressFormatIndex == 2,
                            onSelect = { viewModel.updateCompressFormat(2) }
                        )
                        SettingsRadioButton(
                            title = stringResource(id = R.string.format_option_webp_lossless),
                            isSelected = uiState.compressFormatIndex == 3,
                            onSelect = { viewModel.updateCompressFormat(3) }
                        )
                    }
                }
            }
            item {
                SettingGroupItem(
                    title = stringResource(id = R.string.font),
                    desc = stringResource(id = R.string.font_desc),
                    icon = Icons.Filled.TextFields,
                    onClick = {
                        navHostController.pushTo(Destinations.PREVIEW_ROUTE)
                    }
                )
            }
            item {
                SettingGroupItem(
                    title = stringResource(id = R.string.background),
                    desc = stringResource(id = R.string.background_desc),
                    icon = Icons.Filled.Gradient,
                    onClick = {
                        showBackgroundOption = !showBackgroundOption
                    },
                    action = {
                        Icon(
                            imageVector = if (showBackgroundOption) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowLeft,
                            contentDescription = ""
                        )
                    }
                )
            }
            item {
                AnimatedVisibility(
                    visible = showBackgroundOption,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .selectableGroup()
                    ) {
                        SettingsRadioButton(
                            title = stringResource(id = R.string.bg_option_classic),
                            isSelected = uiState.screenshotBackground == "#fff7e3",
                            onSelect = { viewModel.updateBackgroundColor("#fff7e3") }
                        )

                        SettingsRadioButton(
                            title = stringResource(id = R.string.bg_option_white),
                            isSelected = uiState.screenshotBackground == "#ffffff",
                            onSelect = { viewModel.updateBackgroundColor("#ffffff") }
                        )

                        SettingsRadioButton(
                            title = stringResource(id = R.string.bg_option_follow),
                            isSelected = uiState.screenshotBackground != "#ffffff" && uiState.screenshotBackground != "#fff7e3",
                            onSelect = { viewModel.updateBackgroundColor(background) }
                        )
                    }
                }
            }
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