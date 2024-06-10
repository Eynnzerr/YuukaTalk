package com.eynnzerr.yuukatalk.ui.view

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eynnzerr.yuukatalk.data.model.Talk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TalkPieceEditState(
    val position: Int,
    val talkData: Talk,
    val openEditDialog: Boolean,
)

enum class AdapterWorkingState {
    InEditing,
    InSplit,
}

class TalkAdapter(
    private val talkList: List<Talk>
): RecyclerView.Adapter<TalkAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var index = 0
    }

    var layoutManager: RecyclerView.LayoutManager? = null
    var workingState = AdapterWorkingState.InEditing

    private val _talkPieceState = MutableStateFlow(
        TalkPieceEditState(
            position = 0,
            talkData = Talk.Narration(""),
            openEditDialog = false,
        )
    )
    val talkPieceState = _talkPieceState.asStateFlow()

    fun closeDialog() = _talkPieceState.update { it.copy(openEditDialog = false) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TalkPieceView(parent.context)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = talkList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.index = position
        val talkPieceView = holder.itemView as TalkPieceView
        val talkData = talkList[position]
        talkPieceView.talkData = talkData

        when (workingState) {
            AdapterWorkingState.InEditing -> {
                talkPieceView.onLongClick = {
                    Log.d(TAG, "long pressed. position: ${holder.bindingAdapterPosition}")
                    _talkPieceState.update {
                        it.copy(
                            position = holder.bindingAdapterPosition,
                            talkData = talkData,
                            openEditDialog = true
                        )
                    }
                }
            }
            AdapterWorkingState.InSplit -> {
                Log.d(TAG, "onBindViewHolder: is in split mode.")
                talkPieceView.onClick = {
                    Log.d(TAG, "pressed. Position: ${holder.bindingAdapterPosition}")
                    _talkPieceState.update {
                        it.copy(
                            position = holder.bindingAdapterPosition,
                            talkData = talkData,
                            openEditDialog = true
                        )
                    }
                }
            }
        }
    }

    fun notifyAppendItem() {
        Log.d(TAG, "notifyAppendItem: talklist size: ${talkList.size}")
        notifyItemInserted(talkList.lastIndex)
        notifyScrollToLast()
    }

    fun notifyRemoveItemAtLast() = notifyItemRemoved(talkList.lastIndex)

    fun notifyScrollToLast() = layoutManager?.scrollToPosition(talkList.lastIndex)

    fun getData() = talkList
}

private const val TAG = "TalkAdapter"