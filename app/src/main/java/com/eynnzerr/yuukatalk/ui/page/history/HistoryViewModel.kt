package com.eynnzerr.yuukatalk.ui.page.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.yuukatalk.data.database.AppRepository
import com.eynnzerr.yuukatalk.data.model.TalkProject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val talkProjects: List<TalkProject>
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(talkProjects = emptyList()))
    val uiState = _uiState.asStateFlow()

    init {
        fetchHistoryProjects()
    }

    private fun fetchHistoryProjects() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchAllProjects().collect { historyProjects ->
                _uiState.update { it.copy(talkProjects = historyProjects) }
            }
        }
    }

}