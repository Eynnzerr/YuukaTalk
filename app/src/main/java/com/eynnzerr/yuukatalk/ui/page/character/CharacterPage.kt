package com.eynnzerr.yuukatalk.ui.page.character

import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.ui.component.StudentAvatar
import com.eynnzerr.yuukatalk.ui.component.StudentInfo
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterPage(
    viewModel: CharacterViewModel,
    navHostController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var openDIYDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .appBarScroll(true, scrollBehavior)
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(
                    elevation = 0.dp,
                    color = MaterialTheme.colorScheme.surface
                )
            ),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.characters) + "(${uiState.charactersList.size})") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.removeAllCharacters()
                            Toast.makeText(context, "清除全部学生数据……", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "clear students"
                        )
                    }
                    IconButton(
                        onClick = {
                            // re-import characters
                            viewModel.importCharactersFromFile()
                            Toast.makeText(context, "正在重新导入学生数据……", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "help"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.diy)) },
                icon = { Icon(
                    imageVector = Icons.Filled.DesignServices,
                    contentDescription = "diy characters"
                    )
                },
                onClick = {
                    openDIYDialog = true
                }
            )
        }
        ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            items(uiState.charactersList) { student ->
                StudentInfo(
                    student = student,
                    onPickAvatar = {},
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                // TODO 弹出对话框： 基于当前角色新建或删除该角色
                            }
                        )
                    }
                )
            }
        }
    }

    if (openDIYDialog) {
        var avatarUri by remember { mutableStateOf("") }
        val selectAvatar = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { photoUri ->
            photoUri?.let {
                val contentResolver = context.contentResolver
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)

                avatarUri = it.toString()
            }
        }
        var name by remember { mutableStateOf("") }
        var nameRoma by remember { mutableStateOf("") }
        var school by remember { mutableStateOf("") }
        var emojiUris by remember { mutableStateOf(listOf("")) }

        AlertDialog(
            onDismissRequest = {
                openDIYDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDIYDialog = false

                        val newCharacter = Character(
                            name = name,
                            nameRoma = nameRoma,
                            school = school,
                            avatarPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path + "/$name/avatar",
                            emojiPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path + "/$name/emoji",
                            isAsset = false
                        )
                        viewModel.addCustomCharacter(newCharacter, listOf(avatarUri), emojiUris)
                    },
                ) {
                    Text(stringResource(id = R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDIYDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.DesignServices,
                    contentDescription = "save reminder dialog icon"
                )
            },
            title = {
                Text(text = stringResource(id = R.string.title_diy_dialog))
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 头像（上传或使用已有）、名字、外文名（选填）、学院、表情包
                    // 提供在已有角色上进行修改功能
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        StudentAvatar(
                            url = avatarUri,
                            withBorder = true,
                            isSelected = true,
                            size = 64.dp,
                            onClick = { selectAvatar.launch(arrayOf("image/*")) }
                        )

                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = ""
                        )
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Name") },
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    OutlinedTextField(
                        value = nameRoma,
                        onValueChange = { nameRoma = it },
                        label = { Text(text = "English Name") },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = school,
                        onValueChange = { school = it },
                        label = { Text(text = "School") },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Text(text = "Emojis:")
                }
            }
        )
    }

}