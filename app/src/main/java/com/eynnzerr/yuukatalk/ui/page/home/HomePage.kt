package com.eynnzerr.yuukatalk.ui.page.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.PageEntryItem
import com.eynnzerr.yuukatalk.ui.common.Destinations
import com.eynnzerr.yuukatalk.ui.component.Banner
import com.eynnzerr.yuukatalk.ui.ext.pushTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavHostController
) {
    val entryItems = listOf(
        PageEntryItem(
            title = stringResource(id = R.string.new_entry_title),
            description = stringResource(id = R.string.new_entry_desc),
            icon = Icons.Filled.AddCircleOutline,
            color = MaterialTheme.colorScheme.primaryContainer,
            onClick = {
                // TODO open a dialog or bottom sheet or something
                navController.pushTo(Destinations.TALK_ROUTE)
            }
        ),
        PageEntryItem(
            title = stringResource(id = R.string.history_entry_title),
            description = stringResource(id = R.string.history_entry_desc),
            icon = Icons.Filled.History,
            color = MaterialTheme.colorScheme.surface,
            onClick = {
                navController.pushTo(Destinations.HISTORY_ROUTE)
            }
        ),
        PageEntryItem(
            title = stringResource(id = R.string.student_entry_title),
            description = stringResource(id = R.string.student_entry_desc),
            icon = Icons.Filled.PersonOutline,
            color = MaterialTheme.colorScheme.surface,
            onClick = {

            }
        ),
        PageEntryItem(
            title = stringResource(id = R.string.settings_entry_title),
            description = stringResource(id = R.string.settings_entry_desc),
            icon = Icons.Outlined.Settings,
            color = MaterialTheme.colorScheme.surface,
            onClick = {

            }
        )
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = {},
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = "help"
                        )
                    }
                }
            )
        },

    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            items(entryItems) { item ->
                Banner(
                    title =  item.title,
                    desc = item.description,
                    backgroundColor = item.color,
                    icon = item.icon,
                    onClick = item.onClick,
                    action = {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight,
                            contentDescription = "arrowRight",
                        )
                    },
                )
            }
        }
    }
}
