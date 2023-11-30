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

    // fake data for testing
    private val character = Character(
        name = "Shiroko",
        school = "Abydios",
        avatarPath = "file:///android_asset/shiroko/emoji_0.png",
    )
    private val talkList = mutableListOf<Talk>(
//        Talk.PureText(
//            talker = character,
//            isFirst = true,
//            text = "Say you never let me go."
//        ),
//        Talk.PureText(
//            talker = character,
//            isFirst = false,
//            text = "Deep in the bones I can feel you."
//        ),
//        Talk.Photo(
//            talker = character,
//            isFirst = false,
//            uri = "file:///android_asset/shiroko/emoji_3.png"
//        ),
//        Talk.Narration(
//            text = "Take me back to the time."
//        ),
//        Talk.PureText(
//            talker = Character.Sensei,
//            isFirst = false,
//            text = "We can waste a night with an old film."
//        ),
//        Talk.PureText(
//            talker = Character.Sensei,
//            isFirst = false,
//            text = "Smoke that little weed in the couch in my bedroom."
//        ),
    )
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
}