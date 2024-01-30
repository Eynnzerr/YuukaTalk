package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eynnzerr.yuukatalk.R

@Composable
fun UpdateCharacterDialog(
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
                imageVector = Icons.Filled.Update,
                contentDescription = "update characters"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.title_update_dialog))
        },
        text = {
            Text(text = stringResource(id = R.string.content_update_dialog))
        }
    )
}