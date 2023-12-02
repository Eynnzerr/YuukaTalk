package com.eynnzerr.yuukatalk.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.eynnzerr.yuukatalk.data.database.converter.CharacterListConverter
import com.eynnzerr.yuukatalk.data.database.converter.TalkListConverter

@Entity(tableName = "momotalk_project")
@TypeConverters(TalkListConverter::class, CharacterListConverter::class)
data class TalkProject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(defaultValue = "MomoTalk")
    val name: String = "MomoTalk",
    @ColumnInfo(defaultValue = "")
    val talkHistory: List<Talk> = emptyList(),
    @ColumnInfo
    val studentList: List<Character> = emptyList(),
)
