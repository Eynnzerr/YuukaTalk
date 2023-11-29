package com.eynnzerr.yuukatalk.ui.view

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eynnzerr.yuukatalk.data.model.Talk

class TalkAdapter(
    private val talkList: List<Talk>
): RecyclerView.Adapter<TalkAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var index = 0
    }

    var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TalkPieceView(parent.context)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = talkList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.index = position
        val talkPieceView = holder.itemView as TalkPieceView
        talkPieceView.talkData = talkList[position]
    }

    fun notifyAppendItem() {
        notifyItemInserted(talkList.lastIndex)
        layoutManager?.scrollToPosition(talkList.lastIndex)
    }
}

private const val TAG = "TalkAdapter"