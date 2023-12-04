package com.eynnzerr.yuukatalk.ui.page.character

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

}