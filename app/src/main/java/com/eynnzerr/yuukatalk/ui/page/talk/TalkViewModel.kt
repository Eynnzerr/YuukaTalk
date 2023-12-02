package com.eynnzerr.yuukatalk.ui.page.talk

import android.util.Log
import androidx.lifecycle.ViewModel
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.Talk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
)

@HiltViewModel
class TalkViewModel @Inject constructor(

) : ViewModel() {

    private val talkList = mutableListOf<Talk>()
    private val studentList = mutableListOf(
        Character.Sensei,
        Character(
            name = "Shiroko",
            school = "Abydos",
            avatarPath = "file:///android_asset/shiroko/emoji_0.png",
        ),
        Character(
            name = "Hoshino",
            school = "Abydos",
            avatarPath = "file:///android_asset/hoshino/emoji_0.webp",
        ),
        Character(
            name = "Nonomi",
            school = "Abydos",
            avatarPath = "file:///android_asset/nonomi/emoji_0.webp",
        ),
        Character(
            name = "Serika",
            school = "Abydos",
            avatarPath = "file:///android_asset/serika/emoji_0.webp",
        ),
        Character(
            name = "Ayane",
            school = "Abydos",
            avatarPath = "file:///android_asset/ayane/emoji_0.webp",
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
            textBranches = listOf("")
        )
    )
    val uiState = _uiState.asStateFlow()

    fun updateText(newText: String) = _uiState.update { it.copy(text = newText) }

    fun updateNarrationText(newText: String) = _uiState.update { it.copy(narrationText = newText) }

    fun appendBranch() {
        val newList = listOf(*branchArray, "")
        _uiState.update { it.copy(textBranches = newList) }
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
                isFirstTalking = false
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
                isFirstTalking = false
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
                isFirstTalking = true
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
                narrationText = ""
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
                textBranches = listOf("")
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

}

private const val TAG = "TalkViewModel"