package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eynnzerr.yuukatalk.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BranchDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    values: List<String>,
    onValueChange: (String, Int) -> Unit,
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
                imageVector = Icons.Filled.FormatListBulleted,
                contentDescription = "branch dialog icon"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.title_branch_dialog))
        },
        text = {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onAdd
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddCircleOutline,
                                contentDescription = "add branch."
                            )
                        }
                        IconButton(
                            onClick = onRemove
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RemoveCircleOutline,
                                contentDescription = "remove branch"
                            )
                        }
                    }
                }
                itemsIndexed(
                    items = values,
                    key = { index, _ -> index }
                ) { index, item ->
                    OutlinedTextField(
                        value = item,
                        onValueChange = { onValueChange(it, index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        singleLine = true,
                        prefix = { Text(text = "$index.") }
                    )
                }
            }
        }
    )
}