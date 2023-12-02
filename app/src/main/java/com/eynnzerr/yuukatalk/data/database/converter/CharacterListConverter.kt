package com.eynnzerr.yuukatalk.data.database.converter

import androidx.room.TypeConverter
import com.eynnzerr.yuukatalk.data.model.Character
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CharacterListConverter {
    @TypeConverter
    fun charactersToStr(characters: List<Character>) = Json.encodeToString(characters)

    @TypeConverter
    fun strToCharacters(json: String) = Json.decodeFromString<List<Character>>(json)
}