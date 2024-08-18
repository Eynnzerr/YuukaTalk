package com.eynnzerr.yuukatalk.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.eynnzerr.yuukatalk.data.database.converter.CharacterConverter
import com.eynnzerr.yuukatalk.data.database.converter.CharacterListConverter
import com.eynnzerr.yuukatalk.data.database.converter.TalkListConverter
import com.eynnzerr.yuukatalk.utils.TimeUtils
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "momotalk_project",
    foreignKeys = [
        ForeignKey(
            entity = TalkFolder::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
@TypeConverters(
    TalkListConverter::class,
    CharacterListConverter::class,
    CharacterConverter::class
)
data class TalkProject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "MomoTalk",
    val talkHistory: List<Talk> = emptyList(),
    val studentList: List<Character> = emptyList(),
    @ColumnInfo(defaultValue = "")
    val currentStudent: Character = Sensei,
    @ColumnInfo(defaultValue = "true")
    val isFirstTalking: Boolean = true,
    @ColumnInfo(defaultValue = "empty")
    var createdDate: String = TimeUtils.currentTime(),
    @ColumnInfo(defaultValue = "empty")
    var modifiedDate: String = TimeUtils.currentTime(),
    @ColumnInfo(index = true)
    val folderId: Int? = null
)
