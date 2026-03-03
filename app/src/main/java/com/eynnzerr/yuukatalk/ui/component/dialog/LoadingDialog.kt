package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.theme.YuukaTalkTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingDialog(
    titleText: String = "",
    onDismissRequest: () -> Unit,
    onCancel: (() -> Unit)? = null,
    progress: Float? = null,
    progressText: String = "",
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        confirmButton = {},
        dismissButton = {
            if (onCancel != null) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text(text = stringResource(id = R.string.btn_cancel))
                }
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Photo,
                contentDescription = ""
            )
        },
        title = {
            Text(text = titleText)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (progress == null) {
                    CircularProgressIndicator()
                } else {
                    LinearProgressIndicator(
                        progress = progress.coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${(progress.coerceIn(0f, 1f) * 100).toInt()}%")
                }
                if (progressText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = progressText)
                }
            }
        }
    )
}

@Preview(
    name = "loading dialog",
    showBackground = true
)
@Composable
private fun LoadingDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(10000)
        showDialog = false
    }

    YuukaTalkTheme {
        if (showDialog) {
            LoadingDialog(
                onDismissRequest = { showDialog = false }
            )
        }
    }
}
