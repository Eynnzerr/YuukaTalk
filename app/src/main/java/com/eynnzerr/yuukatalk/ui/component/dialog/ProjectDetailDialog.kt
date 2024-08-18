package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.TalkProject

@Composable
fun ProjectDetailDialog(
    project: TalkProject,
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
                imageVector = Icons.Filled.Message,
                contentDescription = "project information"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.title_project_dialog))
        },
        text = {
            Column {
                Text("名称：${project.name}")
                Text("消息长度：${project.talkHistory.size}")
                Text("角色数量：${project.studentList.size}")
                Text("创建时间：${project.createdDate}")
                Text("修改时间：${project.modifiedDate}")
            }
        }
    )
}