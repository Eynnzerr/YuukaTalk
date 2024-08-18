package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.FolderWithProjects

@Composable
fun FolderDetailDialog(
    folderWithProject: FolderWithProjects,
    onDismissRequest: () -> Unit,
    onConfirm: (Boolean, String) -> Unit,
    onDismiss: () -> Unit,
) {
    val (folder, projects) = folderWithProject
    val (checkedState, onStateChange) = remember { mutableStateOf(false) }
    var titleText by remember { mutableStateOf(folder.name) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(checkedState, titleText) },
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
                imageVector = Icons.Filled.Folder,
                contentDescription = "folder information"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.title_folder_dialog))
        },
        text = {
            Column {
                Text("名称：${folder.name}")
                Text("创建日期：${folder.createdDate}")
                Text("项目数量：${projects.size}")
                OutlinedTextField(
                    modifier = Modifier.padding(top = 16.dp),
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text(text = stringResource(id = R.string.modify_folder_name)) },
                )
                Row(
                    Modifier.fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = checkedState,
                            onValueChange = { onStateChange(!checkedState) },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = "删除目录?",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Checkbox(
                        modifier = Modifier.padding(start = 8.dp),
                        checked = checkedState,
                        onCheckedChange = null
                    )
                }
            }
        }
    )
}