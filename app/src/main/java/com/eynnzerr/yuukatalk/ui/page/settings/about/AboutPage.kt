package com.eynnzerr.yuukatalk.ui.page.settings.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
fun AboutPage(
    navHostController: NavHostController,
) {
    val context = LocalContext.current

    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    val settingsItems = listOf(
        SettingsItem(
            title = stringResource(id = R.string.version),
            desc = versionName,
            painter = painterResource(id = R.drawable.ic_info),
            onClick = {
                Toast.makeText(context, context.getText(R.string.toast_already_newest), Toast.LENGTH_SHORT).show()
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