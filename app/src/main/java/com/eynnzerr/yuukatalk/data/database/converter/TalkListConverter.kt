package com.eynnzerr.yuukatalk.data.database.converter

import androidx.room.TypeConverter
import com.eynnzerr.yuukatalk.data.model.Talk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TalkListConverter {
    @TypeConverter
    fun talksToStr(talks: List<Talk>) = Json.encodeToString(talks)

    @TypeConverter
    fun strToTalks(json: String) = Json.decodeFromString<List<Talk>>(json)
}