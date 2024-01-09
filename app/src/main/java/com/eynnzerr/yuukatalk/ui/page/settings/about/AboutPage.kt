package com.eynnzerr.yuukatalk.ui.page.settings.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.SettingsItem
import com.eynnzerr.yuukatalk.ui.component.SettingGroupItem
import com.eynnzerr.yuukatalk.ui.component.dialog.UpdateDialog
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation
import com.eynnzerr.yuukatalk.utils.VersionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    viewModel: AboutViewModel,
    navHostController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val versionName = VersionUtils.getLocalVersion()
    val settingsItems = listOf(
        SettingsItem(
            title = stringResource(id = R.string.version),
            desc = versionName,
            painter = painterResource(id = R.drawable.ic_info),
            onClick = {
                viewModel.checkVersion()
            }
        ),
        SettingsItem(
            title = stringResource(id = R.string.author),
            desc = stringResource(id = R.string.eynnzerr),
            painter = painterResource(id = R.drawable.ic_personal),
            onClick = {
                startBrowser("http://eynnzerr.top", context)
            }
        ),
        SettingsItem(
            title = stringResource(id = R.string.github),
            desc = stringResource(id = R.string.github_url),
            painter = painterResource(id = R.drawable.ic_github),
            onClick = {
                startBrowser("https://github.com/Eynnzerr/YuukaTalk", context)
            }
        ),
        SettingsItem(
            title = stringResource(id = R.string.libraries),
            desc = stringResource(id = R.string.libraries_description),
            painter = painterResource(id = R.drawable.ic_library),
            onClick = {
                // TODO open dialog to show used 3-rd libraries and credits
            }
        ),
    )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    if (uiState.showUpdate) {
        UpdateDialog(
            onDismissRequest = {
                viewModel.disableShowingUpdate()
            },
            onConfirm = {
                viewModel.disableShowingUpdate()
                startBrowser(uiState.newVersionUrl, context)
            },
            onDismiss = {
                viewModel.disableShowingUpdate()
                viewModel.addLatestVersionToIgnore()
            },
            title = uiState.newVersion,
            content = uiState.updateContent,
        )
    }

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
                title = { Text(stringResource(id = R.string.about)) },
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
                    painter = it.painter,
                    onClick = it.onClick
                )
            }
        }
    }
}

private fun startBrowser(url: String, context: Context) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}