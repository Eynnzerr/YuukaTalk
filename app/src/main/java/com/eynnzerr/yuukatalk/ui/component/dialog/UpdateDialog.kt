package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.eynnzerr.yuukatalk.R

@Composable
fun UpdateDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    content: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(stringResource(id = R.string.confirm_update))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(id = R.string.cancel_update))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Update,
                contentDescription = "new update dialog"
            )
        },
        title = {
            Text(text = title)
        },
        text = {
            MarkdownText(markdown = content)
        }
    )
}