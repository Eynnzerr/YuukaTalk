package com.eynnzerr.yuukatalk.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eynnzerr.yuukatalk.utils.TimeUtils
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "momotalk_folder")
data class TalkFolder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "MomoTalkSeries",
    @ColumnInfo(defaultValue = "empty")
    var createdDate: String = TimeUtils.currentTime(),
)