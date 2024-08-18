package com.eynnzerr.yuukatalk.ui.page.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eynnzerr.yuukatalk.data.AppRepository
import com.eynnzerr.yuukatalk.data.model.FolderWithProjects
import com.eynnzerr.yuukatalk.data.model.TalkFolder
import com.eynnzerr.yuukatalk.data.model.TalkProject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class HistoryUiState(
    val folders: List<FolderWithProjects>,
    val freeProjects: List<TalkProject>,
    val isLoading: Boolean,
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(
        HistoryUiState(
            folders = emptyList(),
            freeProjects = emptyList(),
            isLoading = true,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        fetchHistoryProjects()
    }

    private fun fetchHistoryProjects() {
        viewModelScope.launch(Dispatchers.IO) {
            val foldersFlow = repository.fetchAllFolders()
            val projectsFlow = repository.fetchAllProjects()
            foldersFlow.combine(projectsFlow) { folders, projects ->
                Pair(folders, projects)
            }.collect { (folders, projects) ->
                val foldersWithProjects = folders.map {
                    FolderWithProjects(
                        folder = it,
                        projects = projects.filter { project -> it.id == project.folderId }
                    )
                }
                val freeProjects = projects.filter { it.folderId == null }
                _uiState.update { it.copy(folders = foldersWithProjects, freeProjects = freeProjects) }
                if (_uiState.value.isLoading) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun removeHistoryProject(project: TalkProject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeProject(project)
        }
    }

    fun restoreHistoryProject(project: TalkProject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addProject(project)
        }
    }

    fun importHistoryFromJson(jsonString: String) {
        val project = Json.decodeFromString<TalkProject>(jsonString)
        viewModelScope.launch(Dispatchers.IO) {
            repository.addProject(project)
        }
    }

    fun importFromMoeTalk(jsonString: String) {

    }

    fun createNewFolderWithName(name: String) {
        val folder = TalkFolder(name = name)
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFolder(folder)
        }
    }

    fun removeFolder(folder: TalkFolder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFolder(folder)
            // TODO ROOM直接删除父表记录，关联子表记录外键虽被置空，但是flow不触发。故手动刷新
            val newFreeProjects = repository.fetchAllFreeProjects().first()
            _uiState.update { it.copy(freeProjects = newFreeProjects) }
        }
    }

    fun updateFolder(folder: TalkFolder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFolder(folder)
        }
    }

    fun cancelLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }
}

private const val TAG = "HistoryViewModel"