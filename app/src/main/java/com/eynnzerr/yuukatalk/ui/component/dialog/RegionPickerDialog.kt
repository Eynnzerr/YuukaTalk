package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.outlined.VerticalSplit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.component.PlainButton

@Composable
fun RegionPickerDialog(
    onDismissRequest: () -> Unit,
    onDismiss: () -> Unit,
    onSetStart: () -> Unit,
    onSetEnd: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(id = R.string.btn_cancel))
            }
        },
        icon = {
            Icon(imageVector = Icons.Outlined.VerticalSplit, contentDescription = "")
        },
        title = {
            Text(text = stringResource(id = R.string.set_dialog_title))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PlainButton(
                    onClick = onSetStart,
                    imageVector = Icons.Filled.Start,
                    text = stringResource(id = R.string.set_as_start)
                )
                PlainButton(
                    onClick = onSetEnd,
                    imageVector = Icons.Filled.StopCircle,
                    text = stringResource(id = R.string.set_as_end)
                )
            }
        }
    )
}