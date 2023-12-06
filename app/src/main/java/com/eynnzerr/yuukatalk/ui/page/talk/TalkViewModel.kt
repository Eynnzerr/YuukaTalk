package com.eynnzerr.yuukatalk.ui.page.talk

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
import kotlinx.coroutines.flow.first
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
    val filteredStudents: List<Character>,
    val isEdited: Boolean,
)

sealed class TalkListState(type: Int) {
    class Initialized: TalkListState(type = 0)
    class Refresh: TalkListState(type = 1)
    class Push: TalkListState(type = 2)
    class Pop: TalkListState(type = 3)
    class Modified(val index: Int): TalkListState(type = 4)
    class Removed(val index: Int): TalkListState(type = 5)
    class ModifiedMultiple(val indexes: List<Int>): TalkListState(type = 6)
}

@HiltViewModel
class TalkViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val talkList = mutableListOf<Talk>()
    private val studentList = mutableListOf<Character>(Sensei)
    private val branchArray
        get() = _uiState.value.textBranches.toTypedArray()
    private lateinit var allStudents: List<Character>

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
            filteredStudents = emptyList(),
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
                allStudents = characters
                _uiState.update { it.copy(filteredStudents = characters) }
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
        val newList = mutableListOf(*branchArray).apply { if(size > 1) removeLastOrNull() }
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

    fun removeTalkPiece(index: Int) {
        talkList.removeAt(index)

        _uiState.update {
            it.copy(
                isEdited = true,
                talkListState = TalkListState.Removed(index)
            )
        }
    }

    fun editTalkHistory(talk: Talk, index: Int) {
        talkList[index] = talk
        // 考虑上下文
        when (talk) {
            is Talk.PureText -> {
                val changedIndexes = mutableListOf(index)
                // 如有上文，当前isFirstTalking为!= lastCharacter
                if (index > 0) {
                    val lastTalk = talkList[index - 1]
                    val lastTalker = if (lastTalk is Talk.PureText) lastTalk.talker else if (lastTalk is Talk.Photo) lastTalk.talker else null
                    lastTalker?.let {
                        talk.isFirst = talk.talker != it
                        changedIndexes.add(index - 1)
                    }
                }
                // 如有下文，顺便修改下文isFirstTalking为!= currentCharacter
                if (index < talkList.lastIndex) {
                    val nextTalk = talkList[index + 1]
                    if (nextTalk is Talk.PureText) {
                        nextTalk.isFirst = nextTalk.talker != talk.talker
                        changedIndexes.add(index + 1)
                    } else if (nextTalk is Talk.Photo) {
                        nextTalk.isFirst = nextTalk.talker != talk.talker
                        changedIndexes.add(index + 1)
                    }
                }  else {
                    // 若为修改末尾消息，判断下一条消息是否为学生首次
                    _uiState.update {
                        it.copy(isFirstTalking = talk.talker != _uiState.value.currentStudent)
                    }
                }
                _uiState.update {
                    it.copy(
                        isEdited = true,
                        talkListState = TalkListState.ModifiedMultiple(changedIndexes)
                    )
                }
            }

            is Talk.Photo -> {
                // 同PureText
                val changedIndexes = mutableListOf(index)
                // 如有上文，当前isFirstTalking为!= lastCharacter
                if (index > 0) {
                    val lastTalk = talkList[index - 1]
                    val lastTalker = if (lastTalk is Talk.PureText) lastTalk.talker else if (lastTalk is Talk.Photo) lastTalk.talker else null
                    lastTalker?.let {
                        talk.isFirst = talk.talker != it
                        changedIndexes.add(index - 1)
                    }
                }
                // 如有下文，顺便修改下文isFirstTalking为!= currentCharacter
                if (index < talkList.lastIndex) {
                    val nextTalk = talkList[index + 1]
                    if (nextTalk is Talk.PureText) {
                        nextTalk.isFirst = nextTalk.talker != talk.talker
                        changedIndexes.add(index + 1)
                    } else if (nextTalk is Talk.Photo) {
                        nextTalk.isFirst = nextTalk.talker != talk.talker
                        changedIndexes.add(index + 1)
                    }
                }  else {
                    // 若为修改末尾消息，判断下一条消息是否为学生首次
                    _uiState.update {
                        it.copy(isFirstTalking = talk.talker != _uiState.value.currentStudent)
                    }
                }
                _uiState.update {
                    it.copy(
                        isEdited = true,
                        talkListState = TalkListState.ModifiedMultiple(changedIndexes)
                    )
                }
            }

            else -> {
                // 非文本/图片题材，不涉及说话人，直接更新
                _uiState.update {
                    it.copy(
                        isEdited = true,
                        talkListState = TalkListState.Modified(index)
                    )
                }
            }
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
                searchText = newText,
                filteredStudents = allStudents.filter { student ->
                    student.name.contains(newText, ignoreCase = true) ||
                    student.nameRoma.contains(newText, ignoreCase = true) ||
                    student.school.contains(newText, ignoreCase = true)
                }
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
            _uiState.update { it.copy(isEdited = false)}
        }
    }

    fun isHistoryTalk() = projectId >= 0

}

private const val TAG = "TalkViewModel"