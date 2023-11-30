package com.eynnzerr.yuukatalk.ui.page.talk

import androidx.lifecycle.ViewModel
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.Talk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TalkUiState(
    val text: String,
    val talkList: List<Talk>,
    val studentList: List<Character>,
    val currentStudent: Character,
    val isFirstTalking: Boolean,
    val isMoreToolsOpen: Boolean,
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
            name = "Shiroko2",
            school = "Abydos",
            avatarPath = "file:///android_asset/shiroko/emoji_0.png",
        ),
        Character(
            name = "Shiroko3",
            school = "Abydos",
            avatarPath = "file:///android_asset/shiroko/emoji_0.png",
        ),
        Character(
            name = "Shiroko4",
            school = "Abydos",
            avatarPath = "file:///android_asset/shiroko/emoji_0.png",
        ),
        Character(
            name = "Shiroko5",
            school = "Abydos",
            avatarPath = "file:///android_asset/shiroko/emoji_0.png",
        ),
        Character(
            name = "Shiroko6",
            school = "Abydos",
            avatarPath = "file:///android_asset/shiroko/emoji_0.png",
        ),
    )
    private val _uiState = MutableStateFlow(
        TalkUiState(
            text = "",
            talkList = talkList,
            studentList = studentList,
            currentStudent = studentList[0],
            isFirstTalking = true,
            isMoreToolsOpen = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    fun updateText(newText: String) {
        _uiState.update { it.copy(text = newText) }
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
                talkList = talkList,
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
                talkList = talkList,
                isFirstTalking = false
            )
        }
    }

    fun updateToolVision() {
        _uiState.update { it.copy(isMoreToolsOpen = !it.isMoreToolsOpen) }
    }

    fun selectStudent(student: Character) {
        _uiState.update { it.copy(currentStudent = student, isFirstTalking = true) }
    }
}