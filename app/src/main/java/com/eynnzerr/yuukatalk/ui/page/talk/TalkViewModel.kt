package com.eynnzerr.yuukatalk.ui.page.talk

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.yuukatalk.data.database.AppRepository
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.Sensei
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.data.model.TalkProject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class TalkUiState(
    val chatName: String,
    val text: String,
    val talkList: List<Talk>,
    val studentList: List<Character>,
    val currentStudent: Character,
    val isFirstTalking: Boolean,
    val isMoreToolsOpen: Boolean,
    val searchText: String,
    val narrationText: String,
    val textBranches: List<String>,
    val talkListState: TalkListState, // operate with recyclerView change
    val allStudents: List<Character>,
    val isEdited: Boolean,
)

sealed class TalkListState(type: Int) {
    class Initialized: TalkListState(type = 0)
    class Refresh: TalkListState(type = 1)
    class Push: TalkListState(type = 2)
    class Pop: TalkListState(type = 3)
}

@HiltViewModel
class TalkViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val talkList = mutableListOf<Talk>()
    private val studentList = mutableListOf(
        Sensei,
        Character(
            name = "白子",
            nameRoma = "Shiroko",
            school = "Abydos",
            avatarPath = "abydos/shiroko/avatar",
            emojiPath = "abydos/shiroko/emoji",
        ),
        Character(
            name = "星野",
            nameRoma = "Hoshino",
            school = "Abydos",
            avatarPath = "abydos/hoshino/avatar",
            emojiPath = "abydos/hoshino/emoji"
        ),
        Character(
            name = "野宫",
            nameRoma = "Nonomi",
            school = "Abydos",
            avatarPath = "abydos/nonomi/avatar",
            emojiPath = "abydos/nonomi/emoji"
        ),
        Character(
            name = "芹香",
            nameRoma = "Serika",
            school = "Abydos",
            avatarPath = "abydos/serika/avatar",
            emojiPath = "abydos/serika/emoji"
        ),
        Character(
            name = "绫音",
            nameRoma = "Ayane",
            school = "Abydos",
            avatarPath = "abydos/ayane/avatar",
            emojiPath = "abydos/ayane/emoji",
        ),
    )
    private val branchArray
        get() = _uiState.value.textBranches.toTypedArray()

    private val _uiState = MutableStateFlow(
        TalkUiState(
            chatName = "MomoTalk",
            text = "",
            talkList = talkList,
            studentList = studentList,
            currentStudent = studentList[0],
            isFirstTalking = true,
            isMoreToolsOpen = false,
            searchText = "",
            narrationText = "",
            textBranches = listOf(""),
            talkListState = TalkListState.Initialized(),
            allStudents = emptyList(),
            isEdited = false
        )
    )
    val uiState = _uiState.asStateFlow()

    private var projectId = -1

    fun setProjectId(id: Int) {
        projectId = id
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchAllCharacters().collect { characters ->
                _uiState.update { it.copy(allStudents = characters) }
            }
        }
    }

    fun loadHistory() {
        if (isHistoryTalk()) {
            // means we now enter into a history talk.
            viewModelScope.launch {
                val historyState = withContext(Dispatchers.IO) {
                    repository.fetchProjectById(projectId)
                }
                talkList.addAll(historyState.talkHistory)
                _uiState.update {
                    it.copy(
                        chatName = historyState.name,
                        talkList = historyState.talkHistory,
                        studentList = historyState.studentList,
                        currentStudent = historyState.currentStudent,
                        isFirstTalking = historyState.isFirstTalking,
                        talkListState = TalkListState.Refresh(),
                    )
                }
            }
        }
    }

    fun updateText(newText: String) = _uiState.update { it.copy(text = newText) }

    fun updateNarrationText(newText: String) = _uiState.update { it.copy(narrationText = newText) }

    fun updateChatName(newText: String) = _uiState.update { it.copy(chatName = newText) }

    fun appendBranch() {
        val newList = listOf(*branchArray, "")
        _uiState.update {
            it.copy(textBranches = newList)
        }
    }

    fun removeBranch() {
        val newList = mutableListOf(*branchArray).apply { removeLastOrNull() }
        _uiState.update { it.copy(textBranches = newList) }
    }

    fun editBranchAtIndex(newText: String, index: Int) {
        if (index > branchArray.lastIndex) return
        val newList = mutableListOf(*branchArray).apply { this[index] = newText }
        _uiState.update { it.copy(textBranches = newList) }
    }

    fun sendPureText() {
        val newPureText = Talk.PureText(
            talker = _uiState.value.currentStudent,
            text = _uiState.value.text,
            isFirst = _uiState.value.isFirstTalking
        )
        talkList.add(newPureText)

        _uiState.update {
            it.copy(
                text = "",
                isFirstTalking = false,
                talkListState = TalkListState.Push(),
                isEdited = true
            )
        }
    }

    fun sendPhoto(uri: String) {
        val newPhoto = Talk.Photo(
            talker = _uiState.value.currentStudent,
            uri = uri,
            isFirst = _uiState.value.isFirstTalking
        )
        talkList.add(newPhoto)

        _uiState.update {
            it.copy(
                isFirstTalking = false,
                talkListState = TalkListState.Push(),
                isEdited = true
            )
        }
    }

    fun sendLoveScene() {
        val newLoveScene = Talk.LoveScene(
            studentName = _uiState.value.currentStudent.name
        )
        talkList.add(newLoveScene)

        _uiState.update {
            it.copy(
                isFirstTalking = true,
                talkListState = TalkListState.Push(),
                isEdited = true
            )
        }
    }

    fun sendNarration() {
        val newNarration = Talk.Narration(
            text = _uiState.value.narrationText
        )
        talkList.add(newNarration)

        _uiState.update {
            it.copy(
                isFirstTalking = true,
                narrationText = "",
                talkListState = TalkListState.Push(),
                isEdited = true
            )
        }
    }

    fun sendBranches() {
        val newBranches = Talk.Branch(
            textOptions = _uiState.value.textBranches
        )
        talkList.add(newBranches)

        _uiState.update {
            it.copy(
                isFirstTalking = true,
                textBranches = listOf(""),
                talkListState = TalkListState.Push(),
                isEdited = true
            )
        }
    }

    fun updateToolVision() {
        _uiState.update { it.copy(isMoreToolsOpen = !it.isMoreToolsOpen) }
    }

    fun selectStudent(student: Character) {
        _uiState.update { it.copy(currentStudent = student, isFirstTalking = true) }
    }

    fun addStudent(student: Character) = studentList.add(student)

    fun removeStudent(student: Character) = studentList.remove(student)

    fun updateSearchText(newText: String) {
        _uiState.update {
            it.copy(
                searchText = newText
            )
        }
    }

    fun saveProject() {
        // 如果当前项目已存在则更新，没有则新建
        viewModelScope.launch(Dispatchers.IO) {
            if (projectId == -1) {
                val currentProject = TalkProject(
                    name = _uiState.value.chatName,
                    talkHistory = _uiState.value.talkList,
                    studentList = _uiState.value.studentList,
                    currentStudent = _uiState.value.currentStudent,
                    isFirstTalking = _uiState.value.isFirstTalking
                )
                repository.addProject(currentProject)
            } else {
                val currentProject = TalkProject(
                    id = projectId,
                    name = _uiState.value.chatName,
                    talkHistory = _uiState.value.talkList,
                    studentList = _uiState.value.studentList,
                    currentStudent = _uiState.value.currentStudent,
                    isFirstTalking = _uiState.value.isFirstTalking,
                )
                repository.updateProject(currentProject)
            }
        }
    }

    fun isHistoryTalk() = projectId >= 0

}

private const val TAG = "TalkViewModel"