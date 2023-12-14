package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.component.Guidance

@Composable
fun GuidanceDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(stringResource(id = R.string.btn_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(id = R.string.btn_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Help,
                contentDescription = "help guidance"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.title_guidance_dialog))
        },
        text = {
            Column {
                Guidance(
                    icon = Icons.Outlined.AddCircleOutline,
                    desc = stringResource(id = R.string.desc_guidance_new),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Guidance(
                    icon = Icons.Filled.History,
                    desc = stringResource(id = R.string.desc_guidance_history),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Guidance(
                    icon = Icons.Filled.PersonOutline,
                    desc = stringResource(id = R.string.desc_guidance_character),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Guidance(
                    icon = Icons.Outlined.Settings,
                    desc = stringResource(id = R.string.desc_guidance_settings),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Guidance(
                    icon = Icons.Outlined.Info,
                    desc = stringResource(id = R.string.desc_guidance_more)
                )
            }
        }
    )
}