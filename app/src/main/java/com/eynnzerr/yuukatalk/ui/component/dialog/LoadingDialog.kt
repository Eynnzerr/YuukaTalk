package com.eynnzerr.yuukatalk.ui.component.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        confirmButton = {},
        dismissButton = {},
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator()
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
            LoadingDialog { showDialog = false }
        }
    }
}
