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
    val currentStudent: Character,
    val isFirstTalking: Boolean,
    val isMoreToolsOpen: Boolean,
)

@HiltViewModel
class TalkViewModel @Inject constructor(

) : ViewModel() {
    private val talkList = mutableListOf<Talk>()
    private val _uiState = MutableStateFlow(
        TalkUiState(
            text = "",
            talkList = talkList,
            currentStudent = Character(
                name = "Shiroko",
                school = "Abydios",
                avatarPath = "file:///android_asset/shiroko/emoji_0.png",
            ),
            isFirstTalking = true,
            isMoreToolsOpen = false
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

    fun updateToolVision() {
        _uiState.update { it.copy(isMoreToolsOpen = !it.isMoreToolsOpen) }
    }
}