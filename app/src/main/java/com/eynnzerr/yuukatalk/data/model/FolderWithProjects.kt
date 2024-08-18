package com.eynnzerr.yuukatalk.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class FolderWithProjects(
    @Embedded val folder: TalkFolder = TalkFolder(),
    @Relation(
        parentColumn = "id",
        entityColumn = "folderId"
    )
    val projects: List<TalkProject> = emptyList()
)