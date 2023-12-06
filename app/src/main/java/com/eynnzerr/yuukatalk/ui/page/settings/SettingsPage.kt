package com.eynnzerr.yuukatalk.ui.page.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.SettingsItem
import com.eynnzerr.yuukatalk.ui.component.SettingGroupItem
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    navHostController: NavHostController
) {
    val settingsItems = listOf(
        SettingsItem(
            title = stringResource(id = R.string.editor_options),
            desc = stringResource(id = R.string.editor_options_desc),
            icon = Icons.Outlined.EditNote,
            onClick = {}
        ),
        SettingsItem(
            title = stringResource(id = R.string.appearance),
            desc = stringResource(id = R.string.appearance_desc),
            icon = Icons.Outlined.Palette,
            onClick = {}
        ),
        SettingsItem(
            title = stringResource(id = R.string.languages),
            desc = stringResource(id = R.string.languages_desc),
            icon = Icons.Outlined.Language,
            onClick = {}
        ),
        SettingsItem(
            title = stringResource(id = R.string.about),
            desc = stringResource(id = R.string.about_desc),
            icon = Icons.Outlined.Lightbulb,
            onClick = {}
        ),
    )

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
                title = { Text(stringResource(id = R.string.settings)) },
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
            items(settingsItems) {
                SettingGroupItem(
                    title = it.title,
                    desc = it.desc,
                    icon = it.icon,
                    onClick = it.onClick
                )
            }
        }
    }
}