package com.eynnzerr.yuukatalk.ui.page.settings.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.component.Banner
import com.eynnzerr.yuukatalk.ui.component.SettingsRadioButton
import com.eynnzerr.yuukatalk.ui.component.TalkPreview
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewPage(
    viewModel: PreviewViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var openFontPicker by remember { mutableStateOf(false) }

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
                title = { Text(stringResource(id = R.string.font)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(scaffoldPadding)
        ) {
            item {
                Banner(
                    title = stringResource(id = R.string.current_font),
                    desc = uiState.fontName,
                    icon = Icons.Filled.TextFormat,
                    onClick = {
                        openFontPicker = true
                    }
                )
            }
            item {
                Text(
                    text = stringResource(id = R.string.preview),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
            }
            item {
                Divider()
            }
            item {
                TalkPreview()
            }
        }
    }

    if (openFontPicker) {
        AlertDialog(
            onDismissRequest = {
                openFontPicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openFontPicker = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.TextFormat,
                    contentDescription = "pick font"
                )
            },
            title = {
                Text(text = stringResource(id = R.string.title_font_picker_dialog))
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .selectableGroup()
                ) {
                    uiState.fontResources.forEach { (res, name) ->
                        item {
                            SettingsRadioButton(
                                title = name,
                                isSelected = res == uiState.currentFontResource,
                                onSelect = {
                                    viewModel.updateFontResource(res)
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}