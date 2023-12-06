package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eynnzerr.yuukatalk.R

@Composable
fun NarrationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit
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
                imageVector = Icons.Filled.GraphicEq,
                contentDescription = "narration dialog icon"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.title_narration_dialog))
        },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = stringResource(id = R.string.narration)) },
            )
        }
    )
}