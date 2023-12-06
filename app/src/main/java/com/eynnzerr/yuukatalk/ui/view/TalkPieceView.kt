package com.eynnzerr.yuukatalk.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AbstractComposeView
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.ui.component.TalkPiece
import com.eynnzerr.yuukatalk.ui.component.dialog.NarrationDialog

class TalkPieceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    var talkData: Talk by mutableStateOf(Talk.Narration(""))

    @Composable
    override fun Content() {
        Row {
            TalkPiece(talkData = talkData)
        }
    }
}