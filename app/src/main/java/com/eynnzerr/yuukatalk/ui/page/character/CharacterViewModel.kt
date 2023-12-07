package com.eynnzerr.yuukatalk.ui.page.character

import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import com.eynnzerr.yuukatalk.data.database.AppRepository
import com.eynnzerr.yuukatalk.data.model.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

data class CharacterUiState(
    val charactersList: List<Character>,
    val isImporting: Boolean,
)

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(
        CharacterUiState(
            charactersList = emptyList(),
            isImporting = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        fetchCharacters()
    }

    private fun fetchCharacters() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchAllCharacters().collect { characters ->
                _uiState.update { it.copy(charactersList = characters) }
            }
        }
    }

    fun importCharactersFromFile() {
        _uiState.update { it.copy(isImporting = true) }
        val assetManager = YuukaTalkApplication.context.assets
        viewModelScope.launch(Dispatchers.IO) {
            val json = assetManager.open("characters.json").bufferedReader().readText()
            val characters = Json.decodeFromString<Array<Character>>(json)
            repository.importCharacters(*characters)
            _uiState.update { it.copy(isImporting = false) }
        }
    }

    fun addCustomCharacter(
        character: Character,
        avatarUris: List<String>,
        emojiUris: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // 分别创建avatarPath和emojiPath目录，并从uri复制图片到其中。

            for (uri in avatarUris) {
                val contentResolver = YuukaTalkApplication.context.contentResolver
                contentResolver.openInputStream(Uri.parse(uri))?.let { srcInputStream ->
                    val avatarRoot = File(character.avatarPath).apply {
                        if (!exists()) mkdirs()
                    }
                    val dest = File(avatarRoot, getFileNameFromUri(Uri.parse(uri)) ?: File(uri).name).apply {
                        if (!exists()) createNewFile()
                    }
                    val outputStream = dest.outputStream()
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (srcInputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                    outputStream.close()
                    srcInputStream.close()
                }
            }
            for (uri in emojiUris) {
                // TODO()
            }
            character.currentAvatar = character.getAvatarPaths(YuukaTalkApplication.context).first()
            repository.importCharacters(character)
        }
    }

    fun removeAllCharacters() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeAllCharacters()
        }
    }

    fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null

        // 判断URI的scheme
        if (uri.scheme == "content") {
            // 通过ContentResolver获取文件名
            YuukaTalkApplication.context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex)
                    }
                }
            }
        }

        Log.d(TAG, "getFileNameFromUri: fileName: $fileName")
        return fileName
    }
}

private const val TAG = "CharacterViewModel"