package com.eynnzerr.yuukatalk.ui.page.settings.appearance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.ui.component.ColorButton
import com.eynnzerr.yuukatalk.ui.component.SettingGroupItem
import com.eynnzerr.yuukatalk.ui.component.SettingsRadioButton
import com.eynnzerr.yuukatalk.ui.component.StudentAvatar
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation
import com.eynnzerr.yuukatalk.ui.ext.toHexString
import com.eynnzerr.yuukatalk.ui.theme.PaletteOption
import com.eynnzerr.yuukatalk.ui.theme.defaultColors
import com.eynnzerr.yuukatalk.utils.AppearanceUtils
import com.tencent.mmkv.MMKV

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearancePage(
    viewModel: AppearanceViewModel,
    navHostController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val paletteOptionItems = mapOf(
        PaletteOption.LIGHT to stringResource(id = R.string.option_light),
        PaletteOption.DARK to stringResource(id = R.string.option_dark),
        PaletteOption.FOLLOW_SYSTEM to stringResource(id = R.string.option_follow),
        PaletteOption.DYNAMIC to stringResource(id = R.string.option_dynamic),
        PaletteOption.SELF_ASSIGNED to stringResource(id = R.string.option_self)
    )
    // val backgroundColor = MaterialTheme.colorScheme.surface

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
                title = { Text(stringResource(id = R.string.appearance)) },
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
            modifier = Modifier
                .padding(scaffoldPadding)
                .selectableGroup()
        ) {
            paletteOptionItems.forEach { (paletteOption, title) ->
                item {
                    SettingsRadioButton(
                        title = title,
                        isSelected = paletteOption == uiState.currentOption,
                        onSelect = {
                            viewModel.updatePaletteOption(paletteOption)
                            // viewModel.updateExportBackground(backgroundColor.toHexString())
                        }
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = uiState.currentOption == PaletteOption.SELF_ASSIGNED,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    LazyRow (
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = defaultColors,
                            key = { it }
                        ) { color ->
                            ColorButton(
                                color = color,
                                selected = uiState.currentSeedColor == color,
                                onSelect = {
                                    viewModel.updateSeedColor(color)
                                    // viewModel.updateExportBackground(backgroundColor.toHexString())
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}