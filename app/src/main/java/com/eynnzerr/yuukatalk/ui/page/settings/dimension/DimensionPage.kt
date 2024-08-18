package com.eynnzerr.yuukatalk.ui.page.settings.dimension

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.component.LazyTalkPreview
import com.eynnzerr.yuukatalk.ui.component.SettingDropDown
import com.eynnzerr.yuukatalk.ui.component.dialog.DimensionReminderDialog
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation
import com.eynnzerr.yuukatalk.utils.DimensionUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DimensionPage(
    onBack: () -> Unit
) {
    val dimensionState by DimensionUtils.dimensionState.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val (showHelp, setShowHelp) = remember { mutableStateOf(false) }

    BottomSheetScaffold(
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
                title = { Text(stringResource(id = R.string.dimensions)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        setShowHelp(true)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "info"
                        )
                    }
                    IconButton(onClick = {
                        scope.launch { scaffoldState.bottomSheetState.expand() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Preview,
                            contentDescription = "preview"
                        )
                    }
                    IconButton(onClick = {
                        DimensionUtils.resetDimensions()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.RestartAlt,
                            contentDescription = "reset"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            LazyTalkPreview()
        },
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(scaffoldPadding)
        ) {
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.avatar_size),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.avatarSize.value.toString(),
                    options = (10..100).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateAvatarSize(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.name_font_size),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.nameFontSize.value.toString(),
                    options = (5..40).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateNameFontSize(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.message_side_margin),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.textStartPadding.value.toString(),
                    options = (0..120).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateTextStartPadding(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.message_font_size),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.textFontSize.value.toString(),
                    options = (5..50).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateTextFontSize(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.message_vertical_margin),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.verticalMargin.value.toString(),
                    options = (0..30).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateVerticalMargin(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.image_width),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.photoWidth.value.toString(),
                    options = (30..300).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updatePhotoWidth(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.narration_side_margin),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.narrationPadding.value.toString(),
                    options = (5..40).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateNarrationPadding(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.narration_font_size),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.narrationFontSize.value.toString(),
                    options = (5..40).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateNarrationFontSize(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.branch_side_margin),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.branchStartPadding.value.toString(),
                    options = (0..120).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateBranchStartPadding(it)
                        }
                    }
                )
            }
            item {
                SettingDropDown(
                    title = stringResource(id = R.string.love_scene_side_margin),
                    modifier = Modifier.padding(bottom = 8.dp),
                    initialValue = dimensionState.loveSceneStartPadding.value.toString(),
                    options = (0..120).map { it.toString() },
                    onSelect = { newValue ->
                        newValue.toFloatOrNull()?.let {
                            DimensionUtils.updateLoveSceneStartPadding(it)
                        }
                    }
                )
            }
        }
    }

    if (showHelp) {
        DimensionReminderDialog(
            onConfirm = { setShowHelp(false) },
            onDismiss = { setShowHelp(false) },
            onDismissRequest = { setShowHelp(false) }
        )
    }
}