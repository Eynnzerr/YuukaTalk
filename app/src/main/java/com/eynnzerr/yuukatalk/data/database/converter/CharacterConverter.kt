package com.eynnzerr.yuukatalk.data.database.converter

import androidx.room.TypeConverter
import com.eynnzerr.yuukatalk.data.model.Character
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CharacterConverter {
    @TypeConverter
    fun characterToStr(character: Character) = Json.encodeToString(character)

    @TypeConverter
    fun strToCharacter(json: String) = Json.decodeFromString<Character>(json)
}