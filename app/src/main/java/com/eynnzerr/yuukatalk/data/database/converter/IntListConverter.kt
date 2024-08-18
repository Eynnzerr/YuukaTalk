package com.eynnzerr.yuukatalk.data.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class IntListConverter {
    @TypeConverter
    fun listsToStr(lists: List<Int>) = Json.encodeToString(lists)

    @TypeConverter
    fun strToLists(json: String) = Json.decodeFromString<List<Int>>(json)
}