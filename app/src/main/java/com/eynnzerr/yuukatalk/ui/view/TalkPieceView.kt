package com.eynnzerr.yuukatalk.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.ui.component.TalkPiece

class TalkPieceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    var talkData: Talk by mutableStateOf(Talk.Narration(""))
    var onLongClick by mutableStateOf({})
    var onClick by mutableStateOf({})

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        Row(
            modifier = Modifier.combinedClickable(
                enabled = true,
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
        ) {
            TalkPiece(talkData = talkData)
        }

        Log.d(TAG, "TalkPieceView recomposed! talkData is $talkData")
    }
}

private const val TAG = "TalkPieceView"