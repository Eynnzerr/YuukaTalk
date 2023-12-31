package com.eynnzerr.yuukatalk.ui.page.character

import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.ui.component.PlainButton
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
    var openCharacterDialog by remember { mutableStateOf(false) }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    // states of DIY students
    var name by remember { mutableStateOf("") }
    var nameRoma by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var avatarUris by remember { mutableStateOf(emptyList<String>()) }
    var emojiUris by remember { mutableStateOf(emptyList<String>()) }
    var isAsset by remember { mutableStateOf(false) }

    fun clearDIYState() {
        name = ""
        nameRoma = ""
        school = ""
        avatarUris = emptyList()
        emojiUris = emptyList()
        isAsset = false
    }

    val selectAvatars = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        for (uri in uris) {
            val contentResolver = context.contentResolver
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)
        }
        if (uris.isNotEmpty()) avatarUris = uris.map { it.toString() }
    }
    val selectEmojis = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        for (uri in uris) {
            val contentResolver = context.contentResolver
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)
        }
        emojiUris = emojiUris
            .toMutableList()
            .apply {
                addAll(uris.map { it.toString() })
            }
    }
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
                            Toast.makeText(
                                context,
                                context.resources.getText(R.string.toast_remove_all_characters),
                                Toast.LENGTH_SHORT
                            ).show()
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
                            Toast.makeText(
                                context,
                                context.resources.getText(R.string.toast_reload_all_characters),
                                Toast.LENGTH_SHORT
                            ).show()
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
                    isAsset = false
                    openDIYDialog = true
                }
            )
        }
        ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            itemsIndexed(uiState.charactersList) { index, student ->
                StudentInfo(
                    student = student,
                    onPickAvatar = {},
                    onLongPress = {
                        selectedIndex = index
                        openCharacterDialog = true
                    }
                )
            }
        }
    }

    if (openCharacterDialog) {
        AlertDialog(
            onDismissRequest = {
                openCharacterDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openCharacterDialog = false
                    },
                ) {
                    Text(stringResource(id = R.string.btn_cancel))
                }
            },
            icon = {
                Icon(imageVector = Icons.Filled.Grade, contentDescription = "")
            },
            title = {
                Text(text = stringResource(id = R.string.advanced_options))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ) {
                    // 选择基于当前角色DIY新角色，或删除当前角色
                    PlainButton(
                        onClick = {
                            openCharacterDialog = false

                            val currentCharacter = uiState.charactersList[selectedIndex]
                            // avatarUri = currentCharacter.currentAvatar
                            avatarUris = currentCharacter.getAvatarPaths(context)
                            name = currentCharacter.name
                            nameRoma = currentCharacter.nameRoma
                            school = currentCharacter.school
                            isAsset = currentCharacter.isAsset

                            openDIYDialog = true
                        },
                        imageVector = Icons.Filled.DesignServices,
                        text = stringResource(id = R.string.customize)
                    )
                    PlainButton(
                        onClick = {
                            viewModel.removeCharacter(uiState.charactersList[selectedIndex])
                            openCharacterDialog = false
                        },
                        imageVector = Icons.Filled.DeleteForever,
                        text = stringResource(id = R.string.delete)
                    )
                }
            }
        )
    }

    if (openDIYDialog) {
        AlertDialog(
            onDismissRequest = {
                openDIYDialog = false
                clearDIYState()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDIYDialog = false

                        val newCharacter = Character(
                            name = name,
                            nameRoma = nameRoma,
                            school = school,
                            avatarPath = if (isAsset) uiState.charactersList[selectedIndex].avatarPath else context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path + "/$name/avatar",
                            emojiPath = if (isAsset) uiState.charactersList[selectedIndex].emojiPath else context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path + "/$name/emoji",
                            isAsset = isAsset,
                        )

                        viewModel.addCustomCharacter(newCharacter, avatarUris, emojiUris)
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
                            url = avatarUris.firstOrNull() ?: "",
                            withBorder = true,
                            isSelected = true,
                            size = 64.dp,
                            onClick = { if (!isAsset) selectAvatars.launch(arrayOf("image/*")) }
                        )

                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "",
                        )
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = stringResource(id = R.string.name)) },
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    OutlinedTextField(
                        value = nameRoma,
                        onValueChange = { nameRoma = it },
                        label = { Text(text = stringResource(id = R.string.english_name)) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = school,
                        onValueChange = { school = it },
                        label = { Text(text = stringResource(id = R.string.school)) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.emojis),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxWidth(),
                        columns = GridCells.FixedSize(72.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            Card(
                                modifier = Modifier.size(72.dp),
                                onClick = {
                                    selectEmojis.launch(arrayOf("image/*"))
                                },
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AddCircleOutline,
                                        contentDescription = "add emoji"
                                    )
                                }
                            }
                        }
                        items(
                            emojiUris,
                            key = { it.hashCode() }
                        ) { emojiUri ->
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = ImageRequest
                                        .Builder(context)
                                        .data(emojiUri)
                                        .crossfade(true)
                                        .transformations(RoundedCornersTransformation())
                                        .build(),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "",
                                    modifier = Modifier.aspectRatio(1f),
                                )

                                IconButton(
                                    onClick = {
                                        emojiUris = emojiUris.toMutableList().apply { remove(emojiUri) }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.RemoveCircleOutline,
                                        contentDescription = "delete emoji",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }

}